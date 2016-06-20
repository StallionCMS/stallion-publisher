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

package io.stallion.publisher.content;

import java.io.*;

import io.stallion.requests.IRequest;
import io.stallion.services.Log;
import io.stallion.utils.DateUtils;
import io.stallion.utils.GeneralUtils;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.http.Part;


public class UploadRequestProcessor {
    private IRequest stRequest;
    private String fileName = "";
    private String relativePath = "";
    private String extension = "";

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
        stRequest.setAsMultiPartRequest();

        final String path = stRequest.getParameter("destination");
        final Part filePart = stRequest.getPart("file");
        fileName = getFileNameFromPart(filePart);
        extension = FilenameUtils.getExtension(fileName);
        relativePath = GeneralUtils.slugify(FilenameUtils.getBaseName(fileName)) + "-" + DateUtils.mils() + "." + extension;
        if (basePath.endsWith("/")) {
            basePath += "/";
        }
        String destPath = basePath + relativePath;

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

    public String getFileName() {
        return fileName;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public String getExtension() {
        return extension;
    }
}
