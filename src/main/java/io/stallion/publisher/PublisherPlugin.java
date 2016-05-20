/*
 * Copyright (c) 2015-2016 Patrick Fitzsimmons
 *
 * This file is part of Stallion Publisher.
 *
 * Stallion Publisher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.stallion.publisher;

import io.stallion.boot.AppContextLoader;
import io.stallion.assets.BundleFile;
import io.stallion.assets.DefinedBundle;
import io.stallion.plugins.StallionJavaPlugin;
import io.stallion.restfulEndpoints.*;
import io.stallion.services.Log;

import java.util.List;

import static io.stallion.utils.Literals.*;

public class PublisherPlugin extends StallionJavaPlugin {

    @Override
    public String getPluginName() {
        return "publisher";
    }

    @Override
    public void boot() throws Exception {
        BlogConfigController.register();
        BlogPostController.register();
        CommentController.register();
        ContactController.register();
        AuthorProfileController.register();
        UploadedFileController.register();
        FormSubmissionController.register();
        PageController.register();


        ResourceToEndpoints converter = new ResourceToEndpoints("/st-publisher");
        List<AdminEndpoints> resources = list(new AdminEndpoints());
        for (EndpointResource resource: resources) {
            Log.finer("Register resource {0}", resource.getClass().getName());
            EndpointsRegistry.instance().addEndpoints(converter.convert(resource).toArray(new RestEndpointBase[]{}));
        }

        DefinedBundle.register(
                new DefinedBundle("publisher:admin2.js", ".js",
                        new BundleFile().setPluginName("stallion").setLiveUrl("always/jquery-1.11.3.min.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("admin/simplemde.min.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("always/riot-and-compiler.js").setDebugUrl("always/riot-and-compiler.min.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("always/stallion.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("admin/moment.min.js"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/admin-riot.tag").setProcessor("riot")
                        )
        );

        DefinedBundle.register(
                new DefinedBundle("publisher:admin.js", ".js",
                        new BundleFile().setPluginName("stallion")
                                .setLiveUrl("admin/react-0.14.7/react.min.js")
                                .setDebugUrl("admin/react-0.14.7/react.js"),
                        new BundleFile().setPluginName("stallion")
                                .setLiveUrl("admin/react-0.14.7/react-dom.min.js")
                                .setDebugUrl("admin/react-0.14.7/react-dom.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("admin/react-router.0.13.3.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("admin/zepto.min.js").setDebugUrl("admin/zepto.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("always/stallion.js").setDebugUrl("always/stallion.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("admin/simplemde.min.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("admin/react-textarea.min.js"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/dashboard.jsx?processor=jsx"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/posts.jsx?processor=jsx"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/comments.jsx?processor=jsx"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/contacts.jsx?processor=jsx"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/widgets.jsx?processor=jsx"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/settings-sitemap.jsx?processor=jsx"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/files.jsx?processor=jsx"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/settings-authors.jsx?processor=jsx"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/settings-blogs.jsx?processor=jsx"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/settings-extra-html.jsx?processor=jsx"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/settings-site-information.jsx?processor=jsx"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/post-editor.jsx?processor=jsx"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/sidebar-nav.jsx?processor=jsx"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/app-router.jsx?processor=jsx"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/admin.js")

                )
        );
        DefinedBundle.register(
                new DefinedBundle("publisher:admin.css", ".css",
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/bootstrap.min.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/simplemde.min.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/icomoon.css"),
                        //new BundleFile().setPluginName("publisher").setLiveUrl("admin/bootstrap-theme.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/dashboard.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/admin.css")
                )
        );

        DefinedBundle.register(
                new DefinedBundle("publisher:admin2.css", ".css",
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/bootstrap.min.css"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("always/pure-min.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/simplemde.min.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/icomoon.css"),
                        //new BundleFile().setPluginName("publisher").setLiveUrl("admin/bootstrap-theme.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/dashboard.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/admin.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/admin.css")
                )
        );
    }
}
