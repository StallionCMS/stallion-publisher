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

import io.stallion.dataAccess.AlternativeKey;
import io.stallion.dataAccess.ModelBase;
import io.stallion.dataAccess.db.Converter;
import io.stallion.dataAccess.db.converters.JsonMapConverter;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;

@Table(name="form_submissions")
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
    @Column
    public String getEmail() {
        return email;
    }

    public FormSubmission setEmail(String email) {
        this.email = email;
        return this;
    }

    @AlternativeKey
    @Column
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

    @Column
    public String getFormName() {
        return formName;
    }

    public FormSubmission setFormName(String formName) {
        this.formName = formName;
        return this;
    }


    @Column
    public String getPageUrl() {
        return pageUrl;
    }

    public FormSubmission setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    @Column
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
