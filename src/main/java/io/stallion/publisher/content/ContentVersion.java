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

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.stallion.dataAccess.AlternativeKey;
import io.stallion.dataAccess.ModelController;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name="blog_post_versions")
public class ContentVersion extends Content {
    private Long postId = 0L;
    private ZonedDateTime versionDate;
    private Boolean checkpoint = false;
    private Long versionAuthorId = 0L;
    private String versionAuthorName = "";
    private Boolean permanentCheckpoint = false;
    private String diff = "";
    private Integer wordCount = 0;

    @Column
    public String getSlug() {
        return super.getSlug();
    }

    @Column
    @AlternativeKey
    public Long getPostId() {
        return postId;
    }

    public ContentVersion setPostId(Long postId) {
        this.postId = postId;
        return this;
    }

    @Column
    public ZonedDateTime getVersionDate() {
        return versionDate;
    }

    public ContentVersion setVersionDate(ZonedDateTime versionDate) {
        this.versionDate = versionDate;
        return this;
    }

    @Column
    @AlternativeKey
    public Boolean getCheckpoint() {
        return checkpoint;
    }

    public ContentVersion setCheckpoint(Boolean checkpoint) {
        this.checkpoint = checkpoint;
        return this;
    }


    @Column
    public Long getVersionAuthorId() {
        return versionAuthorId;
    }

    public ContentVersion setVersionAuthorId(Long versionAuthorId) {
        this.versionAuthorId = versionAuthorId;
        return this;
    }

    @Column
    public String getVersionAuthorName() {
        return versionAuthorName;
    }

    public ContentVersion setVersionAuthorName(String versionAuthorName) {
        this.versionAuthorName = versionAuthorName;
        return this;
    }

    @Override
    @JsonIgnore
    public ModelController getController() {
        return ContentsVersionController.instance();
    }

    @Column
    public Boolean getPermanentCheckpoint() {
        return permanentCheckpoint;
    }

    public ContentVersion setPermanentCheckpoint(Boolean permanentCheckpoint) {
        this.permanentCheckpoint = permanentCheckpoint;
        return this;
    }

    @Column
    public String getDiff() {
        return diff;
    }

    public ContentVersion setDiff(String diff) {
        this.diff = diff;
        return this;
    }

    @Column()
    public Integer getWordCount() {
        return wordCount;
    }

    public ContentVersion setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
        return this;
    }
}

