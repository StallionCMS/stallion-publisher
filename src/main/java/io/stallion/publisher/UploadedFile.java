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

import io.stallion.dataAccess.ModelBase;
import io.stallion.settings.Settings;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.ZonedDateTime;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;

@Table(name="uploaded_files")
public class UploadedFile extends ModelBase {
    private String name = "";
    private String url = "";
    private String cloudKey = "";
    private String type = "";
    private String extension = "";
    private ZonedDateTime uploadedAt;

    @Column
    public String getName() {
        return name;
    }

    public UploadedFile setName(String name) {
        this.name = name;
        return this;
    }

    public String getFullUrl() {
        return url.replace("{cdnUrl}", Settings.instance().getCdnUrl());
    }

    @Column
    public String getUrl() {
        return url;
    }

    public UploadedFile setUrl(String url) {
        this.url = url;
        return this;
    }

    @Column
    public String getCloudKey() {
        return cloudKey;
    }

    public UploadedFile setCloudKey(String cloudKey) {
        this.cloudKey = cloudKey;
        return this;
    }

    @Column
    public String getType() {
        return type;
    }

    public UploadedFile setType(String type) {
        this.type = type;
        return this;
    }

    @Column
    public String getExtension() {
        return extension;
    }

    public UploadedFile setExtension(String extension) {
        this.extension = extension;
        return this;
    }

    @Column
    public ZonedDateTime getUploadedAt() {
        return uploadedAt;
    }

    public UploadedFile setUploadedAt(ZonedDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
        return this;
    }
}
