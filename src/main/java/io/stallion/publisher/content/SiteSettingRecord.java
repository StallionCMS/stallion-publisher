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
import io.stallion.dataAccess.UniqueKey;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name="stallion_publisher_site_settings")
public class SiteSettingRecord extends ModelBase {

    private String name = "";
    private String value = "";

    @Column(length = 75)
    @UniqueKey
    public String getName() {
        return name;
    }

    public SiteSettingRecord setName(String name) {
        this.name = name;
        return this;
    }

    @Column(columnDefinition = "longtext")
    public String getValue() {
        return value;
    }

    public SiteSettingRecord setValue(String value) {
        this.value = value;
        return this;
    }
}
