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
import io.stallion.settings.Settings;
import io.stallion.templating.TemplateContextHookHandler;
import io.stallion.utils.rss.RssLink;

import java.util.Map;

public class CommentsContextHook extends TemplateContextHookHandler {


    public void handle(Map<String, Object> obj) {
        obj.put("commenting", new CommentingContext());
        String link = Settings.instance().getSiteUrl() + "/_stx/comments/rss.xml";
        String name = "Comments on " + Settings.instance().getSiteName();
        Context.response().getMeta().getRssLinks().add(
                new RssLink().setLink(link).setTitle(name)
        );
    }
}
