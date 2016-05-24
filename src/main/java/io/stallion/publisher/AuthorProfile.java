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
