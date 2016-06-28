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

import io.stallion.publisher.contacts.Notification;
import io.stallion.publisher.contacts.NotificationCallbackHandlerInterface;
import io.stallion.publisher.contacts.NotificationCallbackResult;
import io.stallion.publisher.contacts.NotificationCallbackHandlerInterface;
import io.stallion.templating.TemplateRenderer;

import java.net.URL;
import java.util.Map;

import static io.stallion.utils.Literals.map;


public class CommentNotificationCallback implements NotificationCallbackHandlerInterface {

    private Long commentId = 0L;


    public NotificationCallbackResult handle(Notification notification) {
        NotificationCallbackResult result = new NotificationCallbackResult();

        result.setThing("comment");
        result.setThingPlural("comments");

        Comment comment = CommentsController.instance().forId(commentId);

        if (comment == null || !comment.isApproved()) {
            return null;
        }


        URL url = getClass().getClassLoader().getResource("templates/comment-notify-email-partial.jinja");
        Map<String, Object> ctx = map();
        ctx.put("comment", comment);

        String html = TemplateRenderer.instance().renderTemplate(url.toString(), ctx);
        result.setEmailBody(html);

        return result;
    }

    public Long getCommentId() {
        return commentId;
    }

    public CommentNotificationCallback setCommentId(Long commentId) {
        this.commentId = commentId;
        return this;
    }
}
