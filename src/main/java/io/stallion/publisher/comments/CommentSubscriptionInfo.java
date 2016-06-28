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

package io.stallion.publisher.comments;

import io.stallion.publisher.contacts.SubscriptionFrequency;


public class CommentSubscriptionInfo {
    private SubscriptionFrequency replyNotifyFrequency = SubscriptionFrequency.DAILY;
    private SubscriptionFrequency threadNotifyFrequency = SubscriptionFrequency.NEVER;
    private boolean blogSubscribe = false;


    public SubscriptionFrequency getReplyNotifyFrequency() {
        return replyNotifyFrequency;
    }

    public CommentSubscriptionInfo setReplyNotifyFrequency(SubscriptionFrequency replyNotifyFrequency) {
        this.replyNotifyFrequency = replyNotifyFrequency;
        return this;
    }


    public SubscriptionFrequency getThreadNotifyFrequency() {
        return threadNotifyFrequency;
    }

    public CommentSubscriptionInfo setThreadNotifyFrequency(SubscriptionFrequency threadNotifyFrequency) {
        this.threadNotifyFrequency = threadNotifyFrequency;
        return this;
    }


    public boolean isBlogSubscribe() {
        return blogSubscribe;
    }

    public CommentSubscriptionInfo setBlogSubscribe(Boolean blogSubscribe) {
        if (blogSubscribe == null) {
            blogSubscribe = false;
        }
        this.blogSubscribe = blogSubscribe;
        return this;
    }
}
