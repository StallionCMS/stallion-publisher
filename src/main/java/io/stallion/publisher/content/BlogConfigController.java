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

import io.stallion.dataAccess.*;
import io.stallion.publisher.PublisherSettings;

import static io.stallion.utils.Literals.empty;


public class BlogConfigController extends StandardModelController<BlogConfig> {
    public static BlogConfigController instance() {
        return (BlogConfigController) DataAccessRegistry.instance().get("blog_configs");
    }

    public static void register() {
        DataAccessRegistry.instance().registerDbModel(BlogConfig.class, BlogConfigController.class, LocalMemoryStash.class, "blog_configs");
        for (BlogConfig config: PublisherSettings.getInstance().getBlogs()) {
            BlogConfig existing = null;
            if (!empty(config.getId())) {
                existing = instance().forId(config.getId());
            } else {
                existing = instance().forUniqueKey("slug", config.getSlug());
            }
            if (existing != null) {
                continue;
            }
            instance().save(config);
        }
    }


}
