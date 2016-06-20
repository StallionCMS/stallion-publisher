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

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.tag.Tag;
import com.hubspot.jinjava.tree.TagNode;
import io.stallion.Context;
import io.stallion.assets.Bundle;
import io.stallion.dataAccess.Displayable;
import io.stallion.dataAccess.file.TextItem;
import io.stallion.publisher.PublisherSettings;
import io.stallion.services.Log;
import io.stallion.templating.TemplateRenderer;

import java.util.List;
import java.util.Map;

import static io.stallion.utils.Literals.empty;
import static io.stallion.utils.Literals.list;


public class CommentsTag implements Tag {
    public String interpret(TagNode tagNode, JinjavaInterpreter jinjavaInterpreter) {
        //jinjavaInterpreter.getContext().put()
        try {



            Context.getResponse().getPageFooterLiterals().addDefinedBundle("publisher:public.js");




            jinjavaInterpreter.enterScope();
            Map<String, Object> context = jinjavaInterpreter.getContext();
            Displayable post = (Displayable) context.get("post");
            context.put("commentThreadId", post.getId());

            CommentThreadContext commentsContext = new CommentThreadContext();
            commentsContext
                    .setThreadId(post.getId())
                    .setReCaptchaKey(PublisherSettings.getInstance().getReCaptchaSiteKey())
                    .setParentPermalink(post.getPermalink())
                    .setParentTitle(post.getTitle());
            if (!empty(PublisherSettings.getInstance().getReCaptchaSiteKey())) {
                Context.getResponse().getPageFooterLiterals().addJs("https://www.google.com/recaptcha/api.js?&render=explicit&onload=stCommentsOnLoadCaptcha");
            }
            List<CommentWrapper> comments = list();
            for (Comment comment : CommentsController.instance()
                    .filterByKey("threadId", post.getId())
                    .filter("deleted", false)
                    .sort("createdTicks", "asc")
                    .all()) {
                if (comment.isApproved() || comment.isAdminable() || comment.isEditable()) {
                    commentsContext.getComments().add(comment.toWrapper());
                }
            }

            context.put("commentsContext", commentsContext);


            return TemplateRenderer.instance().renderTemplate("publisher:comments-section-for-post.jinja", context);
        } catch (RuntimeException e) {
            Log.exception(e, "Error rendering comment thread tag.");
            throw e;
        } finally {
            jinjavaInterpreter.leaveScope();
        }

    }

    public String getEndTagName() {
        return null;
    }

    public String getName() {
        return "comments_section";
    }
}
