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

import io.stallion.Context;
import io.stallion.assets.AssetsController;
import io.stallion.publisher.PublisherSettings;
import io.stallion.services.Log;
import io.stallion.templating.TemplateRenderer;
import io.stallion.utils.GeneralUtils;

import java.net.URL;
import java.util.HashMap;

import static io.stallion.utils.Literals.empty;

public class CommentingContext {
    public String renderForm(Object threadId, String parentPermalink, String parentTitle) throws Exception {
        requireAssets();

        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("parentTitle", parentTitle);
        context.put("parentPermalink", parentPermalink);
        context.put("commentThreadId", threadId);
        context.put("commentThreadIdSlug", GeneralUtils.slugify(threadId.toString()));
        context.put("reCaptchaSiteKey", PublisherSettings.getInstance().getReCaptchaSiteKey());
        String html = cascadingRenderTemplate("comment-entry-form", context);
        if (!empty(PublisherSettings.getInstance().getReCaptchaSiteKey())) {
            Context.getResponse().getPageFooterLiterals().addString("<script src=\"https://www.google.com/recaptcha/api.js?\" async defer></script>");
        }
        return html;
    }

    public int commentCountForThread(Object threadId) {
        return 0;
    }

    public String renderComments(Comparable threadId) throws Exception {
        requireAssets();

        for(Comment comment: CommentsController.instance().all()) {
            Log.finer("Existing comment: id={0} threadId={1} deleted={2}", comment.getId(), comment.getThreadId(), comment.getDeleted());
        }
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("commentThreadId", threadId);
        context.put("threadComments", CommentsController.instance().filterByKey("threadId", threadId).filter("deleted", false).sort("createdTicks", "asc"));

        String html = cascadingRenderTemplate("comments-for-thread", context);
        return html;
    }

    private void requireAssets() {
        Log.info("add to per request footer js");

    }

    public String cascadingRenderTemplate(String templatePrefix, HashMap<String, Object> context) throws Exception {
        URL url = getClass().getResource("/templates/" + templatePrefix + ".jinja");
        return  TemplateRenderer.instance().renderTemplate(url, context);
    }

}
