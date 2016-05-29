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

import io.stallion.Context;
import io.stallion.dataAccess.DataAccessRegistry;
import io.stallion.dataAccess.DisplayableModelController;
import io.stallion.dataAccess.ModelBase;
import io.stallion.dataAccess.db.DB;
import io.stallion.requests.ResponseComplete;
import io.stallion.requests.StResponse;
import io.stallion.requests.validators.SafeMerger;
import io.stallion.restfulEndpoints.EndpointResource;
import io.stallion.restfulEndpoints.ObjectParam;
import io.stallion.settings.Settings;
import io.stallion.templating.TemplateRenderer;
import io.stallion.utils.DateUtils;
import io.stallion.utils.GeneralUtils;
import io.stallion.utils.Markdown;

import javax.servlet.ServletOutputStream;
import javax.ws.rs.*;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Map;

import static io.stallion.utils.Literals.*;


public class AdminEndpoints implements EndpointResource {
    @GET
    @Path("/admin")
    @Produces("text/html")
    public String adminPage() {
        Context.getResponse().getPageFooterLiterals().addDefinedBundle("publisher:admin2.js");
        Context.getResponse().getPageHeadLiterals().addDefinedBundle("publisher:admin2.css");
        return TemplateRenderer.instance().renderTemplate("publisher:admin2.jinja");
    }

    @GET
    @Path("/files")
    @Produces("application/json")
    public Object getFiles(@QueryParam("page") Integer page) {
        page = or(page, 1);
        Map ctx =  map(val("pager", UploadedFileController.instance().filterChain().sort("uploadedAt", "desc").pager(page)));
        return ctx;
    }

    @GET
    @Path("/images")
    @Produces("application/json")
    public Object getImageFiles(@QueryParam("page") Integer page) {
        page = or(page, 1);
        Map ctx =  map(val("pager", UploadedFileController.instance().filter("type", "image").sort("uploadedAt", "desc").pager(page, 100)));
        return ctx;
    }



    @POST
    @Path("/upload-file")
    @Produces("application/json")
    public Object uploadFile() {

        String folder = Settings.instance().getDataDirectory() + "/uploaded-files/";
        if (!new File(folder).isDirectory()) {
            new File(folder).mkdirs();
        }
        UploadRequestProcessor processor = new UploadRequestProcessor(Context.getRequest()).uploadToPath(folder);
        Long id = DataAccessRegistry.instance().getTickets().nextId();
        String url = "{cdnUrl}/st-publisher/view-uploaded-file/" + id + "?ts=" + DateUtils.mils();
        UploadedFile uf = new UploadedFile()
                .setName(processor.getFileName())
                .setExtension(processor.getExtension())
                .setCloudKey(processor.getRelativePath())
                .setUploadedAt(DateUtils.utcNow())
                .setType(UploadedFileController.getTypeForExtension(processor.getExtension()))
                .setUrl(url)
                .setId(id);
        UploadedFileController.instance().save(uf);
        return uf;
    }

    @GET
    @Path("/view-uploaded-file/:fileId")
    public Object viewUploadedFile(@PathParam("fileId") Long fileId) {
        String folder = Settings.instance().getDataDirectory() + "/uploaded-files/";
        UploadedFile uf = UploadedFileController.instance().forId(fileId);
        String fullPath = folder + uf.getCloudKey();
        File file = new File(fullPath);
        sendAssetResponse(file);
        return null;
    }


    public void sendAssetResponse(File file) {
        try {
            sendAssetResponse(new FileInputStream(file), file.lastModified(), file.length(), file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendAssetResponse(InputStream stream, long modifyTime, long contentLength, String fullPath) throws IOException {
        // TODO: Merge this with code from stallion core RequestProccessor to eliminate duplication

        StResponse response = Context.getResponse();
        // Set the caching headers
        Long duration = 60 * 60 * 24 * 365 * 10L; // 10 years
        Long durationMils = duration  * 1000;
        response.addHeader("Cache-Control", "max-age=" + duration);
        response.setDateHeader("Expires", System.currentTimeMillis() + durationMils);
        if (modifyTime > 0) {
            response.setDateHeader("Last-Modified", modifyTime);
        }

        // Set the Content-type
        String contentType = GeneralUtils.guessMimeType(fullPath);
        if (empty(contentType)) {
            contentType = Files.probeContentType(FileSystems.getDefault().getPath(fullPath));
        }
        response.setContentType(contentType);

        Integer BUFF_SIZE = 1024;
        byte[] buffer = new byte[BUFF_SIZE];
        ServletOutputStream os = response.getOutputStream();
        response.setContentLength((int)contentLength);

        try {
            int byteRead = 0;
            while(true) {
                byteRead = stream.read(buffer);
                if (byteRead == -1) {
                    break;
                }
                os.write(buffer, 0, byteRead);
            }
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            os.close();
            stream.close();
        }
        throw new ResponseComplete();
    }




    @GET
    @Path("/posts")
    @Produces("application/json")
    public Object getPosts(@QueryParam("page") Integer page) {
        page = or(page, 1);
        Map ctx =  map(val("pager", BlogPostController.instance().filterChain().pager(page)));
        return ctx;
    }



    @GET
    @Path("/posts/:postId/latest-draft")
    @Produces("application/json")
    public BlogPostVersion getPostLatestDraft(@PathParam("postId") Long postId) {
        BlogPostVersion newVersion = BlogPostVersionController.instance().filter("postId", postId).sort("id", "desc").first();
        if (newVersion == null) {
            BlogPost post = BlogPostController.instance().forIdOrNotFound(postId);
            newVersion = BlogPostVersionController.instance().newVersionFromPost(post);
            BlogPostVersionController.instance().save(newVersion);
        }
        return newVersion;
    }

    @POST
    @Path("/posts/new-for-editing")
    @Produces("application/json")
    public Object newPostForEditing() {
        BlogPost post = new BlogPost()
                .setInitialized(false)
                .setAuthorId(Context.getUser().getId())
                .setAuthor(Context.getUser().getDisplayName())
                .setDraft(true)
                .setPreviewKey(GeneralUtils.randomTokenBase32(16))
                .setId(DataAccessRegistry.instance().getTickets().nextId());
        BlogPostController.instance().save(post);
        BlogPostVersion newVersion = BlogPostVersionController.instance().newVersionFromPost(post);
        BlogPostVersionController.instance().save(newVersion);
        return newVersion;
    }


    @GET
    @Path("/posts/:postId/view-version/:versionId")
    @Produces("text/html")
    public String viewPostVersion(@PathParam("postId") Long postId, @PathParam("versionId") Long versionId) {
        BlogPostVersion version = BlogPostVersionController.instance().forIdOrNotFound(versionId);
        BlogPost item = new BlogPost().setId(version.getPostId());
        BlogPostVersionController.instance().updatePostWithVersion(item, version);
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
    @Path("/posts/:postId/publish/:versionId")
    @Produces("application/json")
    public Object publish(@PathParam("postId") Long postId, @PathParam("versionId") Long versionId) {

        BlogPostVersion lastVersion = BlogPostVersionController.instance().forIdOrNotFound(versionId);
        // Make this version a permanent checkpoint;
        lastVersion.setPermanentCheckpoint(true);
        BlogPostVersionController.instance().save(lastVersion);

        BlogPost post = BlogPostController.instance().forId(postId);
        BlogPostVersionController.instance().updatePostWithVersion(post, lastVersion);
        if (post.getPublishDate() == null) {
            post.setPublishDate(DateUtils.utcNow().minusMinutes(1));
        }
        post.setDraft(false);


        BlogPostController.instance().save(post);

        return post;
    }

    @POST
    @Path("/posts/:postId/update-draft")
    @Produces("application/json")
    public Object updateDraft(@PathParam("postId") Long postId, @ObjectParam BlogPostVersion updatedVersion) {

        BlogPostVersion lastVersion = BlogPostVersionController.instance().filter("postId", postId).sort("id", "desc").first();
        BlogPostVersion newVersion;
        if (lastVersion == null) {
            BlogPost post = BlogPostController.instance().forId(postId);
            newVersion = BlogPostVersionController.instance().newVersionFromPost(post);
        } else {
            newVersion = BlogPostVersionController.instance().newVersionFromPostVersion(lastVersion);
        }


        SafeMerger.with().nonNull("title", "originalContent", "widgets").merge(updatedVersion, newVersion);



        newVersion.setContent(Markdown.instance().process(newVersion.getOriginalContent()));
        BlogPostVersionController.instance().save(newVersion);

        ZonedDateTime fifteenAgo = DateUtils.utcNow().minusMinutes(15);
        // Set all other new saved versions in the last fifteen minutes to not be a checkpoint. Thus only the last
        // save in a given session of editing will ever be considered a checkpoint version.
        DB.instance().execute("UPDATE blog_post_versions SET checkpoint=0 WHERE " +
                "postId=? AND checkpoint=1 AND versionAuthorId=? AND versionDate > ? AND id!=? AND permanentCheckpoint=0 ",
                postId, Context.getUser().getId(), new Timestamp(fifteenAgo.toInstant().toEpochMilli()), newVersion.getId());


        // If the blog post is a draft, then we always sync the new version to the post
        BlogPost post = BlogPostController.instance().forId(postId);
        if (post.getDraft()) {
            BlogPostVersionController.instance().updatePostWithVersion(post, newVersion);
            if (post.getInitialized() != true && !empty(post.getTitle()) && !empty(post.getAuthor())) {
                post.setInitialized(true);
            }
            BlogPostController.instance().save(post);
        }

        return newVersion;
    }
}


