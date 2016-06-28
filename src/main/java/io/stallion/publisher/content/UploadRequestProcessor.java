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

import java.awt.image.BufferedImage;
import java.io.*;

import io.stallion.dataAccess.DataAccessRegistry;
import io.stallion.requests.IRequest;
import io.stallion.services.Log;
import io.stallion.utils.DateUtils;
import io.stallion.utils.GeneralUtils;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.parboiled.common.FileUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.Part;


public class UploadRequestProcessor {
    private IRequest stRequest;
    private UploadedFile uploaded = new UploadedFile();

    public UploadRequestProcessor(IRequest stRequest) {
        this.stRequest = stRequest;
    }

    public UploadRequestProcessor uploadToPath(String basePath) {
        try {
            doUpload(basePath);
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void doUpload(String basePath) throws IOException {
        Long id = DataAccessRegistry.instance().getTickets().nextId();
        String url = "{cdnUrl}/st-publisher/view-uploaded-file/" + id + "?ts=" + DateUtils.mils();

        stRequest.setAsMultiPartRequest();

        final String path = stRequest.getParameter("destination");
        final Part filePart = stRequest.getPart("file");
        String fileName = getFileNameFromPart(filePart);
        String extension = FilenameUtils.getExtension(fileName);
        String relativePath = GeneralUtils.slugify(FilenameUtils.getBaseName(fileName)) + "-" + DateUtils.mils() + "." + extension;
        if (basePath.endsWith("/")) {
            basePath += "/";
        }
        String destPath = basePath + relativePath;

        uploaded
                .setCloudKey(relativePath)
                .setExtension(extension)
                .setName(fileName)
                .setUploadedAt(DateUtils.utcNow())
                .setType(UploadedFileController.getTypeForExtension(extension))
                .setRawUrl(url)
                .setId(id)
        ;

        OutputStream out = null;
        InputStream filecontent = null;

        try {
            out = new FileOutputStream(destPath);
            filecontent = filePart.getInputStream();

            int read = 0;
            final byte[] bytes = new byte[1024];

            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            Log.info("File{0}being uploaded to {1}",
                    new Object[]{fileName, path});

        } finally {
            if (out != null) {
                out.close();
            }
            if (filecontent != null) {
                filecontent.close();
            }
        }
        if ("image".equals(uploaded.getType())) {
            handleImage(destPath);
        }

    }

    protected void handleImage(String path) {
        try {
            File originalImageFile = new File(path);
            BufferedImage image = ImageIO.read(originalImageFile);
            hydrateHeightAndWidth(image);
            if (uploaded.getWidth() > 60) {
                createResized(image, path, 120, 60, "thumb");
            }
            if (uploaded.getWidth() > 250) {
                createResized(image, path, 550, 250, "small");
            }

            if (uploaded.getWidth() > 900) {
                createResized(image, path, 1600, 900, "medium");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    protected void hydrateHeightAndWidth(BufferedImage image) throws IOException {
        uploaded.setHeight(image.getHeight());
        uploaded.setWidth(image.getWidth());
    }

    public void createResized(BufferedImage image, String orgPath, int targetHeight, int targetWidth, String postfix) throws IOException {
        String imageFormat = uploaded.getExtension();

        BufferedImage scaledImg = Scalr.resize(image, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH,
                targetWidth, targetHeight, Scalr.OP_ANTIALIAS);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int height = scaledImg.getHeight();
        int width = scaledImg.getWidth();
        ImageIO.write(scaledImg, imageFormat, baos);
        baos.flush();
        byte[] scaledImageInByte = baos.toByteArray();
        baos.close();

        String base = FilenameUtils.removeExtension(orgPath);
        String thumbnailPath = base + "." + postfix + "." + uploaded.getExtension();
        Log.info("Write all byptes to {0}", thumbnailPath);
        FileUtils.writeAllBytes(scaledImageInByte, new File(thumbnailPath));

        String relativePath = FilenameUtils.getBaseName(uploaded.getCloudKey()) + "." + postfix + "." + uploaded.getExtension();
        String url = "{cdnUrl}/st-publisher/view-uploaded-file/" + uploaded.getId() + "/" + postfix + "?ts=" + DateUtils.mils();
        if (postfix.equals("thumb")) {
            uploaded.setThumbCloudKey(relativePath);
            uploaded.setThumbRawUrl(url);
            uploaded.setThumbHeight(height);
            uploaded.setThumbWidth(width);
        } else if (postfix.equals("small")) {
            uploaded.setSmallCloudKey(relativePath);
            uploaded.setSmallRawUrl(url);
            uploaded.setSmallHeight(height);
            uploaded.setSmallWidth(width);
        } else {
            uploaded.setMediumCloudKey(relativePath);
            uploaded.setMediumRawUrl(url);
            uploaded.setMediumHeight(height);
            uploaded.setMediumWidth(width);
        }


        //return scaledImageInByte;
    }


    private String getFileNameFromPart(final Part part) {
        final String partHeader = part.getHeader("content-disposition");
        Log.info("Part Header = {0}", partHeader);
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    public UploadedFile getUploaded() {
        return this.uploaded;
    }
}
