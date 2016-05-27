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
import io.stallion.requests.ResponseComplete;
import io.stallion.requests.StResponse;
import io.stallion.requests.validators.SafeMerger;
import io.stallion.restfulEndpoints.EndpointResource;
import io.stallion.restfulEndpoints.ObjectParam;
import io.stallion.restfulEndpoints.SlugRegistry;
import io.stallion.services.Log;
import io.stallion.settings.Settings;
import io.stallion.templating.TemplateRenderer;
import io.stallion.testing.TestClient;
import io.stallion.utils.DateUtils;
import io.stallion.utils.GeneralUtils;
import io.stallion.utils.Markdown;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletOutputStream;
import javax.ws.rs.*;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;


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
    @Path("/posts/:postId")
    @Produces("application/json")
    public Object getPost(@PathParam("postId") Long postId) {
        return BlogPostController.instance().forId(postId);
    }


    @POST
    @Path("/posts/:postId/update-draft")
    public Object updateDraft(@PathParam("postId") Long postId, @ObjectParam BlogPost updatedPost) {
        BlogPost post = BlogPostController.instance().forId(postId);
        SafeMerger.with().nonNull("title", "originalContent", "widgets").merge(updatedPost, post);
        post.setContent(Markdown.instance().process(post.getOriginalContent()));
        BlogPostController.instance().save(post);

        BlogPost retrieved = BlogPostController.instance().originalForId(postId);
        BlogPost fromSlug = (BlogPost)SlugRegistry.instance().lookup(post.getSlug());
        Log.info("param: {0} forId: {1} orgForId: {2} fromSlug: {3}",
                System.identityHashCode(updatedPost),
                System.identityHashCode(post),
                System.identityHashCode(retrieved),
                System.identityHashCode(fromSlug)
                );

        //TestClient client = new TestClient();
        //Log.info("Content: {0}", client.get(post.getSlug()).getContent());

        return post;
    }
}


