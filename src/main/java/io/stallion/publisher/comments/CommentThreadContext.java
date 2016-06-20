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

import java.util.List;
import java.util.Map;

import static io.stallion.utils.Literals.list;
import static io.stallion.utils.Literals.map;


public class CommentThreadContext {
    private Long threadId = 0L;
    private String parentTitle = "";
    private String parentPermalink = "";
    private List<CommentWrapper> comments = list();
    private Map<String, Object> commentById = map();
    private Map<String, Object> riotTagByCommentId = map();
    private String reCaptchaKey = "";


    public Long getThreadId() {
        return threadId;
    }

    public CommentThreadContext setThreadId(Long threadId) {
        this.threadId = threadId;
        return this;
    }

    public String getParentTitle() {
        return parentTitle;
    }

    public CommentThreadContext setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
        return this;
    }

    public String getParentPermalink() {
        return parentPermalink;
    }

    public CommentThreadContext setParentPermalink(String parentPermalink) {
        this.parentPermalink = parentPermalink;
        return this;
    }

    public List<CommentWrapper> getComments() {
        return comments;
    }

    public CommentThreadContext setComments(List<CommentWrapper> comments) {
        this.comments = comments;
        return this;
    }

    public Map<String, Object> getCommentById() {
        return commentById;
    }

    public CommentThreadContext setCommentById(Map<String, Object> commentById) {
        this.commentById = commentById;
        return this;
    }

    public Map<String, Object> getRiotTagByCommentId() {
        return riotTagByCommentId;
    }

    public CommentThreadContext setRiotTagByCommentId(Map<String, Object> riotTagByCommentId) {
        this.riotTagByCommentId = riotTagByCommentId;
        return this;
    }

    public String getReCaptchaKey() {
        return reCaptchaKey;
    }

    public CommentThreadContext setReCaptchaKey(String reCaptchaKey) {
        this.reCaptchaKey = reCaptchaKey;
        return this;
    }
}
