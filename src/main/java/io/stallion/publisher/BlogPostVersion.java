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

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.stallion.dataAccess.AlternativeKey;
import io.stallion.dataAccess.ModelController;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name="blog_post_versions")
public class BlogPostVersion extends BlogPost {
    private Long postId = 0L;
    private ZonedDateTime versionDate;
    private Boolean checkpoint = false;
    private Long versionAuthorId = 0L;
    private String versionAuthorName = "";
    private Boolean permanentCheckpoint = false;

    @Column
    public String getSlug() {
        return super.getSlug();
    }

    @Column
    @AlternativeKey
    public Long getPostId() {
        return postId;
    }

    public BlogPostVersion setPostId(Long postId) {
        this.postId = postId;
        return this;
    }

    @Column
    public ZonedDateTime getVersionDate() {
        return versionDate;
    }

    public BlogPostVersion setVersionDate(ZonedDateTime versionDate) {
        this.versionDate = versionDate;
        return this;
    }

    @Column
    @AlternativeKey
    public Boolean getCheckpoint() {
        return checkpoint;
    }

    public BlogPostVersion setCheckpoint(Boolean checkpoint) {
        this.checkpoint = checkpoint;
        return this;
    }


    @Column
    public Long getVersionAuthorId() {
        return versionAuthorId;
    }

    public BlogPostVersion setVersionAuthorId(Long versionAuthorId) {
        this.versionAuthorId = versionAuthorId;
        return this;
    }

    @Column
    public String getVersionAuthorName() {
        return versionAuthorName;
    }

    public BlogPostVersion setVersionAuthorName(String versionAuthorName) {
        this.versionAuthorName = versionAuthorName;
        return this;
    }

    @Override
    @JsonIgnore
    public ModelController getController() {
        return BlogPostVersionController.instance();
    }

    @Column
    public Boolean getPermanentCheckpoint() {
        return permanentCheckpoint;
    }

    public BlogPostVersion setPermanentCheckpoint(Boolean permanentCheckpoint) {
        this.permanentCheckpoint = permanentCheckpoint;
        return this;
    }
}

