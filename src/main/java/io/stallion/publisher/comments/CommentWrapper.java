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

import org.apache.commons.codec.digest.DigestUtils;

import static io.stallion.utils.Literals.*;


public class CommentWrapper {
    private Long id = 0L;
    private String authorDisplayName = "";
    private String bodyHtml = "";
    private String bodyMarkdown = "";
    private String authorWebSite = "";
    private String authorEmailHash = "";
    private String authorEmail = "";
    private CommentState state = CommentState.PENDING_AKISMET;
    private Boolean isApproved = false;
    private Long parentId = 0L;
    private Long threadId = 0L;
    private String parentPermalink = "";
    private Long createdTicks = 0L;
    private boolean editable = false;
    private boolean adminable = false;
    private boolean pending = false;
    private String permalink = "";

    public String getGravatarUrl() {
        if (empty(getAuthorEmailHash())) {
            return "";
        }
        return "https://www.gravatar.com/avatar/" + getAuthorEmailHash() + "?d=identicon";
    }



    public String getAvatarLetter() {
        return authorDisplayName.substring(0, 1).toUpperCase();
    }


    public String getAvatarColor() {

        String hash = DigestUtils.md5Hex(authorDisplayName).toUpperCase();
        String r = Integer.toHexString(Integer.parseInt(hash.substring(0, 1), 16) / 2);
        String g = Integer.toHexString(Integer.parseInt(hash.substring(1, 2), 16) / 2);
        String b = Integer.toHexString(Integer.parseInt(hash.substring(2, 3), 16) / 2);
        return "#" + r + r + g + g + b + b;
    }

    public Long getId() {
        return id;
    }

    public CommentWrapper setId(Long id) {
        this.id = id;
        return this;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public CommentWrapper setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
        return this;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public CommentWrapper setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
        return this;
    }

    public String getBodyMarkdown() {
        return bodyMarkdown;
    }

    public CommentWrapper setBodyMarkdown(String bodyMarkdown) {
        this.bodyMarkdown = bodyMarkdown;
        return this;
    }

    public String getAuthorWebSite() {
        return authorWebSite;
    }

    public CommentWrapper setAuthorWebSite(String authorWebSite) {
        this.authorWebSite = authorWebSite;
        return this;
    }

    public String getAuthorEmailHash() {
        return authorEmailHash;
    }

    public CommentWrapper setAuthorEmailHash(String authorEmailHash) {
        this.authorEmailHash = authorEmailHash;
        return this;
    }

    public CommentState getState() {
        return state;
    }

    public CommentWrapper setState(CommentState state) {
        this.state = state;
        return this;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public CommentWrapper setApproved(Boolean approved) {
        isApproved = approved;
        return this;
    }

    public Long getParentId() {
        return parentId;
    }

    public CommentWrapper setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public Long getThreadId() {
        return threadId;
    }

    public CommentWrapper setThreadId(Long threadId) {
        this.threadId = threadId;
        return this;
    }

    public String getParentPermalink() {
        return parentPermalink;
    }

    public CommentWrapper setParentPermalink(String parentPermalink) {
        this.parentPermalink = parentPermalink;
        return this;
    }

    public Long getCreatedTicks() {
        return createdTicks;
    }

    public CommentWrapper setCreatedTicks(Long createdTicks) {
        this.createdTicks = createdTicks;
        return this;
    }

    public boolean isEditable() {
        return editable;
    }

    public CommentWrapper setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }

    public boolean isAdminable() {
        return adminable;
    }

    public CommentWrapper setAdminable(boolean adminable) {
        this.adminable = adminable;
        return this;
    }

    public boolean isPending() {
        return pending;
    }

    public CommentWrapper setPending(boolean pending) {
        this.pending = pending;
        return this;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public CommentWrapper setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
        return this;
    }

    public String getPermalink() {
        return permalink;
    }

    public CommentWrapper setPermalink(String permalink) {
        this.permalink = permalink;
        return this;
    }

}
