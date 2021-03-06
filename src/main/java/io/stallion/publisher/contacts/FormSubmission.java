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
import io.stallion.dataAccess.db.Converter;
import io.stallion.dataAccess.db.converters.JsonMapConverter;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

@Table(name="stallion_publisher_form_submissions")
public class FormSubmission extends ModelBase {

    private String email = "";
    private String everCookie = "";
    private Long contactId = 0L;
    private Long submittedAt = 0L;
    private Map<String, Object> data = new HashMap<String, Object>();
    private String formName = "";
    private String pageUrl = "";
    private String pageTitle = "";
    private String formId = "";


    @AlternativeKey
    @Column(length = 150)
    public String getEmail() {
        return email;
    }

    public FormSubmission setEmail(String email) {
        this.email = email;
        return this;
    }

    @AlternativeKey
    @Column(length = 60)
    public String getEverCookie() {
        return everCookie;
    }

    public FormSubmission setEverCookie(String everCookie) {
        this.everCookie = everCookie;
        return this;
    }

    @AlternativeKey
    @Column
    public Long getContactId() {
        return contactId;
    }

    public FormSubmission setContactId(Long contactId) {
        this.contactId = contactId;
        return this;
    }


    @Column(columnDefinition = "longtext")
    @Converter(cls= JsonMapConverter.class)
    public Map<String, Object> getData() {
        return data;
    }

    public FormSubmission setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    @Column(length = 100)
    public String getFormName() {
        return formName;
    }

    public FormSubmission setFormName(String formName) {
        this.formName = formName;
        return this;
    }


    @Column(length = 255)
    public String getPageUrl() {
        return pageUrl;
    }

    public FormSubmission setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    @Column(length = 255)
    public String getPageTitle() {
        return pageTitle;
    }

    public FormSubmission setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
        return this;
    }

    @Column(length = 50)
    public String getFormId() {
        return formId;
    }

    public FormSubmission setFormId(String formId) {
        this.formId = formId;
        return this;
    }

    @Column
    public Long getSubmittedAt() {
        return submittedAt;
    }

    public FormSubmission setSubmittedAt(Long submittedAt) {
        this.submittedAt = submittedAt;
        return this;
    }

}
