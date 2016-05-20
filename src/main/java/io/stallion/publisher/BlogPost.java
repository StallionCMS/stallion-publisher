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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.stallion.dataAccess.Displayable;
import io.stallion.dataAccess.MappedModel;
import io.stallion.dataAccess.ModelController;
import io.stallion.dataAccess.StandardDisplayableModel;
import io.stallion.dataAccess.file.TextItem;

import javax.persistence.Column;
import javax.persistence.Table;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;

@Table(name = "blog_posts")
public class BlogPost extends StandardDisplayableModel {
    private Long authorId = 0L;
    private Long updatedAt = 0L;

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
}
