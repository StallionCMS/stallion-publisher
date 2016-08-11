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

package io.stallion.publisher.contacts;


import io.stallion.dataAccess.AlternativeKey;
import io.stallion.dataAccess.ModelBase;
import io.stallion.dataAccess.UniqueKey;
import io.stallion.dataAccess.file.ModelWithFilePath;
import io.stallion.utils.GeneralUtils;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name="stallion_publisher_subscriptions")
public class Subscription extends ModelBase  {
    private String name;
    private String ownerKey;
    private boolean enabled = true;
    private Long optInDate = 0L;
    private Long optOutDate = 0L;
    private Long createdAt = 0L;
    private Long contactId;
    private SubscriptionFrequency frequency = SubscriptionFrequency.INSTANT;
    private String filePath = "";
    private boolean canChangeFrequency = true;

    @Column(length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @UniqueKey
    @Column(length = 150)
    public String getOwnerKey() {
        return ownerKey;
    }

    public void setOwnerKey(String ownerKey) {
        this.ownerKey = ownerKey;
    }

    @Column
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }



    @AlternativeKey
    @Column
    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    @Column(length = 40)
    public SubscriptionFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(SubscriptionFrequency frequency) {
        this.frequency = frequency;
    }

    @Column
    public Long getOptInDate() {
        return optInDate;
    }

    public void setOptInDate(Long optInDate) {
        this.optInDate = optInDate;
    }

    @Column
    public Long getOptOutDate() {
        return optOutDate;
    }

    public void setOptOutDate(Long optOutDate) {
        this.optOutDate = optOutDate;
    }

    @Column
    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    @Column
    public String generateFilePath() {
        return GeneralUtils.slugify(getOwnerKey()) + "---" + getId() + ".json";
    }



    @Column
    public boolean isCanChangeFrequency() {
        return canChangeFrequency;
    }

    public void setCanChangeFrequency(boolean canChangeFrequency) {
        this.canChangeFrequency = canChangeFrequency;
    }
}
