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

import static io.stallion.utils.Literals.*;

import java.util.Map;
import java.util.Set;

import io.stallion.Context;
import io.stallion.dataAccess.DataAccessRegistry;
import io.stallion.dataAccess.DisplayableModelController;
import io.stallion.dataAccess.NoStash;
import io.stallion.reflection.PropertyUtils;
import io.stallion.utils.DateUtils;
import io.stallion.utils.json.JSON;
import org.apache.commons.lang3.StringUtils;


public class ContentsVersionController extends DisplayableModelController<ContentVersion> {
    private static ContentsVersionController _instance;
    public static ContentsVersionController instance() {
        return _instance;
    }
    public static void register() {
        _instance = (ContentsVersionController)DataAccessRegistry.instance().registerDbModel(ContentVersion.class, ContentsVersionController.class, NoStash.class);
    }

    private static Set<String> ignoreUpdateFields = set("id", "versionDate", "postId", "checkpoint", "oldUrls");
    public void updatePostWithVersion(Content post, ContentVersion version) {
        for(Map.Entry<String, Object> prop: PropertyUtils.getProperties(version).entrySet()) {
            if (ignoreUpdateFields.contains(prop.getKey())) {
                continue;
            }
            if (PropertyUtils.isWriteable(post, prop.getKey())) {
                PropertyUtils.setProperty(post, prop.getKey(), prop.getValue());
            }
        }
    }

    public ContentVersion newVersionFromPostVersion(ContentVersion post) {
        return newVersionFromPost(post);
    }

    public ContentVersion newVersionFromPost(Content post) {
        ContentVersion version = new ContentVersion();
        for(Map.Entry<String, Object> prop: PropertyUtils.getProperties(post).entrySet()) {
            if (PropertyUtils.isWriteable(version, prop.getKey())) {
                PropertyUtils.setProperty(version, prop.getKey(), prop.getValue());
            }
        }
        // Deep copy widgets
        version.setWidgets(JSON.parseList(JSON.stringify(post.getWidgets())));
        // Need to use the ID of the version instance, not the blog post
        if (post instanceof ContentVersion) {
            version.setPostId(((ContentVersion) post).getPostId());
        } else {
            version.setPostId(post.getId());
        }
        version.setVersionAuthorId(Context.getUser().getId());
        version.setVersionAuthorName(or(Context.getUser().getDisplayName(), Context.getUser().getUsername()));
        version.setId(DataAccessRegistry.instance().getTickets().nextId());
        version.setVersionDate(DateUtils.utcNow());
        version.setCheckpoint(true);
        version.setPermanentCheckpoint(false);
        version.setWordCount(StringUtils.countMatches(version.getOriginalContent(), " "));

        return version;
    }
}

