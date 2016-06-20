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


public class NotificationCallbackResult {
    private String completeEmailSubject = "";
    private String emailBody = "";
    private String thing = "";
    private String thingPlural = "";

    public String getCompleteEmailSubject() {
        return completeEmailSubject;
    }

    public NotificationCallbackResult setCompleteEmailSubject(String completeEmailSubject) {
        this.completeEmailSubject = completeEmailSubject;
        return this;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public NotificationCallbackResult setEmailBody(String emailBody) {
        this.emailBody = emailBody;
        return this;
    }

    public String getThing() {
        return thing;
    }

    public NotificationCallbackResult setThing(String thing) {
        this.thing = thing;
        return this;
    }

    public String getThingPlural() {
        return thingPlural;
    }

    public NotificationCallbackResult setThingPlural(String thingPlural) {
        this.thingPlural = thingPlural;
        return this;
    }
}
