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

import io.stallion.email.ContactableEmailer;
import io.stallion.users.User;


public class AdminNotifyCommentEmailer extends ContactableEmailer {
    private Comment comment;

    public AdminNotifyCommentEmailer(User user, Comment comment) {
        super(user);
        this.comment = comment;
        put("comment", comment);
        put("rejectPermalink", comment.permalinkWithQuery("stLogin=true&stModerateAction=reject&stModerateId=" + comment.getId()));
        put("approvePermalink", comment.permalinkWithQuery("stLogin=true&stModerateAction=approve&stModerateId=" + comment.getId()));

    }

    @Override
    public boolean isTransactional() {
        return true;
    }

    @Override
    public String getTemplate() {
        return getClass().getResource("/templates/new-comment-admin-email.jinja").toString();
    }

    @Override
    public String getSubject() {
        return "New comment on \"{comment.parentTitle}\" by {comment.authorDisplayName}" ;
    }

}
