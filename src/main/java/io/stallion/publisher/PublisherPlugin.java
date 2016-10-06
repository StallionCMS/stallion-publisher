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
import io.stallion.boot.StallionRunAction;
import io.stallion.exceptions.ConfigException;
import io.stallion.hooks.HookRegistry;
import io.stallion.plugins.StallionJavaPlugin;
import io.stallion.publisher.comments.CommentsController;
import io.stallion.publisher.comments.CommentsEndpoints;
import io.stallion.publisher.comments.CommentsTag;
import io.stallion.publisher.comments.SeleniumEndpoints;
import io.stallion.publisher.contacts.*;
import io.stallion.publisher.content.*;
import io.stallion.publisher.liveTesting.TomeEndpoints;
import io.stallion.publisher.tools.NewPublisherSiteAction;
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
    public List<? extends StallionRunAction> getActions() {
        return list(new NewPublisherSiteAction());
    }

    @Override
    public List<String> getSqlMigrations() {
        return list(
                "00010-create-table-stallion_publisher_comments",
                "00020-create-table-stallion_publisher_content_versions",
                "00030-create-table-stallion_publisher_notifications",
                "00040-create-table-stallion_publisher_blog_configs",
                "00050-create-table-stallion_publisher_author_profiles",
                "00060-create-table-stallion_publisher_uploaded_files",
                "00070-create-table-stallion_publisher_subscriptions",
                "00080-create-table-stallion_publisher_contents",
                "00090-create-table-stallion_publisher_contacts",
                "00100-create-table-stallion_publisher_form_submissions",
                "00110-create-table-stallion_publisher_global_modules",
                "00120-create-table-stallion_publisher_site_settings",
                "00130-create-table-stallion_publisher_global_module_versions",
                "00140-alter-stallion_publisher_uploaded_files-add-1"
        );
    }

    @Override
    public void boot() throws Exception {
        validateSettings();

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


        // Hooks
        HookRegistry.instance().register(new ContentSlugLookupHook());


        // Register endpoints


        List<EndpointResource> resources = list(
                new MainEndpoints(),
                new ConfigEndpoints(),
                new UploadedFileEndpoints(),
                new ContentEndpoints(),
                new CommentsEndpoints(),
                new ContactsEndpoints(),
                new GlobalModuleEndpoints(),
                new SeleniumEndpoints(),
                new TomeEndpoints(),
                new AuthorEndpoints()
        );
        for (EndpointResource resource: resources) {
            Log.finer("Register resource {0}", resource.getClass().getName());
            ResourceToEndpoints converter = new ResourceToEndpoints("/st-publisher");
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
        TemplateRenderer.instance().getJinjaTemplating().registerTag(new EditableRichTextTag());
        TemplateRenderer.instance().getJinjaTemplating().registerTag(new EditableImageCollectionTag());


        // Load javascript and style bundles


    }

    private void validateSettings() {
        PublisherSettings publisherSettings = PublisherSettings.getInstance();
        Settings settings = Settings.instance();
        if (publisherSettings.isUseCloudStorageForUploads())
        {
            if (settings.getCloudStorage() == null || empty(settings.getCloudStorage().getAccessToken())) {
                throw new ConfigException("In your publisher.toml you enabled the setting for cloud storage, but you did not define a [cloudStorage] section in stallion.toml with an accessTokena and secretKey setting.");
            }
            if (empty(publisherSettings.getUploadsBucket())) {
                throw new ConfigException("You did not define a setting in publisher.toml called 'uploadsBucket' which could contain the s3 bucket to upload files to.");
            }
        }

    }
}
