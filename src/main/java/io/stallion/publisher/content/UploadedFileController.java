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

import io.stallion.dataAccess.DataAccessRegistry;
import io.stallion.dataAccess.DataAccessRegistration;
import io.stallion.dataAccess.StandardModelController;

import java.util.Map;

import static io.stallion.utils.Literals.*;


public class UploadedFileController extends StandardModelController<UploadedFile> {
    public static UploadedFileController instance() {
        return (UploadedFileController) DataAccessRegistry.instance().get("uploaded_files");
    }
    public static void register() {
        DataAccessRegistration registration = new DataAccessRegistration()
                .setBucket("uploaded_files")
                .setModelClass(UploadedFile.class)
                .setControllerClass(UploadedFileController.class);
        DataAccessRegistry.instance().register(registration);
    }

    public static String getTypeForExtension(String ext) {
        return extensionToFileType.getOrDefault(ext.toLowerCase(), "other");
    }

    public static Map<String, String> extensionToFileType = map(
            val("png", "image"),
            val("svg", "image"),
            val("jpg", "image"),
            val("gif", "image"),
            val("doc", "document"),
            val("pdf", "document"),
            val("docx", "document"),
            val("xls", "document"),
            val("xlsx", "document"),
            val("numbers", "document")
    );
}
