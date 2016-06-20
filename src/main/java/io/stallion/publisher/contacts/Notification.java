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

package io.stallion.publisher.contacts;

import io.stallion.dataAccess.AlternativeKey;
import io.stallion.dataAccess.MappedModel;
import io.stallion.dataAccess.ModelBase;
import io.stallion.dataAccess.UniqueKey;
import io.stallion.utils.json.JSON;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name="notifications")
public class Notification extends ModelBase {
    private String key;
    private Long contactId;
    private String subscriptionId;

    private boolean seen = false;
    private boolean sent = false;
    private Long sendAt = 0L;
    private Long sentAt = 0L;
    private Long createdAt = 0L;
    private String callbackClassName = "";
    private String callbackPlugin = null;
    private SubscriptionFrequency frequency = SubscriptionFrequency.DAILY;
    private String periodKey = "";
    private String extraData = "";

    @Column
    @UniqueKey
    public String getKey() {
        return key;
    }

    public Notification setKey(String key) {
        this.key = key;
        return this;
    }

    @Column
    public String getSubscriptionId() {
        return subscriptionId;
    }

    public Notification setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
        return this;
    }

    @Column
    public boolean isSeen() {
        return seen;
    }

    public Notification setSeen(boolean seen) {
        this.seen = seen;
        return this;
    }

    @Column
    public boolean isSent() {
        return sent;
    }

    public Notification setSent(boolean sent) {
        this.sent = sent;
        return this;
    }

    @Column
    public Long getSendAt() {
        return sendAt;
    }

    public Notification setSendAt(Long sendAt) {
        this.sendAt = sendAt;
        return this;
    }

    @Column
    public Long getSentAt() {
        return sentAt;
    }

    public Notification setSentAt(Long sentAt) {
        this.sentAt = sentAt;
        return this;
    }

    @Column
    public Long getCreatedAt() {
        return createdAt;
    }

    public Notification setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @Column
    public String getCallbackClassName() {
        return callbackClassName;
    }

    public Notification setCallbackClassName(String callbackClassName) {
        this.callbackClassName = callbackClassName;
        return this;
    }

    @Column
    public String getCallbackPlugin() {
        return callbackPlugin;
    }

    public Notification setCallbackPlugin(String callbackPlugin) {
        this.callbackPlugin = callbackPlugin;
        return this;
    }

    @Column
    public SubscriptionFrequency getFrequency() {
        return frequency;
    }

    public Notification setFrequency(SubscriptionFrequency frequency) {
        this.frequency = frequency;
        return this;
    }

    @Column
    @AlternativeKey
    public String getPeriodKey() {
        return periodKey;
    }

    public Notification setPeriodKey(String periodKey) {
        this.periodKey = periodKey;
        return this;
    }

    public Notification setHandler(NotificationCallbackHandlerInterface handler) {
        this.setExtraData(JSON.stringify(handler));
        this.setCallbackClassName(handler.getClass().getCanonicalName());
        return this;
    }


    public Notification setExtraData(String extraData) {
        this.extraData = extraData;
        return this;
    }

    @Column(columnDefinition = "longtext")
    public String getExtraData() {
        return extraData;
    }

    @Column
    public Long getContactId() {
        return contactId;
    }

    public Notification setContactId(Long contactId) {
        this.contactId = contactId;
        return this;
    }
}
