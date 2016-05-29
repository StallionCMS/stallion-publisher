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

import static io.stallion.utils.Literals.*;

import java.util.Map;
import java.util.Set;

import io.stallion.Context;
import io.stallion.dataAccess.DataAccessRegistry;
import io.stallion.dataAccess.DisplayableModelController;
import io.stallion.reflection.PropertyUtils;
import io.stallion.utils.DateUtils;
import io.stallion.utils.json.JSON;


public class BlogPostVersionController extends DisplayableModelController<BlogPostVersion> {
    private static BlogPostVersionController _instance;
    public static BlogPostVersionController instance() {
        return _instance;
    }
    public static void register() {
        _instance = (BlogPostVersionController)DataAccessRegistry.instance().registerDbModel(BlogPostVersion.class, BlogPostVersionController.class, false);
    }

    private static Set<String> ignoreUpdateFields = set("id", "versionDate", "postId", "checkpoint");
    public void updatePostWithVersion(BlogPost post, BlogPostVersion version) {
        for(Map.Entry<String, Object> prop: PropertyUtils.getProperties(version).entrySet()) {
            if (ignoreUpdateFields.contains(prop.getKey())) {
                continue;
            }
            if (PropertyUtils.isWriteable(post, prop.getKey())) {
                PropertyUtils.setProperty(post, prop.getKey(), prop.getValue());
            }
        }
    }

    public BlogPostVersion newVersionFromPostVersion(BlogPostVersion post) {
        return newVersionFromPost(post);
    }

    public BlogPostVersion newVersionFromPost(BlogPost post) {
        BlogPostVersion version = new BlogPostVersion();
        for(Map.Entry<String, Object> prop: PropertyUtils.getProperties(post).entrySet()) {
            if (PropertyUtils.isWriteable(version, prop.getKey())) {
                PropertyUtils.setProperty(version, prop.getKey(), prop.getValue());
            }
        }
        // Deep copy widgets
        version.setWidgets(JSON.parseList(JSON.stringify(post.getWidgets())));
        // Need to use the ID of the version instance, not the blog post
        if (post instanceof BlogPostVersion) {
            version.setPostId(((BlogPostVersion) post).getPostId());
        } else {
            version.setPostId(post.getId());
        }
        version.setVersionAuthorId(Context.getUser().getId());
        version.setVersionAuthorName(or(Context.getUser().getDisplayName(), Context.getUser().getUsername()));
        version.setId(DataAccessRegistry.instance().getTickets().nextId());
        version.setVersionDate(DateUtils.utcNow());
        version.setCheckpoint(true);
        version.setPermanentCheckpoint(false);
        return version;
    }
}

