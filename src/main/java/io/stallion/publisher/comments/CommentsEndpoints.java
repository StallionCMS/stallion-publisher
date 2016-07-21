/*
 * Copyright (c) 2015-2016 Stallion Software LLC
 *
 * This file is part of Stallion Publisher.
 *
 * Stallion Publisher is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International license.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International license
 * along with this program. If not, see <https://creativecommons.org/licenses/by-nc-sa/4.0/>.
 */

package io.stallion.publisher.comments;

import static io.stallion.utils.Literals.*;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import ac.simons.akismet.Akismet;
import ac.simons.akismet.AkismetComment;
import ac.simons.akismet.AkismetException;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.stallion.Context;
import io.stallion.dataAccess.filtering.FilterChain;
import io.stallion.dataAccess.filtering.Pager;
import io.stallion.exceptions.*;
import io.stallion.publisher.PublisherSettings;
import io.stallion.publisher.contacts.Contact;
import io.stallion.publisher.contacts.ContactsController;
import io.stallion.publisher.contacts.SubscriptionFrequency;
import io.stallion.requests.validators.SafeMerger;
import io.stallion.restfulEndpoints.EndpointResource;
import io.stallion.restfulEndpoints.MinRole;
import io.stallion.restfulEndpoints.ObjectParam;
import io.stallion.services.Log;
import io.stallion.settings.Settings;
import io.stallion.templating.TemplateRenderer;
import io.stallion.testing.Stubbing;
import io.stallion.users.Role;
import io.stallion.utils.DateUtils;
import io.stallion.utils.Literals;
import io.stallion.utils.json.JSON;
import io.stallion.utils.json.RestrictedViews;
import static io.stallion.utils.Literals.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.servlet.http.Cookie;
import javax.ws.rs.*;
import javax.ws.rs.NotFoundException;

@Produces("application/json")
public class CommentsEndpoints implements EndpointResource {

    @POST
    @Path("/comments/submit")
    @JsonView(RestrictedViews.Owner.class)
    @Produces("application/json")
    public CommentWrapper submitComment(
            @ObjectParam Comment rawComment) throws Exception {

        Comment comment = SafeMerger.with()
                .nonEmpty("bodyMarkdown", "authorDisplayName", "threadId", "parentPermalink", "parentTitle")
                .email("authorEmail")
                .optional("authorFirstName", "authorLastName", "authorWebSite", "captchaResponse", "threadSubscribe", "mentionSubscribe")
                .merge(rawComment);


        checkRECaptcha(comment);
        checkAkismet(comment);
        processAndSaveComment(comment);
        NewCommentEmailHandler.enqueue(comment);
        if (comment.isApproved()) {
            CommentsController.instance().postCommentApproved(comment);
        }
        Log.info("Submmited comment {0} {1}", comment.getAuthorEmail(), comment.getId());
        return CommentsController.instance().forId(comment.getId()).toWrapper(true);
    }

    private void checkRECaptcha(Comment comment) {
        try {
            Stubbing.checkExecuteStub(comment);
        } catch (Stubbing.StubbedOut stubbedOut) {
            return;
        }
        if (!StringUtils.isEmpty(PublisherSettings.getInstance().getReCaptchaSecret())) {
            Log.finer("Check captcha");
            String secret = PublisherSettings.getInstance().getReCaptchaSecret();
            HttpResponse<JsonNode> jsonResponse = null;
            try {
                jsonResponse = Unirest.get("https://www.google.com/recaptcha/api/siteverify")
                        .queryString("secret", secret)
                        .queryString("response", comment.getCaptchaResponse())
                        .queryString("remoteip", Context.getRequest().getActualIp())
                        .asJson();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            Boolean succeeded = jsonResponse.getBody().getObject().getBoolean("success");
            if (!succeeded) {
                throw new ClientException("Invalid captcha. Please enter the code again.", 422);
            }
        }

    }

    private void checkAkismet(Comment comment) {
        try {
            Stubbing.checkExecuteStub(comment);
        } catch (Stubbing.StubbedOut stubbedOut) {
            return;
        }
        if (StringUtils.isEmpty(PublisherSettings.getInstance().getAkismetKey())) {
            return;
        }
        Log.finer("Check akismet");
        HttpClient client = HttpClients.createDefault();
        Akismet akismet = new Akismet(client);
        AkismetComment ac = new AkismetComment();
        Log.info("User agent: {0} IP: {1}",
                Context.getRequest().getHeader("User-agent"),
                Context.getRequest().getRemoteAddr()
        );

        ac.setCommentAuthor(comment.getAuthorDisplayName());
        ac.setCommentAuthorEmail(comment.getAuthorEmail());
        ac.setCommentAuthorUrl(comment.getAuthorWebSite());
        ac.setCommentType("publisher");
        ac.setCommentContent(comment.getBodyHtml());
        ac.setPermalink(comment.getParentPermalink());
        ac.setUserAgent(Context.getRequest().getHeader("User-agent"));
        ac.setReferrer(Context.getRequest().getHeader("Referer"));
        ac.setUserIp(Context.getRequest().getActualIp());
        akismet.setApiKey(PublisherSettings.getInstance().getAkismetKey());
        boolean isSpam = false;
        try {
            isSpam = akismet.commentCheck(ac);
        } catch (AkismetException e) {
            throw new RuntimeException(e);
        }
        comment.setAkismetCheckedAt(mils());
        if (isSpam) {
            comment.setAkismetApproved(false);
            comment.setState(CommentState.AKISMET_SPAM);
        } else {
            comment.setAkismetApproved(true);
        }

    }

    public Comment processAndSaveComment(Comment comment) {
        if (PublisherSettings.getInstance().getModerationEnabled()) {
            if (comment.getAkismetApproved() || StringUtils.isEmpty(PublisherSettings.getInstance().getAkismetKey())) {
                comment.setState(CommentState.PENDING_MODERATION);
            }
        } else {
            if (comment.getAkismetApproved() ||
                    empty(PublisherSettings.getInstance().getAkismetKey())) {
                comment.setState(CommentState.APPROVED);
                comment.setApproved(true);
            }
        }

        if (comment.isApproved()) {
            comment.setPreviouslyApproved(true);
        }

        Cookie cookie = Context.getRequest().getCookie(Constants.AUTHOR_SECRET_COOKIE);
        if (cookie == null) {
            cookie = Context.getResponse().addCookie(
                    Constants.AUTHOR_SECRET_COOKIE,
                    RandomStringUtils.randomAlphanumeric(30),
                    20*365*60*60*24
            );
        }
        comment.setAuthorSecret(cookie.getValue());

        Log.info("Save comment from {0}", comment.getAuthorEmail());

        comment.setCreatedTicks(DateUtils.mils());

        Contact contact = new Contact();
        contact.setEmail(comment.getAuthorEmail());
        contact.setDisplayName(comment.getAuthorDisplayName());
        contact.setWebSite(comment.getAuthorWebSite());
        contact.setEverCookie(RandomStringUtils.randomAlphanumeric(30));
        if (Literals.empty(contact.getId())) {
            Context.getResponse().addCookie("stContactToken", contact.getEverCookie(),
                    20*365*60*60*24
            );
        }
        contact = ContactsController.instance().getOrCreate(contact);

        comment.setContactId(contact.getId());
        CommentsController.instance().save(comment);


        /* Subscribe the commenter to updates */
        CommentSubscriptionInfo subscriptionInfo = new CommentSubscriptionInfo();
        if (comment.getMentionSubscribe() == true) {
            subscriptionInfo.setReplyNotifyFrequency(SubscriptionFrequency.DAILY);
        } else {
            subscriptionInfo.setReplyNotifyFrequency(SubscriptionFrequency.NEVER);
        }
        if (comment.getThreadSubscribe() == true) {
            subscriptionInfo.setThreadNotifyFrequency(SubscriptionFrequency.DAILY);
        } else {
            subscriptionInfo.setThreadNotifyFrequency(SubscriptionFrequency.NEVER);
        }
        CommentsController.instance().updateCommentSubscriptionInfo(subscriptionInfo, comment, contact);

        return comment;
    }

    private void enqueueAdminEmail(Comment comment) {

    }


    @GET
    @Produces("text/xml")
    @Path("/comments/rss.xml")
    public String rss() {
        Pager<Comment> pager = CommentsController.instance()
                .filter("isApproved", "true")
                .sort("createdTicks", "desc")
                .pager(1);
        Map<String, Object> context = map();
        context.put("commentsPager", pager);
        URL url = getClass().getClassLoader().getResource("templates/comments-rss.jinja");
        return TemplateRenderer.instance().renderTemplate(url, context);
    }

    @GET
    @Path("/comments/dashboard")
    @Produces("text/html")
    @MinRole(Role.STAFF)
    public Object adminDashboard() throws Exception {
        URL url = getClass().getClassLoader().getResource("templates/comments-manage-dashboard.jinja");
        Context.getResponse().getMeta().setTitle("Comments Dashboard");
        return TemplateRenderer.instance().renderTemplate(url.toString());
    }

    @GET
    @Path("/comments/assets/:path")
    public String dashboardJsx(@PathParam("path") String path) throws IOException {
        if (StringUtils.countMatches(path, ".") > 1) {
            throw new UsageException("Invalid asset file path: " + path);
        }
        URL url = getClass().getResource("/assets/" + path);
        if (url == null) {
            throw new io.stallion.exceptions.NotFoundException("Missing file path: " + path);
        }
        if (Settings.instance().getDevMode() && url.toString().contains("/target/classes/")) {
            String newPath = url.toString().replace("/target/classes/", "/src/main/resources/");
            url = new URL(newPath);

        }
        if (path.endsWith(".css")) {
            Context.getResponse().setContentType("text/css");
        } else {
            Context.getResponse().setContentType("text/javascript");
        }
        return org.apache.commons.io.IOUtils.toString(url, UTF8);
    }

    @GET
    @Path("/comments/comment-subscriptions/")
    @Produces("text/html")
    public String commentSubscriptions(
            @QueryParam("commentId") Long commentId
    ) throws JsonProcessingException

    {
        Comment comment = CommentsController.instance().forIdOrNotFound(commentId);
        if (!comment.isEditable()) {
            throw new ClientException("You do not have permission to edit this comment", 403);
        }
        Contact contact = ContactsController.instance().forId(comment.getContactId());
        Map<String, Object> ctx = map();
        ctx.put("contact", contact);
        ctx.put("comment", comment);
        ctx.put("subscriptionInfo", CommentsController.instance().getCommentSubscriptionInfo(comment, contact));
        ctx.put("contextJson", JSON.stringify(ctx, RestrictedViews.Owner.class));



        URL url = getClass().getClassLoader().getResource("templates/comments-subscribe.jinja");
        Context.getResponse().getMeta().setTitle("Subscribe to emails?");
        return TemplateRenderer.instance().renderTemplate(url.toString(), ctx);
    }

    @POST
    @Path("/comments/update-comment-subscriptions")
    public CommentSubscriptionInfo updateCommentSubscriptions(@QueryParam("commentId") Long commentId,
                                                              @ObjectParam(targetClass = CommentSubscriptionInfo.class) CommentSubscriptionInfo subscriptionInfo) {
        Comment comment = CommentsController.instance().forIdOrNotFound(commentId);
        if (!comment.isEditable()) {
            throw new ClientException("You do not have permission to edit this comment", 403);
        }
        return updateCommentSubscriptions(comment, subscriptionInfo);
    }

    public CommentSubscriptionInfo updateCommentSubscriptions(Comment comment, CommentSubscriptionInfo subscriptionInfo) {
        Contact contact = ContactsController.instance().forId(comment.getContactId());
        subscriptionInfo = CommentsController.instance().updateCommentSubscriptionInfo(subscriptionInfo, comment, contact);
        return subscriptionInfo;

    }

    @POST
    @Path("/comments/:id/revise")
    public Object reviseComment(
            @PathParam("id") Long id,
            @ObjectParam Comment revisedComment) throws Exception {
        // Either, Auth level is staff or the author wrote the comment
        Comment existing = CommentsController.instance().forIdOrNotFound(id);
        if (!Context.getUser().isInRole(Role.STAFF) && !existing.isEditable()) {
            throw new ClientException("You do not have permission to edit this comment", 403);
        }
        revisedComment = SafeMerger.with()
                .optional("authorDisplayName", "authorFirstName", "authorLastName",
                        "authorWebSite", "bodyMarkdown"
                )
                .optionalEmail("authorEmail")
                .merge(revisedComment, existing);
        CommentsController.instance().save(revisedComment);
        return revisedComment.toWrapper();
    }

    @POST
    @MinRole(Role.STAFF)
    @Path("/comments/:id/delete")
    public Object deleteComment(@PathParam("id") Long id) {
        Comment cmt = CommentsController.instance().forIdOrNotFound(id);
        if (cmt.isApproved()) {
            cmt.setPreviouslyApproved(true);
        }
        cmt.setApproved(false);
        cmt.setModeratedAt(mils());
        cmt.setModeratorApproved(false);
        cmt.setState(CommentState.REJECTED);
        CommentsController.instance().softDelete(cmt);
        return true;
    }

    @POST
    @MinRole(Role.STAFF)
    @Path("/comments/:id/restore-and-approve")
    public Object restoreAndApproveComment(@PathParam("id") Long id) {
        Comment cmt = CommentsController.instance().forIdWithDeleted(id);
        if (cmt == null) {
            throw new io.stallion.exceptions.NotFoundException("Could not find comment with that id " + id);
        }
        cmt.setDeleted(false);
        cmt.setApproved(true);
        cmt.setModeratorApproved(true);
        cmt.setModeratedAt(mils());
        cmt.setState(CommentState.APPROVED);
        CommentsController.instance().save(cmt);

        if (!cmt.getPreviouslyApproved()) {
            CommentsController.instance().postCommentApproved(cmt);

            cmt.setPreviouslyApproved(true);
            CommentsController.instance().save(cmt);
        }


        return true;
    }



    @PUT
    @Path("/comments/:id/update")
    @MinRole(Role.STAFF)
    public Object updateComment(@PathParam("id") Object id, @ObjectParam(targetClass = Comment.class) String key) {
        // Either, Auth level is staff, or the cookie/secret matches
        return "comment updated";
    }


    @GET
    @Path("/comments/:id/view")
    @MinRole(Role.STAFF)
    @Produces("application/json")
    public Object viewComment(@PathParam("id") Long id) {
        Comment cmt = CommentsController.instance().forIdWithDeleted(id);
        if (cmt == null) {
            throw new io.stallion.exceptions.NotFoundException("Comment not found.");
        }
        return cmt.toWrapper();
    }

    @GET
    @Path("/comments/dashboard.json")
    @MinRole(Role.STAFF)
    @Produces("application/json")
    public Pager dashboardScreen(@QueryParam("deleted") Boolean deleted, @QueryParam("page") Integer page) {
        page = or(page, 1);
        DashboardScreen screen = new DashboardScreen();
        FilterChain<Comment> comments = CommentsController.instance().filterChain();
        Log.info("Deleted: {0}", deleted);
        if (deleted != null && true == deleted) {
            Log.info("Include deleted!");
            comments = comments.includeDeleted();
        }
        comments = comments.sort("createdTicks", "desc");

        return comments.pager(page, 50);
    }


    @GET
    @Path("/comments/find")
    public Object findComments() {

        return "find comments";
    }


    @GET
    @Path("/comments/list")
    @Produces("application/json")
    public Object getComments(@QueryParam("page") Integer page) {
        page = or(page, 1);
        Map ctx =  map(val("pager", CommentsController.instance().filterChain().exclude("deleted", true).sort("id", "desc").pager(page)));
        return ctx;
    }


}
