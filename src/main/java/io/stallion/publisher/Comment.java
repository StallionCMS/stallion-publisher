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

package io.stallion.publisher;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.stallion.Context;
import io.stallion.dataAccess.*;
import io.stallion.users.Role;
import io.stallion.utils.DateUtils;
import io.stallion.utils.GeneralUtils;
import io.stallion.utils.json.JSON;
import io.stallion.utils.json.RestrictedViews;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.servlet.http.Cookie;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;

@Table(name="comments")
public class Comment extends MappedModel {
    public static final String COMMENT_SECRET_COOKIE = "stPublisherCommenterSecret";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd-HHmmss");

    private String authorFirstName = "";
    private String authorLastName = "";
    private String authorDisplayName = "";
    private String bodyHtml = "";
    private String bodyMarkdown = "";
    private String authorEmail = "";
    private String authorWebSite = "";
    private String authorSecret = "";
    private CommentState state = CommentState.PENDING_AKISMET;
    private Boolean approved = false;
    private Boolean akismetApproved = false;
    private Boolean moderatorApproved = false;
    private Long akismetCheckedAt = 0L;
    private Long moderatedAt = 0L;
    private Long approvedAt = 0L;
    private Long parentId = 0L;
    private Long threadId = 0L;
    private String editingToken = "";
    private String parentPermalink = "";
    private String captchaResponse = "";
    private Long createdTicks = 0L;
    private String filePath = "";
    private String parentTitle;
    private Long contactId;
    private Long notificationsRegisteredAt = 0L;
    private Boolean previouslyApproved = false;
    private Boolean threadSubscribe = false;
    private Boolean mentionSubscribe = false;

    public CommentWrapper toWrapper() {
        return toWrapper(false);
    }

    public CommentWrapper toWrapper(boolean withEditable) {
        CommentWrapper wrapper = new CommentWrapper()
                .setAdminable(isAdminable())
                .setApproved(isApproved())
                .setAuthorDisplayName(getAuthorDisplayName())
                .setAuthorWebSite(getAuthorWebSite())
                .setBodyHtml(getBodyHtml())
                .setState(getState())
                .setCreatedTicks(getCreatedTicks())
                .setEditable(isEditable())
                .setParentId(getParentId())
                .setPermalink(getPermalink())
                .setId(getId())
                .setParentPermalink(getParentPermalink())
                .setPending(state == CommentState.PENDING_MODERATION)
                .setThreadId(getThreadId())
                ;
        if (isEditable() || isAdminable() || withEditable) {
            wrapper.setBodyMarkdown(getBodyMarkdown());
            wrapper.setAuthorEmail(getAuthorEmail());
        }
        if (PublisherSettings.getInstance().isCommentsUseGravatar()) {
            wrapper.setAuthorEmailHash(GeneralUtils.md5Hash(getAuthorEmail()));
        }

        return wrapper;

    }


    @Column
    public String getAuthorFirstName() {
        return authorFirstName;
    }

    public Comment setAuthorFirstName(String authorFirstName) {
        this.authorFirstName = authorFirstName;
        return this;
    }


    @Column
    public String getAuthorLastName() {
        return authorLastName;
    }

    public Comment setAuthorLastName(String authorLastName) {
        this.authorLastName = authorLastName;
        return this;
    }



    @JsonView(RestrictedViews.Public.class)
    @Column
    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public Comment setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
        return this;
    }




    @JsonView(RestrictedViews.Public.class)
    @Column
    public String getBodyHtml() {
        return bodyHtml;
    }

    public Comment setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
        return this;
    }


    @JsonView(RestrictedViews.Member.class)
    @Column
    public String getAuthorEmail() {
        return authorEmail;
    }

    public Comment setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
        return this;
    }

    @JsonView(RestrictedViews.Public.class)
    @Column
    public String getAuthorWebSite() {
        return authorWebSite;
    }

    public Comment setAuthorWebSite(String authorWebSite) {
        this.authorWebSite = authorWebSite;
        return this;
    }
    @Column
    public CommentState getState() {
        return state;
    }

    public Comment setState(CommentState state) {
        this.state = state;
        return this;
    }

    @Column
    public Boolean isApproved() {
        return approved;
    }

    @JsonView(RestrictedViews.Member.class)
    public Comment setApproved(Boolean isApproved) {
        this.approved = isApproved;
        return this;
    }

    @Column
    public Boolean getAkismetApproved() {
        return akismetApproved;
    }

    public Comment setAkismetApproved(Boolean akismetApproved) {
        this.akismetApproved = akismetApproved;
        return this;
    }

    @Column
    public Boolean getModeratorApproved() {
        return moderatorApproved;
    }

    public Comment setModeratorApproved(Boolean moderatorApproved) {
        this.moderatorApproved = moderatorApproved;
        return this;
    }

    @Column
    public Long getAkismetCheckedAt() {
        return akismetCheckedAt;
    }

    public Comment setAkismetCheckedAt(Long akismetCheckedAt) {
        this.akismetCheckedAt = akismetCheckedAt;
        return this;
    }

    @Column
    public Long getModeratedAt() {
        return moderatedAt;
    }

    public Comment setModeratedAt(Long moderatedAt) {
        this.moderatedAt = moderatedAt;
        return this;
    }


    @JsonView(RestrictedViews.Public.class)
    @Column
    public Long getParentId() {
        return parentId;
    }

    public Comment setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }


    @JsonView(RestrictedViews.Public.class)
    @AlternativeKey
    @Column
    public Long getThreadId() {
        return threadId;
    }

    public Comment setThreadId(Long threadId) {
        this.threadId = threadId;
        return this;
    }

    @Column
    public String getEditingToken() {
        return editingToken;
    }

    public Comment setEditingToken(String editingToken) {
        this.editingToken = editingToken;
        return this;
    }

    @Column
    public String getParentPermalink() {
        return parentPermalink;
    }

    public Comment setParentPermalink(String parentPermalink) {
        this.parentPermalink = parentPermalink;
        return this;
    }


    @Column
    public String getCaptchaResponse() {
        return captchaResponse;
    }

    public Comment setCaptchaResponse(String captchaResponse) {
        this.captchaResponse = captchaResponse;
        return this;
    }

    @Column
    @JsonView(RestrictedViews.Public.class)
    public Long getCreatedTicks() {
        return createdTicks;
    }

    public Comment setCreatedTicks(Long createdTicks) {
        this.createdTicks = createdTicks;
        return this;
    }

    @Column
    public String getAuthorSecret() {
        return authorSecret;
    }

    public Comment setAuthorSecret(String authorSecret) {
        this.authorSecret = authorSecret;
        return this;
    }


    @JsonIgnore
    @JsonView(RestrictedViews.Member.class)
    public boolean isEditable() {
        if (Context.getUser().isInRole(Role.STAFF)) {
            return true;
        }
        if (Context.getRequest() != null) {
            Cookie cookie = Context.getRequest().getCookie(COMMENT_SECRET_COOKIE);
            if (cookie != null && !empty(cookie.getValue())) {
                if (cookie.getValue().equals(getAuthorSecret())) {
                    return true;
                }
            }
        }
        return false;
    }

    @JsonIgnore
    public boolean isAdminable() {
        if (Context.getUser().isInRole(Role.STAFF)) {
            return true;
        }
        return false;
    }


    @JsonProperty
    public String getThreadIdSlugified() {
        return GeneralUtils.slugify(getThreadId().toString());
    }

    @JsonIgnore
    public String getOwnerJson() {
        try {
            return JSON.stringify(this, RestrictedViews.Member.class, true);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonIgnore
    public String getModeratedClass() {
        if (state == CommentState.APPROVED) {
            return "st-comment-approved";
        } else if (state == CommentState.PENDING_MODERATION) {
            return "st-comment-pending";
        } else {
            return "st-comment-rejected";
        }
    }


    @JsonView(RestrictedViews.Public.class)
    @Column
    public String getParentTitle() {
        return parentTitle;
    }

    public Comment setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
        return this;
    }


    public String getPermalink() {
        return this.parentPermalink + "#st-comment-" + getId();
    }

    public String permalinkWithParams(Map<String, String> params) {
        List<BasicNameValuePair> pairs = list();
        for(Map.Entry<String, String> entry: params.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return permalinkWithQuery(URLEncodedUtils.format(pairs, "UTF-8"));
    }

    public String permalinkWithQuery(String query) {
        String link;
        if (this.parentPermalink.contains("?")) {
            link = this.parentPermalink + "&";
        } else {
            link = this.parentPermalink + "?";
        }
        return link + query + "#st-comment-" + getId();
    }

    @JsonView(RestrictedViews.Owner.class)
    @Column
    public Long getContactId() {
        return contactId;
    }

    public Comment setContactId(Long contactId) {
        this.contactId = contactId;
        return this;
    }

    @Column
    public Long getApprovedAt() {
        return approvedAt;
    }

    public Comment setApprovedAt(Long approvedAt) {
        this.approvedAt = approvedAt;
        return this;
    }

    @Column
    public Boolean getPreviouslyApproved() {
        return previouslyApproved;
    }

    public Comment setPreviouslyApproved(Boolean previouslyApproved) {
        this.previouslyApproved = previouslyApproved;
        return this;
    }

    @Column
    public Long getNotificationsRegisteredAt() {
        return notificationsRegisteredAt;
    }

    public Comment setNotificationsRegisteredAt(Long notificationsRegisteredAt) {
        this.notificationsRegisteredAt = notificationsRegisteredAt;
        return this;
    }

    @JsonView(RestrictedViews.Member.class)
    @Column
    public Boolean getThreadSubscribe() {
        return threadSubscribe;
    }

    public Comment setThreadSubscribe(Boolean threadSubscribe) {
        this.threadSubscribe = threadSubscribe;
        return this;
    }

    @JsonView(RestrictedViews.Member.class)
    @Column
    public Boolean getMentionSubscribe() {
        return mentionSubscribe;
    }

    public Comment setMentionSubscribe(Boolean mentionSubscribe) {
        this.mentionSubscribe = mentionSubscribe;
        return this;
    }

    @JsonView(RestrictedViews.Member.class)
    @Column
    public String getBodyMarkdown() {
        return bodyMarkdown;
    }

    public Comment setBodyMarkdown(String bodyMarkdown) {
        this.bodyMarkdown = bodyMarkdown;
        return this;
    }
}
