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

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.stallion.dataAccess.ModelController;
import io.stallion.dataAccess.StandardDisplayableModel;
import io.stallion.dataAccess.db.Converter;
import io.stallion.dataAccess.db.converters.JsonListConverter;

import javax.persistence.Column;
import javax.persistence.Table;

import java.util.List;
import java.util.Map;

import static io.stallion.utils.Literals.*;

@Table(name = "blog_posts")
public class BlogPost extends StandardDisplayableModel {
    private Long authorId = 0L;
    private Long updatedAt = 0L;
    private List<Map> widgets = list();
    private Boolean initialized = true;

    @Column
    public Long getAuthorId() {
        return authorId;
    }

    public BlogPost setAuthorId(Long authorId) {
        this.authorId = authorId;
        return this;
    }

    @Column
    public Long getUpdatedAt() {
        return updatedAt;
    }

    public BlogPost setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    //@JsonGetter("currentlyPublished")
    //private boolean currentlyPublished = false;


    public boolean isCurrentlyPublished() {
        return getPublished();
    }

    public BlogPost setCurrentlyPublished(boolean currentlyPublished) {
        //this.currentlyPublished = currentlyPublished;
        return this;
    }

    @Override
    @JsonIgnore
    public ModelController getController() {
        return BlogPostController.instance();
    }

    @Column(columnDefinition = "longtext")
    @Converter(cls= JsonListConverter.class)
    public List<Map> getWidgets() {
        return widgets;
    }

    public BlogPost setWidgets(List<Map> widgets) {
        this.widgets = widgets;
        return this;
    }

    @Column
    public Boolean getInitialized() {
        return initialized;
    }

    public BlogPost setInitialized(Boolean initialized) {
        this.initialized = initialized;
        return this;
    }
}
