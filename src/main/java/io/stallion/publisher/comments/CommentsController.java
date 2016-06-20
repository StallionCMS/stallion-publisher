/*
 * Copyright (c) 2015-2016 Patrick Fitzsimmons
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

import io.stallion.Context;
import io.stallion.dataAccess.DataAccessRegistration;
import io.stallion.dataAccess.DataAccessRegistry;
import io.stallion.dataAccess.StandardModelController;
import io.stallion.dataAccess.file.JsonFilePersister;
import io.stallion.publisher.contacts.*;
import io.stallion.services.Log;
import io.stallion.utils.DateUtils;
import io.stallion.utils.Sanitize;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.stallion.utils.Literals.*;

public class CommentsController extends StandardModelController<Comment> {


    public static CommentsController instance() {
        return (CommentsController) DataAccessRegistry.instance().get("comments");
    }

    public static void register() {
        DataAccessRegistry.instance().registerDbModel(Comment.class, CommentsController.class, false);
    }


    public void postCommentApproved(Comment newComment) {

        // We only register notifications once per comment, don't double email
        if (!empty(newComment.getNotificationsRegisteredAt())) {
            return;
        }
        newComment.setNotificationsRegisteredAt(mils());
        save(newComment);


        //sendNotifyEmailsForComment(comment, SubscriptionFrequency.INSTANT);
        List<Comment> threadComments = listForKey("threadId", newComment.getThreadId());
        Set<Long> seenContactIds = new HashSet<Long>();
        for(Comment threadComment: threadComments) {

            // Skip the current comment
            if (threadComment.getId().equals(newComment.getId())) {
                continue;
            }
            // Don't notify commenters who never had their comment approved
            if (!threadComment.isApproved()) {
                continue;
            }
            // Don't notify people of their own comment, obviously
            if (newComment.getContactId().equals(threadComment.getContactId())) {
                continue;
            }
            // Only notify once per contact
            if (seenContactIds.contains(threadComment.getContactId())) {
                continue;
            }
            seenContactIds.add(threadComment.getContactId());

            Contact contact = ContactsController.instance().forId(threadComment.getContactId());
            if (contact == null) {
                Log.warn("The comment did not have a valid contact: comment={0} email={1} contactId={2}",
                        threadComment.getId(), threadComment.getAuthorEmail(), threadComment.getContactId()
                );
                continue;
            }


            Subscription subcriptionToUse = null;

            // Get the notfication frequency for the reply subscription
            Subscription replySub = SubscriptionController.instance().forUniqueKey("ownerKey",
                    getOwnerKeyForReplySubscription(threadComment, contact));
            if (wasMentionedInComment(contact, newComment)) {
                Log.info("Contact {0} was mentioned in comment {1}", contact.getDisplayName(), newComment.getBodyHtml());
            }
            if (replySub != null
                    && replySub.isEnabled()
                    && !replySub.getFrequency().equals(SubscriptionFrequency.NEVER)
                    && wasMentionedInComment(contact, newComment)) {
                subcriptionToUse = replySub;
            }
            Subscription threadSub = SubscriptionController.instance().forUniqueKey(
                    "ownerKey", getOwnerKeyForThreadSubscription(threadComment, contact)
            );

            // Get the nofication for the thread subscription, if more frequent, use that
            if (threadSub != null
                        && threadSub.isEnabled()
                        && !threadSub.getFrequency().equals(SubscriptionFrequency.NEVER)) {
                if (subcriptionToUse == null ||
                        threadSub.getFrequency().ordinal() < subcriptionToUse.getFrequency().ordinal()) {
                    subcriptionToUse = threadSub;
                }
            }
            // frequency is not null if the new comment matches a valid subscription
            if (subcriptionToUse != null) {
                Log.info("Register notification for comment={0} contact={1} subscriptionFreq={2}",
                        newComment.getAuthorDisplayName(), contact.getEmail(), subcriptionToUse.getFrequency());
                registerNotification(newComment, contact, subcriptionToUse);
            } else {
                Log.info("Contact {0} was not subscribed, no notification sent.", contact.getEmail());
            }
        }

    }

    private void registerNotification(Comment newComment, Contact notifyContact, Subscription subscription) {
        CommentNotificationCallback handler = new CommentNotificationCallback()
                .setCommentId(newComment.getId());
        Notification notification = new Notification()
                .setHandler(handler)
                .setCallbackPlugin("publisher")
                .setFrequency(subscription.getFrequency())
                .setContactId(notifyContact.getId())
                .setCreatedAt(DateUtils.mils())
                .setSubscriptionId(subscription.getId().toString())
                .setKey("comment-notify---" + newComment.getId().toString() + "---" + notifyContact.getId().toString() );
                ;
        NotificationController.instance().submitNotification(notification);


    }



    private Boolean wasMentionedInComment(Contact contact, Comment comment) {
        return comment.getBodyHtml().contains("data-mentioned-contact=\"" + contact.getId() + "\"");
    }




    @Override
    public void onPreSavePrepare(Comment comment) {
        PegDownProcessor pegDownProcessor = new PegDownProcessor(
                Extensions.FENCED_CODE_BLOCKS |
                        Extensions.AUTOLINKS |
                        Extensions.STRIKETHROUGH
        );

        String html = comment.getBodyMarkdown();
        html = replaceAtMentions(comment, html);
        html = pegDownProcessor.markdownToHtml(html);

        html = Sanitize.basicSanitize(html);
        html = html.replace("</p>", "</p>\n\n");
        html = html.replace("<br>", "<br>\n\n");

        Log.info("New body html: {0}", html);
        // make links no-follow
        comment.setBodyHtml(html);
        // Get rid of <script> tags in the original markdown, also, just to be safe
        // This should not be necessary as we never inject bodyMarkdown directly onto the page,
        // but it never hurts to be extra careful. We don't use the sanitizer because
        // we don't want tons of HTML entities to be inserted, as that makes it harder
        // for the markdown to be edited later
        comment.setBodyMarkdown(scriptMatcher.matcher(comment.getBodyMarkdown()).replaceAll(""));

        comment.setParentTitle(Sanitize.stripAll(comment.getParentTitle()));
        comment.setAuthorFirstName(Sanitize.stripAll(comment.getAuthorFirstName()));
        comment.setAuthorLastName(Sanitize.stripAll(comment.getAuthorLastName()));
        comment.setAuthorDisplayName(Sanitize.stripAll(comment.getAuthorDisplayName()));
        comment.setAuthorEmail(Sanitize.stripAll(comment.getAuthorEmail()));
        comment.setAuthorWebSite(Sanitize.stripAll(comment.getAuthorWebSite()));
    }

    static Pattern scriptMatcher = Pattern.compile("(?s)<script.*?(/>|</script>)");


    static Pattern atMentionMatcher = Pattern.compile("(@(\\w+)|@\"([^\"]+)\"|@'([^']+)')");

    public String replaceAtMentions(Comment comment, String body) {
        return replaceAtMentionsForThreadComments(comment, body, listCommentsForThread(comment.getThreadId()));
    }

    public String replaceAtMentionsForThreadComments(Comment comment, String body, List<Comment> threadComments) {
        body = body.replace("&#64;", "@").replace("&#34;", "\"");
        StringBuffer output = new StringBuffer();
        Matcher matcher = atMentionMatcher.matcher(body);
        Map<String, Long> nameToContactId = map();
        Map<Long, String> contactIdToCommentId = map();
        for (Comment cmt: threadComments) {
            if (cmt.getId().equals(comment.getId())) {
                continue;
            }
            if (cmt.getAuthorEmail().equals(comment.getAuthorEmail())) {
                continue;
            }
            if (empty(cmt.getContactId()) || cmt.getContactId().equals(comment.getContactId())) {
                continue;
            }
            nameToContactId.put(cmt.getAuthorDisplayName().toLowerCase(), cmt.getContactId());
            contactIdToCommentId.put(cmt.getContactId(), cmt.getId().toString());
        }
        while (matcher.find()) {
            String name = firstTruthy(matcher.group(2), matcher.group(3), matcher.group(4));
            if (nameToContactId.containsKey(name.toLowerCase())) {
                Long contactId = nameToContactId.get(name.toLowerCase());
                String replacement = "<a href=\"#stallion-comment-"
                        + contactIdToCommentId.get(contactId) + "\""
                        + " data-mentioned-contact=\""
                        + contactId + "\">@"
                        + name + "</a>";
                matcher.appendReplacement(output, replacement);
            }

        }
        matcher.appendTail(output);
        return output.toString();
    }

    public List<Comment> listCommentsForThread(Long threadId) {
        return filterByKey("threadId", threadId).all();
    }

    public CommentSubscriptionInfo getCommentSubscriptionInfo(Comment comment, Contact contact) {
        CommentSubscriptionInfo info = new CommentSubscriptionInfo();

        String replyKey = getOwnerKeyForReplySubscription(comment, contact);
        Subscription replySub = SubscriptionController.instance().forUniqueKey("ownerKey", replyKey);

        String threadKey = getOwnerKeyForThreadSubscription(comment, contact);
        Subscription threadSub = SubscriptionController.instance().forUniqueKey("ownerKey", threadKey);

        if (replySub != null) {
            info.setReplyNotifyFrequency(replySub.getFrequency());
        }
        if (threadSub != null) {
            info.setThreadNotifyFrequency(threadSub.getFrequency());
        }
        return info;
    }

    public String getOwnerKeyForReplySubscription(Comment comment, Contact contact) {
        return "comment-reply|" + comment.getThreadIdSlugified() + "|" + contact.getId();
    }

    public String getOwnerKeyForThreadSubscription(Comment comment, Contact contact) {
        return "comment-thread|" + comment.getThreadIdSlugified() + "|" + contact.getId();
    }

    public CommentSubscriptionInfo updateCommentSubscriptionInfo(CommentSubscriptionInfo info, Comment comment, Contact contact) {


        String replyKey = getOwnerKeyForReplySubscription(comment, contact);
        SubscriptionController.instance().saveContactSubscription(
                contact,
                "Direct replies and mentions on thread  " + comment.getParentTitle(),
                replyKey,
                info.getReplyNotifyFrequency()
        );

        String threadKey = getOwnerKeyForThreadSubscription(comment, contact);
        SubscriptionController.instance().saveContactSubscription(
                contact,
                "Comment thread for " + comment.getParentTitle(),
                threadKey,
                info.getThreadNotifyFrequency()
        );




        return info;
    }




}
