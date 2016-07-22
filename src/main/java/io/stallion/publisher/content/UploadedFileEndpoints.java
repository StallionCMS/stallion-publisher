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
import static io.stallion.Context.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import io.stallion.Context;
import io.stallion.dataAccess.filtering.FilterChain;
import io.stallion.exceptions.*;
import io.stallion.requests.ResponseComplete;
import io.stallion.requests.ServletFileSender;
import io.stallion.restfulEndpoints.EndpointResource;
import io.stallion.restfulEndpoints.MinRole;
import io.stallion.services.Log;
import io.stallion.settings.Settings;
import io.stallion.users.Role;

import javax.ws.rs.*;
import javax.ws.rs.NotFoundException;

@MinRole(Role.STAFF_LIMITED)
@Produces("application/json")
@Path("/files")
public class UploadedFileEndpoints implements EndpointResource {
    @GET
    @Path("/library")
    @Produces("application/json")
    @MinRole(Role.MEMBER)
    public Object getFiles(@QueryParam("page") Integer page) {
        page = or(page, 1);
        FilterChain<UploadedFile> chain = UploadedFileController.instance().filterChain();
        if (!Context.getUser().isInRole(Role.STAFF_LIMITED)) {
            chain = chain.filter("ownerId", Context.getUser().getId());
        }
        Map ctx =  map(val("pager", chain.sort("uploadedAt", "desc").pager(page, 100)));
        return ctx;
    }


    @GET
    @Path("/images")
    @Produces("application/json")
    @MinRole(Role.MEMBER)
    public Object getImageFiles(@QueryParam("page") Integer page) {
        page = or(page, 1);
        FilterChain<UploadedFile> chain = UploadedFileController.instance().filter("type", "image");
        if (!Context.getUser().isInRole(Role.STAFF_LIMITED)) {
            chain = chain.filter("ownerId", Context.getUser().getId());
        }
        Map ctx =  map(val("pager", chain.sort("uploadedAt", "desc").pager(page, 100)));
        return ctx;
    }



    @POST
    @Path("/upload-file")
    @Produces("application/json")
    @MinRole(Role.MEMBER)
    public Object uploadFile() {

        String folder = Settings.instance().getDataDirectory() + "/uploaded-files/";
        if (!new File(folder).isDirectory()) {
            new File(folder).mkdirs();
        }
        UploadRequestProcessor processor = new UploadRequestProcessor(folder, Context.getRequest()).upload();
        UploadedFile uf = processor.getUploaded();
        UploadedFileController.instance().save(uf);
        return uf;
    }

    @GET
    @Path("/view/:secret/:fileId/thumb")
    @MinRole(Role.ANON)
    public Object viewUploadedThumbFile(@PathParam("secret") String secret, @PathParam("fileId") Long fileId) {
        String folder = Settings.instance().getDataDirectory() + "/uploaded-files/";
        UploadedFile uf = UploadedFileController.instance().forId(fileId);
        if (empty(uf.getThumbCloudKey())) {
            throw new io.stallion.exceptions.NotFoundException("File not found.");
        }
        if (!uf.getSecret().equals(secret)) {
            throw new ClientException("Invalid file token.");
        }
        String fullPath = folder + uf.getThumbCloudKey();
        File file = new File(fullPath);
        sendAssetResponse(file);
        throw new ResponseComplete();
    }

    @GET
    @Path("/view/:secret/:fileId/medium")
    @MinRole(Role.ANON)
    public Object viewUploadedMediumFile(@PathParam("secret") String secret, @PathParam("fileId") Long fileId) {
        String folder = Settings.instance().getDataDirectory() + "/uploaded-files/";
        UploadedFile uf = UploadedFileController.instance().forId(fileId);
        if (empty(uf.getMediumCloudKey())) {
            throw new io.stallion.exceptions.NotFoundException("File not found.");
        }
        if (!uf.getSecret().equals(secret)) {
            throw new ClientException("Invalid file token.");
        }

        String fullPath = folder + uf.getMediumCloudKey();
        File file = new File(fullPath);
        sendAssetResponse(file);
        throw new ResponseComplete();
    }

    @GET
    @Path("/view/:secret/:fileId/:slug")
    @MinRole(Role.ANON)
    public Object viewUploadedFile(@PathParam("secret") String secret, @PathParam("fileId") Long fileId, @PathParam("slug") String slug) {
        String folder = Settings.instance().getDataDirectory() + "/uploaded-files/";
        UploadedFile uf = UploadedFileController.instance().forIdOrNotFound(fileId);
        if (!uf.getSecret().equals(secret)) {
            throw new ClientException("Invalid file token.");
        }

        String fullPath = folder + uf.getCloudKey();
        File file = new File(fullPath);
        sendAssetResponse(file);
        throw new ResponseComplete();
    }


    public void sendAssetResponse(File file) {
        try {
            sendAssetResponse(new FileInputStream(file), file.lastModified(), file.length(), file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendAssetResponse(InputStream stream, long modifyTime, long contentLength, String fullPath) throws IOException {
        new ServletFileSender(Context.getRequest(), Context.getResponse()).sendAssetResponse(stream, modifyTime, contentLength, fullPath);
    }


}
