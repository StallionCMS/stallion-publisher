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

import io.stallion.assets.*;
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
import io.stallion.utils.ResourceHelpers;

import java.util.List;

import static io.stallion.utils.Literals.*;

public class PublisherPlugin extends StallionJavaPlugin {

    @Override
    public String getPluginName() {
        return "publisher";
    }

    @Override
    public List<String> getSqlMigrations() {
        return list(
                "00010-create-table-comments",
                "00040-create-table-content_versions",
                "00050-create-table-notifications",
                "00060-create-table-blog_configs",
                "00070-create-table-author_profiles",
                "00080-create-table-uploaded_files",
                "00100-create-table-subscriptions",
                "00110-create-table-contents",
                "00120-create-table-contacts",
                "00130-create-table-form_submissions",
                "00140-create-table-global_modules",
                "00150-create-table-site_settings",
                "00160-create-table-global_module_versions"
        );
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



        BundleRegistry.instance().register(
                new CompiledBundle("admin3",
                        new ResourceBundleFile("publisher", "v3/bootstrap.min.css"),
                        new ResourceBundleFile("publisher", "vendor/simplemde.min.css"),
                        //new BundleFile().setPluginName("publisher", "admin/icomoon.css"),
                        //new BundleFile().setPluginName("publisher", "admin/bootstrap-theme.css"),
                        new ResourceBundleFile("publisher", "vendor/pikaday.css"),
                        new ResourceBundleFile("stallion", "vendor/vue.min.js", "vendor/vue.js"),
                        new ResourceBundleFile("stallion", "vendor/vue-router.min.js", "vendor/vue-router.js"),
                        new ResourceBundleFile("publisher", "vendor/select2.min.css"),
                        new ResourceBundleFile("publisher", "vendor/dropzone.css"),
                        new ResourceBundleFile("publisher", "v3/dashboard.css"),
                        new ResourceBundleFile("publisher", "v3/admin.css"),

                        new ResourceBundleFile("stallion", "always/jquery-1.11.3.min.js"),


                        new ResourceBundleFile("publisher", "vendor/simplemde.min.js"),
                        new ResourceBundleFile("publisher", "vendor/bootstrap.min.js"),
                        //        new BundleFile().setPluginName("stallion", "always/riot-and-compiler.js").setDebugUrl("always/riot-and-compiler.min.js"),
                        new ResourceBundleFile("stallion", "always/stallion.js"),
                        new ResourceBundleFile("stallion", "admin/moment.min.js"),
                        new ResourceBundleFile("publisher", "vendor/dropzone.js"),
                        new ResourceBundleFile("publisher", "vendor/moment-timezone-with-data-2010-2020.min.js"),
                        new ResourceBundleFile("publisher", "vendor/pikaday.js"),
                        new ResourceBundleFile("publisher", "vendor/sortable.min.js"),
                        new ResourceBundleFile("publisher", "vendor/select2.min.js"),
                        new ResourceBundleFile("publisher", "vendor/to-markdown.js"),
                        new ResourceBundleFile("publisher", "vendor/jquery.oembed.js"),
                        new ResourceBundleFile("publisher", "v3/common.js"),
                        new ResourceBundleFile("publisher", "public/require.js"),
                        new ResourceBundleFile("publisher", "v3/imports.js"),
                        new VueResourceBundleFile("publisher", "v3/form-components/*.vue"),
                        new VueResourceBundleFile("publisher", "v3/markdown-editor/*.vue"),
                        new VueResourceBundleFile("publisher", "v3/markdown-editor/tinymce-image-plugin.js"),
                        new VueResourceBundleFile("publisher", "v3/page-editor/*.vue"),
                        new VueResourceBundleFile("publisher", "v3/screens/*.vue"),
                        new VueResourceBundleFile("publisher", "v3/*.vue"),
                        /*
                        new VueResourceBundleFile("publisher", "v3/form-components/modal-base.vue"),
                        new VueResourceBundleFile("publisher", "v3/form-components/author-picker.vue"),
                        new VueResourceBundleFile("publisher", "v3/form-components/autogrow-textarea.vue"),
                        new VueResourceBundleFile("publisher", "v3/form-components/date-picker.vue"),
                        new VueResourceBundleFile("publisher", "v3/form-components/datetime-picker.vue"),
                        new VueResourceBundleFile("publisher", "v3/form-components/image-library.vue"),
                        new VueResourceBundleFile("publisher", "v3/form-components/image-selector.vue"),

                        new VueResourceBundleFile("publisher", "v3/form-components/markdown-basic.vue"),
                        new VueResourceBundleFile("publisher", "v3/form-components/image-simple-formatting.vue"),
                        new VueResourceBundleFile("publisher", "v3/form-components/image-uploader.vue"),
                        new VueResourceBundleFile("publisher", "v3/form-components/image-picker-field.vue"),
                        new VueResourceBundleFile("publisher", "v3/form-components/image-collection-configure.vue"),
                        new VueResourceBundleFile("publisher", "v3/form-components/image-collection-field.vue"),
                        new VueResourceBundleFile("publisher", "v3/form-components/loading-div.vue"),

                        new VueResourceBundleFile("publisher", "v3/markdown-editor/embed-widget-configure.vue"),
                        new VueResourceBundleFile("publisher", "v3/markdown-editor/html-form-widget-configure.vue"),
                        new VueResourceBundleFile("publisher", "v3/markdown-editor/html-widget-configure.vue"),
                        new VueResourceBundleFile("publisher", "v3/markdown-editor/image-collection-widget-configure.vue"),
                        new VueResourceBundleFile("publisher", "v3/markdown-editor/image-collection-widget-modal.vue"),
                        new VueResourceBundleFile("publisher", "v3/markdown-editor/image-full-formatting.vue"),
                        new VueResourceBundleFile("publisher", "v3/markdown-editor/image-widget-configure.vue"),
                        new VueResourceBundleFile("publisher", "v3/markdown-editor/image-widget-modal.vue"),
                        new VueResourceBundleFile("publisher", "v3/markdown-editor/internal-link-picker.vue"),
                        new VueResourceBundleFile("publisher", "v3/markdown-editor/insert-link-modal.vue"),
                        new VueResourceBundleFile("publisher", "v3/markdown-editor/paste-html-modal.vue"),
                        new VueResourceBundleFile("publisher", "v3/markdown-editor/widget-modal.vue"),
                        new VueResourceBundleFile("publisher", "v3/markdown-editor/markdown-editor.vue"),
                        new VueResourceBundleFile("publisher", "v3/page-editor/page-element.vue"),
                        new VueResourceBundleFile("publisher", "v3/page-editor/page-element-text.vue"),
                        new VueResourceBundleFile("publisher", "v3/page-editor/page-element-image.vue"),
                        new VueResourceBundleFile("publisher", "v3/page-editor/page-element-image-collection.vue"),
                        new VueResourceBundleFile("publisher", "v3/page-editor/page-element-markdown.vue"),
                        new VueResourceBundleFile("publisher", "v3/page-editor/version-history.vue"),
                        new VueResourceBundleFile("publisher", "v3/page-editor/page-editor.vue"),
                        new VueResourceBundleFile("publisher", "v3/screens/file-library.vue"),
                        new VueResourceBundleFile("publisher", "v3/screens/file-upload.vue"),
                        new VueResourceBundleFile("publisher", "v3/screens/contents-table.vue"),
                        new VueResourceBundleFile("publisher", "v3/screens/contacts-table.vue"),
                        new VueResourceBundleFile("publisher", "v3/screens/comments-table.vue"),
                        new VueResourceBundleFile("publisher", "v3/screens/new-page.vue"),
                        new VueResourceBundleFile("publisher", "v3/screens/new-post.vue"),
                        new VueResourceBundleFile("publisher", "v3/screens/settings-authors.vue"),
                        new VueResourceBundleFile("publisher", "v3/screens/settings-extra-html.vue"),
                        new VueResourceBundleFile("publisher", "v3/screens/settings-site-information.vue"),
                        new VueResourceBundleFile("publisher", "v3/screens/settings-global-modules.vue"),
                        new VueResourceBundleFile("publisher", "v3/screens/view-content.vue"),
                        new VueResourceBundleFile("publisher", "v3/dashboard-home.vue"),
                        new VueResourceBundleFile("publisher", "v3/sidebar-menu.vue"),
                        //*/
                        new ResourceBundleFile("publisher", "v3/main.js")
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
