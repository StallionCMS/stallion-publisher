/*
 * Copyright (c) 2015-2016 Patrick Fitzsimmons
 *
 * This file is part of Stallion Publisher.
 *
 * Stallion Publisher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.stallion.publisher;

import io.stallion.dataAccess.ModelBase;

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
