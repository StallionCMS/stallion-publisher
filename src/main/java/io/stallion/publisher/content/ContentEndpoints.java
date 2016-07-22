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

import io.stallion.Context;
import io.stallion.dataAccess.DataAccessRegistry;
import io.stallion.dataAccess.DisplayableModelController;
import io.stallion.dataAccess.ModelBase;
import io.stallion.dataAccess.db.DB;
import io.stallion.dataAccess.filtering.FilterChain;
import io.stallion.dataAccess.filtering.Or;
import io.stallion.dataAccess.filtering.Pager;
import io.stallion.exceptions.*;
import io.stallion.exceptions.NotFoundException;
import io.stallion.requests.ResponseComplete;
import io.stallion.requests.ServletFileSender;
import io.stallion.requests.StResponse;
import io.stallion.requests.validators.SafeMerger;
import io.stallion.restfulEndpoints.*;
import io.stallion.services.Log;
import io.stallion.settings.Settings;
import io.stallion.templating.TemplateRenderer;
import io.stallion.users.Role;
import io.stallion.users.UserController;
import io.stallion.utils.DateUtils;
import io.stallion.utils.GeneralUtils;
import io.stallion.utils.Markdown;
import io.stallion.utils.Sanitize;
import io.stallion.utils.json.JSON;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.ws.rs.*;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static io.stallion.utils.Literals.*;

@MinRole(Role.STAFF_LIMITED)
@Produces("application/json")
@Path("/content")
public class ContentEndpoints implements EndpointResource {




    @GET
    @Path("/active-authors")
    @Produces("application/json")
    public Object listAuthors()  {

        Map ctx =  map(val("authors", AuthorProfileController.instance().listAllAuthors()));
        return ctx;
    }

    @GET
    @Path("/all-live-contents")
    @Produces("application/json")
    public Object allContent(@QueryParam("page") Integer page) {
        page = or(page, 1);
        Map ctx =  map(val("pager", ContentController.instance().filter("published", true).pager(page, 1000)));
        return ctx;
    }






    @GET
    @Path("/posts")
    @Produces("application/json")
    public Object getPosts(@QueryParam("page") Integer page, @QueryParam("search") String search) {
        page = or(page, 1);
        FilterChain<Content> chain = ContentController.instance().filter("type", "post").filter("initialized", true);
        if (!empty(search)) {
            chain = chain.search(search, "title");
        }
        Map ctx =  map(val("pager", chain.sort("updatedAt", "desc").pager(page)));
        return ctx;
    }



    @GET
    @Path("/pages")
    @Produces("application/json")
    public Object getPages(@QueryParam("page") Integer page, @QueryParam("search") String search) {
        page = or(page, 1);
        FilterChain<Content> chain = ContentController.instance().filter("type", "page").filter("initialized", true);
        if (!empty(search)) {
            chain = chain.search(search, "title");
        }
        Map ctx =  map(val("pager", chain.sort("updatedAt", "desc").pager(page)));
        return ctx;
    }

    @GET
    @Path("/list-blogs")
    @Produces("application/json")
    public Object listBlogs() {

        Map ctx =  map(val("blogs", BlogConfigController.instance().filterChain().sort("id", "desc").all()));
        return ctx;
    }





    @GET
    @Path("/:postId/latest-draft")
    @Produces("application/json")
    public Map<String, Object> getPostLatestDraft(@PathParam("postId") Long postId) {
        ContentVersion newVersion = ContentsVersionController.instance().filter("postId", postId).sort("versionDate", "desc").first();
        if (newVersion == null) {
            Content post = ContentController.instance().forIdOrNotFound(postId);
            newVersion = ContentsVersionController.instance().newVersionFromPost(post);
            ContentsVersionController.instance().save(newVersion);
        }

        Map<String, Object> renderContext = map();

        /*
        List<PageElement> elements = list(
                new PageElement()
                        .setName("features")
                        .setRawContent("### Features\n\n* Faster\n* Stronger\n\n"),
                new PageElement()
                        .setName("testimonials")
                        .setRawContent("### Testimonials\n\n* This rocks\n* Best thing I've ever used.\n\n")
        );
        */
        List<PageElement> elements = list();

        renderContext.put(EditableMarkdownTag.ELEMENTS_CONTEXT_VAR, elements);
        try {
            ContentController.instance().render(newVersion, renderContext);
        } catch (Exception e) {
            Log.exception(e, "Error rendering item " + newVersion.getId());
        }




        for(PageElement ele: elements) {
            PageElement savedEle = newVersion.getElement(ele.getName());
            if (emptyInstance(savedEle)) {
                continue;
            }
            ele.setRawContent(savedEle.getRawContent());
            ele.setWidgets(savedEle.getWidgets());
            ele.setContent(savedEle.getContent());
            ele.setData(savedEle.getData());
        }
        newVersion.setElements(elements);

        Map context = map(val("post", newVersion));

        String template = ContentController.instance().getTemplate(newVersion);
        Boolean useMarkdown = null;
        boolean contentEditable = true;
        for (PageTemplateDefinition def: TemplateConfig.instance().getPageTemplates()) {
            if (def.getTemplate().equals(template)) {
                useMarkdown = def.getUseMarkdown();
                contentEditable = def.isContentEditable();
            }
        }
        context.put("useMarkdown", useMarkdown);
        context.put("contentEditable", contentEditable);
        //context.put("templateElements", elements);
        return context;
    }

    @POST
    @Path("/new-for-editing")
    @Produces("application/json")
    public Object newPostForEditing(
            @BodyParam(value = "blogId", allowEmpty = true) Long blogId,
            @BodyParam(value = "type", allowEmpty = true) String type,
            @BodyParam(value="cloneId", allowEmpty = true) Long cloneId,
            @BodyParam(value="template", allowEmpty = true) String template) {
        type = or(type, "post");
        if ("post".equals("type") && empty(blogId)) {
            BlogConfig blogConfig = BlogConfigController.instance().filterChain().sort("id", "ASC").first();
            if (blogConfig != null) {
                blogId = blogConfig.getId();
            }
        }
        Content post;
        if (!empty(cloneId)) {
            post = JSON.parse(JSON.stringify(ContentController.instance().forIdOrNotFound(cloneId)), Content.class);
            post
                    .setAuthorId(Context.getUser().getId())
                    .setAuthor(Context.getUser().getDisplayName())
                    .setDraft(true)
                    .setPreviewKey(GeneralUtils.randomTokenBase32(16))
                    .setPublishDate(null)
                    .setOldUrls(list())
                    .setId(DataAccessRegistry.instance().getTickets().nextId());
            post.setSlug(post.getSlug() + "-" + post.getId());
        } else {
            post = new Content()
                    .setInitialized(false)
                    .setBlogId(blogId)
                    .setType(type)
                    .setAuthorId(Context.getUser().getId())
                    .setAuthor(Context.getUser().getDisplayName())
                    .setContent("")
                    .setOriginalContent("")
                    .setDraft(true)
                    .setPreviewKey(GeneralUtils.randomTokenBase32(16))
                    .setId(DataAccessRegistry.instance().getTickets().nextId());
            if ("post".equals("type")) {
                post.setTitle("Untitled post created at " + DateUtils.formatLocalDateFromLong(DateUtils.mils(), "MMMM d h:mm a"));
                post.setContent("<p>This is a brand new post. This is the content of the post. You can edit it.</p>");
                post.setOriginalContent("This is a brand new post. This is the content of the post. You can edit it.");
            } else {
                post.setTitle("Untitled page created at " + DateUtils.formatLocalDateFromLong(DateUtils.mils(), "MMMM d h:mm a"));
                post.setContent("<p>This is a brand new page. This is the content of the page. You can edit it.</p>");
                post.setOriginalContent("This is a brand new post. This is the content of the post. You can edit it.");
            }

        }
        if (!empty(template)) {
             post.setTemplate(template);
        }
        ContentController.instance().save(post);
        ContentVersion newVersion = ContentsVersionController.instance().newVersionFromPost(post);
        ContentsVersionController.instance().save(newVersion);
        return newVersion;
    }


    @GET
    @Path("/choose-page-template-context")
    @Produces("application/json")
    public Object chosePageTemplateContext() {
        Map ctx = map();
        ctx.put("templates", filter(TemplateConfig.instance().getPageTemplates(), pt->!pt.isSpecial()));
        List<PageTemplateDefinition> specialTemplates = filter(TemplateConfig.instance().getPageTemplates(), pt->pt.isSpecial());
        specialTemplates.add(new PageTemplateDefinition().setContentEditable(true).setSpecial(true).setTemplate("publisher:/page-templates/blank.jinja"));
        ctx.put("specialTemplates", specialTemplates);
        ctx.put("recentPages", ContentController.instance().pages().sort("updatedAt", "desc").pager(1, 3).getItems());
        return ctx;
    }

    @GET
    @Path("/:postId/load-versions")
    @Produces("application/json")
    public Map loadVersions(@PathParam("postId") Long postId, @QueryParam("loadAll") Boolean loadAll) {

        FilterChain<ContentVersion> chain = ContentsVersionController.instance().filter("postId", postId);
        if (loadAll != true) {
            chain = chain.andAnyOf(new Or("checkpoint", true), new Or("versionDate", DateUtils.utcNow().minusMinutes(30), ">"));
        }
        Pager<ContentVersion> pager = chain.sort("id", "desc").pager(1, 200);
        Map ctx = map(val("pager", pager));
        return ctx;

    }

    @GET
    @Path("/:postId/preview")
    @Produces("text/html")
    public String previewPost(@PathParam("postId") Long postId, @PathParam("versionId") Long versionId) {
        return viewPostVersion(postId, 0L);
    }

    @GET
    @Path("/:postId/view-latest-version")
    @Produces("text/html")
    public String viewLatestVersion(@PathParam("postId") Long postId) {
        ContentVersion newVersion = ContentsVersionController.instance().filter("postId", postId).sort("versionDate", "desc").first();
        if (newVersion == null) {
            Content post = ContentController.instance().forIdOrNotFound(postId);
            newVersion = ContentsVersionController.instance().newVersionFromPost(post);
            ContentsVersionController.instance().save(newVersion);
        }
        return viewPostVersion(postId, newVersion.getId());
    }

    @GET
    @Path("/:postId/view-version/:versionId")
    @Produces("text/html")
    public String viewPostVersion(@PathParam("postId") Long postId, @PathParam("versionId") Long versionId) {
        Content item;
        if (!empty(versionId)) {
            ContentVersion version = ContentsVersionController.instance().originalForId(versionId);
            if (version == null) {
                throw new io.stallion.exceptions.NotFoundException("Could not find versionId " + versionId);
            }
            item = new Content().setId(version.getPostId());
            ContentsVersionController.instance().updatePostWithVersion(item, version);
        } else {
            item = ContentController.instance().forId(postId);
        }

        // TODO: Share this logic with Stallion RequestProcessor??
        StResponse response = Context.getResponse();
        Map ctx = map(val("page", item), val("post", item), val("item", item));
        response.getMeta().setDescription(item.getMetaDescription());
        if (!empty(item.getTitleTag())) {
            response.getMeta().setTitle(item.getTitleTag());
        } else {
            response.getMeta().setTitle(item.getTitle());
        }
        response.getMeta().setBodyCssId(item.getSlugForCssId());
        response.getMeta().getCssClasses().add("st-" + ((ModelBase) item).getController().getBucket());
        response.getMeta().setOgType(item.getOgType());
        if (!empty(item.getRelCanonical())) {
            response.getMeta().setCanonicalUrl(item.getRelCanonical());
        }
        if (!empty(item.getContentType())) {
            response.setContentType(item.getContentType());
        }

        String output = ((DisplayableModelController)item.getController()).render(item, ctx);
        return output;
    }

    @POST
    @Path("/:postId/publish/:versionId")
    @Produces("application/json")
    public Object publish(@PathParam("postId") Long postId, @PathParam("versionId") Long versionId) {

        ContentVersion lastVersion = ContentsVersionController.instance().forIdOrNotFound(versionId);
        // Make this version a permanent checkpoint;

        Content post = ContentController.instance().forId(postId);
        if (post.getPublished()  && !post.getSlug().equals(lastVersion.getSlug())) {
            post.getOldUrls().add(post.getSlug());
        }
        ContentsVersionController.instance().updatePostWithVersion(post, lastVersion);
        post.setInitialized(true);
        if (post.getPublishDate() == null || (!post.getScheduled() && post.getDraft())) {
            post.setPublishDate(DateUtils.utcNow().minusMinutes(1));
            post.setScheduled(true);
        }
        post.setUpdatedAt(DateUtils.mils());
        post.setDraft(false);


        ContentController.instance().save(post);

        lastVersion.setScheduled(post.getScheduled());
        lastVersion.setPublishDate(post.getPublishDate());
        lastVersion.setDraft(false);
        lastVersion.setUpdatedAt(post.getUpdatedAt());
        lastVersion.setPermanentCheckpoint(true);
        lastVersion.setOldUrls(post.getOldUrls());
        ContentsVersionController.instance().save(lastVersion);


        return post;
    }

    @POST
    @Path("/make-version-most-recent")
    @Produces("application/json")
    public Object makeVersionMostRecent(@BodyParam("postId") Long postId, @BodyParam("versionId") Long versionId) {
        ContentVersion version = ContentsVersionController.instance().forId(versionId);
        ContentVersion newVersion = ContentsVersionController.instance().newVersionFromPostVersion(version);
        newVersion.setPermanentCheckpoint(true);
        newVersion.setDiff("Restoration of version " + DateUtils.formatLocalDate(version.getVersionDate()));
        ContentsVersionController.instance().save(newVersion);

        // If the blog post is a draft, then we always sync the new version to the post
        Content post = ContentController.instance().forId(postId);
        if (post.getDraft()) {
            ContentsVersionController.instance().updatePostWithVersion(post, newVersion);
            if (post.getInitialized() != true && !empty(post.getTitle()) && !empty(post.getAuthor())) {
                post.setInitialized(true);
            }
            ContentController.instance().save(post);
        }

        return newVersion;
    }

    @POST
    @Path("/:postId/update-draft")
    @Produces("application/json")
    public Object updateDraft(@PathParam("postId") Long postId, @ObjectParam ContentVersion updatedVersion) {

        ContentVersion lastVersion = ContentsVersionController.instance().filter("postId", postId).sort("id", "desc").first();
        ContentVersion newVersion;
        if (lastVersion == null) {
            Content post = ContentController.instance().forId(postId);
            newVersion = ContentsVersionController.instance().newVersionFromPost(post);
        } else {
            newVersion = ContentsVersionController.instance().newVersionFromPostVersion(lastVersion);
        }

        newVersion.setDiff(truncateSmart(Sanitize.stripAll(StringUtils.difference(newVersion.getOriginalContent(), updatedVersion.getOriginalContent())), 250));
        SafeMerger.with()
                .nonNull("title", "originalContent", "widgets", "metaDescription", "headHtml", "footerHtml", "elements")
                .optional("slug", "authorId", "scheduled", "publishDate", "featuredImage", "slugTouched")
                .merge(updatedVersion, newVersion);



        newVersion.setContent(Markdown.instance().process(newVersion.getOriginalContent()));
        for(PageElement ele: newVersion.getElements()) {
            // TODO: use an enum, or class hierarchy or something a little less hacky
            if (ele.getType().equals("markdown")) {
                ele.setContent(Markdown.instance().process(ele.getRawContent()));
            }
        }
        ContentsVersionController.instance().save(newVersion);

        ZonedDateTime fifteenAgo = DateUtils.utcNow().minusMinutes(15);
        // Set all other new saved versions in the last fifteen minutes to not be a checkpoint. Thus only the last
        // save in a given session of editing will ever be considered a checkpoint version.
        DB.instance().execute("UPDATE content_versions SET checkpoint=0 WHERE " +
                "postId=? AND checkpoint=1 AND versionAuthorId=? AND versionDate > ? AND id!=? AND permanentCheckpoint=0 ",
                postId, Context.getUser().getId(), new Timestamp(fifteenAgo.toInstant().toEpochMilli()), newVersion.getId());


        // If the blog post is a draft, then we always sync the new version to the post
        Content post = ContentController.instance().forId(postId);
        if (post.getDraft()) {
            ContentsVersionController.instance().updatePostWithVersion(post, newVersion);
            if (post.getInitialized() != true && !empty(post.getTitle())) {
                post.setInitialized(true);
            }
            post.setUpdatedAt(DateUtils.mils());
            ContentController.instance().save(post);
        } else {
            post.setUpdatedAt(DateUtils.mils());
            ContentController.instance().save(post);
        }

        return newVersion;
    }


    @GET
    @Path("/list-global-modules")
    @MinRole(Role.STAFF)
    public Object listGlobalModules() {
        Map ctx = map(val("modules", TemplateConfig.instance().getGlobalModules()));
        return ctx;
    }


    @GET
    @Path("/authors")
    @Produces("application/json")
    public Object listAuthors(@QueryParam("page") Integer page, @QueryParam("withDeleted") Boolean withDeleted) {
        page = or(page, 1);
        Map ctx =  map(val("pager", UserController.instance()
                .filterChain()
                //.filterBy("role", Role.STAFF_LIMITED, FilterOperator.GREATER_THAN_OR_EQUAL)
                .andAnyOf(new Or("role", Role.STAFF_LIMITED), new Or("role", Role.STAFF), new Or("role", Role.ADMIN), new Or("role", Role.HOST))
                .pager(page, 100)));
        return ctx;
    }


    public ContentController contents() {
        return ContentController.instance();
    }

    public ContentsVersionController contentVerions() {
        return ContentsVersionController.instance();
    }

}


