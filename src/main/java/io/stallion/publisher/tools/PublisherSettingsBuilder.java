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

package io.stallion.publisher.tools;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static io.stallion.utils.Literals.*;

import io.stallion.exceptions.CommandException;
import io.stallion.services.Log;
import io.stallion.templating.JinjaTemplating;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;


public class PublisherSettingsBuilder {

    private String rootUrl;
    private boolean commentModeration;



    public void build(String targetFolder, JinjaTemplating templating) {
        File file = new File(targetFolder + "/conf/publisher.toml");
        if (file.exists()) {
            throw new CommandException("A conf/publisher.toml file already exists. Cannot initialize the new blog.");
        }
        Map map = map(val("builder", this));
        try {
            String template = IOUtils.toString(getClass().getResource("/templates/wizard/publisher.toml.jinja").toURI());
            String content = templating.renderTemplate(template, map);
            FileUtils.write(file, content, "UTF-8");
        } catch (IOException |URISyntaxException e) {
            throw new RuntimeException(e);
        }


    }

    public String getRootUrl() {
        return rootUrl;
    }

    public PublisherSettingsBuilder setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
        return this;
    }

    public boolean isCommentModeration() {
        return commentModeration;
    }

    public PublisherSettingsBuilder setCommentModeration(boolean commentModeration) {
        this.commentModeration = commentModeration;
        return this;
    }
}
