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

package io.stallion.publisher;

import io.stallion.assets.BundleFile;
import io.stallion.assets.DefinedBundle;
import io.stallion.plugins.StallionJavaPlugin;
import io.stallion.publisher.comments.CommentsController;
import io.stallion.publisher.comments.CommentsEndpoints;
import io.stallion.publisher.comments.CommentsTag;
import io.stallion.publisher.comments.SeleniumEndpoints;
import io.stallion.publisher.contacts.*;
import io.stallion.publisher.content.*;
import io.stallion.restfulEndpoints.*;
import io.stallion.services.Log;
import io.stallion.settings.Settings;
import io.stallion.sitemaps.SiteMapController;
import io.stallion.sitemaps.SiteMapItem;
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

        // Register controllers
        TemplateConfig.load();
        BlogConfigController.register();
        ContentController.register();
        ContentsVersionController.register();
        CommentsController.register();
        ContactsController.register();
        AuthorProfileController.register();
        UploadedFileController.register();
        FormSubmissionController.register();
        SiteSettingsController.register();
        SubscriptionController.register();
        NotificationController.register();
        GlobalModuleController.register();
        GlobalModuleVersionController.register();


        // Register endpoints

        ResourceToEndpoints converter = new ResourceToEndpoints("/st-publisher");
        List<EndpointResource> resources = list(
                new AdminEndpoints(),
                new CommentsEndpoints(),
                new ContactsEndpoints(),
                new GlobalModuleEndpoints(),
                new SeleniumEndpoints()
        );
        for (EndpointResource resource: resources) {
            Log.finer("Register resource {0}", resource.getClass().getName());
            EndpointsRegistry.instance().addEndpoints(converter.convert(resource).toArray(new RestEndpointBase[]{}));
        }

        // Register blog endpoints

        for (BlogConfig config: BlogConfigController.instance().all()) {
            BlogEndpoints resource = new BlogEndpoints(config);
            String rootUrl = config.getSlug();
            if (rootUrl.endsWith("/")) {
                rootUrl = rootUrl.substring(0, rootUrl.length() - 1);
            }
            ResourceToEndpoints resourceConverter = new ResourceToEndpoints(rootUrl);
            List<JavaRestEndpoint> endpointList = resourceConverter.convert(resource);
            JavaRestEndpoint[] endpointArray = endpointList.toArray(new JavaRestEndpoint[0]);
            EndpointsRegistry.instance().addEndpoints(endpointArray);
            SiteMapController.instance().addItem(new SiteMapItem().setPermalink(Settings.instance().getSiteUrl() + rootUrl));
        }


        // Register Jinja Tags

        TemplateRenderer.instance().getJinjaTemplating().registerTag(new GlobalModuleTag());
        TemplateRenderer.instance().getJinjaTemplating().registerTag(new FormTag());
        TemplateRenderer.instance().getJinjaTemplating().registerTag(new CommentsTag());
        TemplateRenderer.instance().getJinjaTemplating().registerTag(new EditableMarkdownTag());
        TemplateRenderer.instance().getJinjaTemplating().registerTag(new EditableImageTag());
        TemplateRenderer.instance().getJinjaTemplating().registerTag(new EditableTextTag());
        TemplateRenderer.instance().getJinjaTemplating().registerTag(new EditableImageCollectionTag());


        // Load javascript and style bundles


        // PUBLIC STYLESHEETS

        DefinedBundle.getAlwaysHeadStylesheets()
                .add("publisher", "public/comments-public.css")
                .add("publisher", "public/unslider-dots.css")
                .add("publisher", "public/unslider.css")
                .add("publisher", "public/photoswipe.css")
                .add("publisher", "public/default-skin.css")
                .add("publisher", "public/comments-public.css")
                .add("publisher", "public/contacts-always.css");
        DefinedBundle.getAlwaysFooterJavascripts()
                .add("publisher", "public/perfectLayout.min.js")
                .add("publisher", "public/require.js")
                .add("publisher", "public/unslider-min.js")
                .add("publisher", "public/contacts-always.js")
                .add("publisher", "public/widgets.js");


        // PUBLIC COMMENTS STYLESHEETS

        DefinedBundle.register(
                new DefinedBundle("publisher:public.js", ".js",
                        new BundleFile().setPluginName("publisher").setLiveUrl("public/comments-public.js"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("public/comments-public-riot.tag").setProcessor("riot")
                )
        );

        // ADMIN STYLESHEETS AND CSS

        DefinedBundle.register(
                new DefinedBundle("publisher:admin2.js", ".js",
                        new BundleFile().setPluginName("stallion").setLiveUrl("always/jquery-1.11.3.min.js"),
                        //new BundleFile().setPluginName("publisher").setLiveUrl("public/require.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("admin/simplemde.min.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("admin/bootstrap.min.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("always/riot-and-compiler.js").setDebugUrl("always/riot-and-compiler.min.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("always/stallion.js"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("admin/moment.min.js"),

                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/dropzone.js"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/pikaday.js"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/sortable.min.js"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/select2.min.js"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/to-markdown.js"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/jquery.oembed.js"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/common.tag").setProcessor("riot"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/components.tag").setProcessor("riot"),
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
                new DefinedBundle("publisher:admin2.css", ".css",
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/bootstrap.min.css"),

                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/simplemde.min.css"),
                        //new BundleFile().setPluginName("publisher").setLiveUrl("admin/icomoon.css"),
                        //new BundleFile().setPluginName("publisher").setLiveUrl("admin/bootstrap-theme.css"),
                        new BundleFile().setPluginName("stallion").setLiveUrl("v2/pikaday.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/dashboard.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/select2.min.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/admin.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/dropzone.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/admin.css")
                )
        );

        /*
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
                        new BundleFile().setPluginName("publisher").setLiveUrl("v2/select2.min.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/icomoon.css"),
                        //new BundleFile().setPluginName("publisher").setLiveUrl("admin/bootstrap-theme.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/dashboard.css"),
                        new BundleFile().setPluginName("publisher").setLiveUrl("admin/admin.css")
                )
        );
        */


    }
}
