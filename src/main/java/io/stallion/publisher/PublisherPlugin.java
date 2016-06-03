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

import io.stallion.boot.AppContextLoader;
import io.stallion.assets.BundleFile;
import io.stallion.assets.DefinedBundle;
import io.stallion.plugins.StallionJavaPlugin;
import io.stallion.restfulEndpoints.*;
import io.stallion.services.Log;
import io.stallion.templating.JinjaTemplating;
import io.stallion.templating.TemplateRenderer;

import java.util.List;

import static io.stallion.utils.Literals.*;

public class PublisherPlugin extends StallionJavaPlugin {

    @Override
    public String getPluginName() {
        return "publisher";
    }

    @Override
    public void boot() throws Exception {
        TemplateConfig.load();
        BlogConfigController.register();
        BlogPostController.register();
        BlogPostVersionController.register();
        CommentController.register();
        ContactController.register();
        AuthorProfileController.register();
        UploadedFileController.register();
        FormSubmissionController.register();
        SiteSettingsController.register();
        PageController.register();
        GlobalModuleController.register();
        GlobalModuleVersionController.register();



        ResourceToEndpoints converter = new ResourceToEndpoints("/st-publisher");
        List<EndpointResource> resources = list(
                new AdminEndpoints(),
                new CommentsEndpoints(),
                new ContactsEndpoints(),
                new GlobalModuleEndpoints()
        );
        for (EndpointResource resource: resources) {
            Log.finer("Register resource {0}", resource.getClass().getName());
            EndpointsRegistry.instance().addEndpoints(converter.convert(resource).toArray(new RestEndpointBase[]{}));
        }

        DefinedBundle.register(
                new DefinedBundle("publisher:admin2.js", ".js",
                        new BundleFile().setPluginName("stallion").setLiveUrl("always/jquery-1.11.3.min.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("admin/simplemde.min.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("admin/bootstrap.min.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("always/riot-and-compiler.js").setDebugUrl("always/riot-and-compiler.min.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("always/stallion.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("admin/moment.min.js"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/dropzone.js"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/common.tag").setProcessor("riot"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/admin-riot.tag").setProcessor("riot"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/markdown-editor.tag").setProcessor("riot"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/page-editor.tag").setProcessor("riot"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/comments-admin.tag").setProcessor("riot"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/contacts-admin.tag").setProcessor("riot"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/authors-config.tag").setProcessor("riot"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/blog-configs.tag").setProcessor("riot"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/extra-html-config.tag").setProcessor("riot"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/template-widgets-config.tag").setProcessor("riot"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/sitemap-config.tag").setProcessor("riot"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/site-information-config.tag").setProcessor("riot"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/file-library.tag").setProcessor("riot")
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
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/dropzone.css"),
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
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/dropzone.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/admin.css")
                )
        );

        TemplateRenderer.instance().getJinjaTemplating().registerTag(new GlobalModuleTag());
    }
}
