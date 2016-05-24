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
import io.stallion.dataAccess.UniqueKey;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Table;

import static io.stallion.utils.Literals.list;

@Table(name="author_profiles")
public class AuthorProfile extends ModelBase {
    private Long userId;
    private String bio;
    private String bioMarkdown;

    @UniqueKey
    @Column
    public Long getUserId() {
        return userId;
    }

    public AuthorProfile setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    @Column(columnDefinition = "longtext")
    public String getBio() {
        return bio;
    }

    public AuthorProfile setBio(String bio) {
        this.bio = bio;
        return this;
    }

    @Column(columnDefinition = "longtext")
    public String getBioMarkdown() {
        return bioMarkdown;
    }

    public AuthorProfile setBioMarkdown(String bioMarkdown) {
        this.bioMarkdown = bioMarkdown;
        return this;
    }
}
