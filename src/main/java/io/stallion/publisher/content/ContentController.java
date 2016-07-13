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

package io.stallion.publisher.content;

import io.stallion.dataAccess.DataAccessRegistry;
import io.stallion.dataAccess.DataAccessRegistration;
import io.stallion.dataAccess.DisplayableModelController;
import io.stallion.dataAccess.filtering.FilterChain;
import io.stallion.settings.Settings;

import static io.stallion.utils.Literals.empty;
import static io.stallion.utils.Literals.emptyInstance;
import static io.stallion.utils.Literals.or;


public class ContentController extends DisplayableModelController<Content> {
    public static ContentController instance() {
        return (ContentController) DataAccessRegistry.instance().get("contents");
    }
    public static void register() {
        DataAccessRegistration registration = new DataAccessRegistration()
                .setModelClass(Content.class)
                .setControllerClass(ContentController.class)
                .setBucket("contents");
        DataAccessRegistry.instance().register(registration);

    }

    public FilterChain<Content> pages() {
        return filter("type", "page");
    }

    public FilterChain<Content> posts(Long blogId) {
        return filter("type", "post").filter("blogId", blogId);
    }

    public FilterChain<Content> posts() {
        return filter("type", "post");
    }


    @Override
    public String getTemplate(Content item) {
        if (!empty(item.getTemplate())) {
            return item.getTemplate();
        }
        if (empty(item.getBlogId())) {
            return getDefaultTemplate();
        }
        BlogConfig config = BlogConfigController.instance().forId(item.getBlogId());
        if (emptyInstance(config)) {
            return getDefaultTemplate();
        }
        return or(config.getPostTemplate(), getDefaultTemplate());

    }




}