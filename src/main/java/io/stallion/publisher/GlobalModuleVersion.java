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

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import io.stallion.services.Log;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name="global_module_versions")
public class GlobalModuleVersion extends GlobalModule {
    private Long globalModuleId;
    private ZonedDateTime versionDate;
    private Long authorId;
    private String authorName;

    @Column
    public Long getGlobalModuleId() {
        return globalModuleId;
    }

    public GlobalModuleVersion setGlobalModuleId(Long globalModuleId) {
        this.globalModuleId = globalModuleId;
        return this;
    }

    @Column
    public String getName() {
        return super.getName();
    }

    @Column
    public ZonedDateTime getVersionDate() {
        return versionDate;
    }

    public GlobalModuleVersion setVersionDate(ZonedDateTime versionDate) {
        this.versionDate = versionDate;
        return this;
    }

    @Column
    public Long getAuthorId() {
        return authorId;
    }

    public GlobalModuleVersion setAuthorId(Long authorId) {
        this.authorId = authorId;
        return this;
    }

    @Column
    public String getAuthorName() {
        return authorName;
    }

    public GlobalModuleVersion setAuthorName(String authorName) {
        this.authorName = authorName;
        return this;
    }
}
