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

import io.stallion.dataAccess.ModelBase;
import io.stallion.settings.Settings;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.ZonedDateTime;

import static io.stallion.utils.Literals.empty;

@Table(name="uploaded_files")
public class UploadedFile extends ModelBase {
    private String name = "";
    private String rawUrl = "";
    private String cloudKey = "";
    private String type = "";
    private String extension = "";
    private ZonedDateTime uploadedAt;
    private Integer height = 0;
    private Integer width = 0;

    // Thumb limited to 350px wide or 250px tall
    private String thumbCloudKey = "";
    private String thumbRawUrl = "";
    private Integer thumbWidth = 0;
    private Integer thumbHeight = 0;

    private String smallCloudKey = "";
    private String smallRawUrl = "";
    private Integer smallWidth = 0;
    private Integer smallHeight = 0;


    // Medium is limited to 900px wide
    private String mediumCloudKey = "";
    private Integer mediumWidth = 0;
    private Integer mediumHeight = 0;
    private String mediumRawUrl = "";

    @Column
    public String getName() {
        return name;
    }

    public UploadedFile setName(String name) {
        this.name = name;
        return this;
    }

    public String getUrl() {
        return rawUrl.replace("{cdnUrl}", Settings.instance().getCdnUrl());
    }

    public String getThumbUrl() {
        if (empty(getThumbRawUrl())) {
            return getUrl();
        } else {
            return getThumbRawUrl().replace("{cdnUrl}", Settings.instance().getCdnUrl());
        }
    }

    public String getSmallUrl() {
        if (empty(getSmallRawUrl())) {
            return getUrl();
        } else {
            return getSmallRawUrl().replace("{cdnUrl}", Settings.instance().getCdnUrl());
        }

    }


    public String getMediumUrl() {
        if (empty(getMediumRawUrl())) {
            return getUrl();
        } else {
            return getMediumRawUrl().replace("{cdnUrl}", Settings.instance().getCdnUrl());
        }

    }


    @Column
    public String getRawUrl() {
        return rawUrl;
    }

    public UploadedFile setRawUrl(String rawUrl) {
        this.rawUrl = rawUrl;
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

    @Column
    public Integer getHeight() {
        return height;
    }

    public UploadedFile setHeight(Integer height) {
        this.height = height;
        return this;
    }

    @Column
    public Integer getWidth() {
        return width;
    }

    public UploadedFile setWidth(Integer width) {
        this.width = width;
        return this;
    }

    @Column
    public String getThumbCloudKey() {
        return thumbCloudKey;
    }

    public UploadedFile setThumbCloudKey(String thumbCloudKey) {
        this.thumbCloudKey = thumbCloudKey;
        return this;
    }

    @Column
    public String getThumbRawUrl() {
        return thumbRawUrl;
    }

    public UploadedFile setThumbRawUrl(String thumbRawUrl) {
        this.thumbRawUrl = thumbRawUrl;
        return this;
    }

    @Column
    public Integer getThumbWidth() {
        return thumbWidth;
    }

    public UploadedFile setThumbWidth(Integer thumbWidth) {
        this.thumbWidth = thumbWidth;
        return this;
    }

    @Column
    public Integer getThumbHeight() {
        return thumbHeight;
    }

    public UploadedFile setThumbHeight(Integer thumbHeight) {
        this.thumbHeight = thumbHeight;
        return this;
    }

    @Column
    public String getMediumCloudKey() {
        return mediumCloudKey;
    }

    public UploadedFile setMediumCloudKey(String mediumCloudKey) {
        this.mediumCloudKey = mediumCloudKey;
        return this;
    }

    @Column
    public Integer getMediumWidth() {
        return mediumWidth;
    }

    public UploadedFile setMediumWidth(Integer mediumWidth) {
        this.mediumWidth = mediumWidth;
        return this;
    }

    @Column
    public Integer getMediumHeight() {
        return mediumHeight;
    }

    public UploadedFile setMediumHeight(Integer mediumHeight) {
        this.mediumHeight = mediumHeight;
        return this;
    }

    @Column
    public String getMediumRawUrl() {
        return mediumRawUrl;
    }

    public UploadedFile setMediumRawUrl(String mediumRawUrl) {
        this.mediumRawUrl = mediumRawUrl;
        return this;
    }

    @Column
    public String getSmallCloudKey() {
        return smallCloudKey;
    }

    public UploadedFile setSmallCloudKey(String smallCloudKey) {
        this.smallCloudKey = smallCloudKey;
        return this;
    }

    @Column
    public String getSmallRawUrl() {
        return smallRawUrl;
    }

    public UploadedFile setSmallRawUrl(String smallRawUrl) {
        this.smallRawUrl = smallRawUrl;
        return this;
    }

    @Column
    public Integer getSmallWidth() {
        return smallWidth;
    }

    public UploadedFile setSmallWidth(Integer smallWidth) {
        this.smallWidth = smallWidth;
        return this;
    }

    @Column
    public Integer getSmallHeight() {
        return smallHeight;
    }

    public UploadedFile setSmallHeight(Integer smallHeight) {
        this.smallHeight = smallHeight;
        return this;
    }
}
