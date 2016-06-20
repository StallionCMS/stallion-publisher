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

import io.stallion.plugins.BasePluginSettings;
import io.stallion.publisher.content.BlogConfig;
import io.stallion.settings.SettingMeta;

import java.util.ArrayList;
import java.util.List;


public class PublisherSettings extends BasePluginSettings {
    @SettingMeta(valBoolean = false, help="If true, will display a gravtar image next to users images.")
    private boolean commentsUseGravatar = false;
    @SettingMeta(cls=ArrayList.class)
    public List<BlogConfig> blogs;

    @SettingMeta(cls=ArrayList.class)
    private List<String> notifyEmails;
    @SettingMeta()
    private Boolean doubleOptIn;
    @SettingMeta()
    private Boolean moderationEnabled = null;
    @SettingMeta(valInt = 90)
    private Integer commentsClosedAfterDays = null;
    @SettingMeta()
    private Boolean requireValidEmail = null;
    @SettingMeta()
    private String akismetKey = null;
    @SettingMeta()
    private String reCaptchaSecret = null;
    @SettingMeta()
    private String reCaptchaSiteKey = null;
    @SettingMeta(val="comments")
    private String folderName = null;
    @SettingMeta()
    private String realIpHeader;
    @SettingMeta(cls=ArrayList.class)
    private List<String> moderatorEmails = null;


    @SettingMeta(val="blog_posts")
    private String blogPostTableName;

    public static PublisherSettings getInstance() {
        return getInstance(PublisherSettings.class, "publisher");
    }

    public void assignDefaults() {

    }

    public boolean isCommentsUseGravatar() {
        return commentsUseGravatar;
    }

    public PublisherSettings setCommentsUseGravatar(boolean commentsUseGravatar) {
        this.commentsUseGravatar = commentsUseGravatar;
        return this;
    }

    public List<BlogConfig> getBlogs() {
        return blogs;
    }

    public PublisherSettings setBlogs(List<BlogConfig> blogs) {
        this.blogs = blogs;
        return this;
    }

    public String getBlogPostTableName() {
        return blogPostTableName;
    }

    public void setBlogPostTableName(String blogPostTableName) {
        this.blogPostTableName = blogPostTableName;
    }


    public List<String> getNotifyEmails() {
        return notifyEmails;
    }

    public void setNotifyEmails(List<String> notifyEmails) {
        this.notifyEmails = notifyEmails;
    }

    public Boolean getDoubleOptIn() {
        return doubleOptIn;
    }

    public void setDoubleOptIn(Boolean doubleOptIn) {
        this.doubleOptIn = doubleOptIn;
    }

    public Boolean getModerationEnabled() {
        return moderationEnabled;
    }

    public void setModerationEnabled(Boolean moderationEnabled) {
        this.moderationEnabled = moderationEnabled;
    }

    public int getCommentsClosedAfterDays() {
        return commentsClosedAfterDays;
    }

    public void setCommentsClosedAfterDays(int commentsClosedAfterDays) {
        this.commentsClosedAfterDays = commentsClosedAfterDays;
    }

    public Boolean getRequireValidEmail() {
        return requireValidEmail;
    }

    public void setRequireValidEmail(Boolean requireValidEmail) {
        this.requireValidEmail = requireValidEmail;
    }

    public String getAkismetKey() {
        return akismetKey;
    }

    public void setAkismetKey(String akismetKey) {
        this.akismetKey = akismetKey;
    }


    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }


    public String getRealIpHeader() {
        return realIpHeader;
    }

    public void setRealIpHeader(String realIpHeader) {
        this.realIpHeader = realIpHeader;
    }

    public String getReCaptchaSecret() {
        return reCaptchaSecret;
    }

    public void setReCaptchaSecret(String reCaptchaSecret) {
        this.reCaptchaSecret = reCaptchaSecret;
    }

    public List<String> getModeratorEmails() {
        return moderatorEmails;
    }

    public void setModeratorEmails(List<String> moderatorEmails) {
        this.moderatorEmails = moderatorEmails;
    }

    public String getReCaptchaSiteKey() {
        return reCaptchaSiteKey;
    }

    public void setReCaptchaSiteKey(String reCaptchaSiteKey) {
        this.reCaptchaSiteKey = reCaptchaSiteKey;
    }

}

