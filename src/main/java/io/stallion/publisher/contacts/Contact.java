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

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.stallion.dataAccess.ModelBase;
import io.stallion.dataAccess.UniqueKey;
import io.stallion.dataAccess.db.Converter;
import io.stallion.dataAccess.db.converters.JsonMapConverter;
import io.stallion.email.Contactable;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Map;
import java.util.Set;

import static io.stallion.utils.Literals.map;
import static io.stallion.utils.Literals.set;

@Table(name="contacts")
public class Contact extends ModelBase implements Contactable {
    private String givenName = "";
    private String familyName = "";
    private String email = "";
    private String displayName = "";
    private String webSite = "";
    private String everCookie = "";
    private String filePath = "";
    private String secretToken = "";
    private boolean optedOut = false;
    private boolean disabled = false;
    private boolean totallyOptedOut;
    private String honorific = "";
    private Long optOutDate = 0L;
    private boolean verifiedOptIn = false;
    private long optInAt = 0;
    private long verifySentAt = 0;
    private long verifyRejectedAt = 0;
    private boolean verifiedEmail = false;
    private Map<String, Object> extra = map();

    public final static Set<String> SETTABLE_FIELDS = set("givenName", "familyName", "displayName", "webSite", "honorific");


    @Column(length = 100)
    public String getGivenName() {
        return givenName;
    }

    public Contact setGivenName(String givenName) {
        this.givenName = givenName;
        return this;
    }

    @Column(length = 100)
    public String getFamilyName() {
        return familyName;
    }

    public Contact setFamilyName(String familyName) {
        this.familyName = familyName;
        return this;
    }

    @Column(length = 150)
    @UniqueKey
    public String getEmail() {
        return email;
    }

    public Contact setEmail(String email) {
        this.email = email;
        return this;
    }

    @Column(length = 200)
    public String getDisplayName() {
        return displayName;
    }

    public Contact setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Column(length = 255)
    public String getWebSite() {
        return webSite;
    }

    public Contact setWebSite(String webSite) {
        this.webSite = webSite;
        return this;
    }

    @UniqueKey
    @Column(length = 60)
    @JsonIgnore
    public String getEverCookie() {
        return everCookie;
    }

    public Contact setEverCookie(String everCookie) {
        this.everCookie = everCookie;
        return this;
    }



    @UniqueKey
    @Column(length = 60)
    @JsonIgnore
    public String getSecretToken() {
        return secretToken;
    }

    public Contact setSecretToken(String secretToken) {
        this.secretToken = secretToken;
        return this;
    }


    @Column
    public Long getOptOutDate() {
        return optOutDate;
    }

    public Contact setOptOutDate(Long optOutDate) {
        this.optOutDate = optOutDate;
        return this;
    }

    @Column
    public boolean isVerifiedOptIn() {
        return verifiedOptIn;
    }

    public Contact setVerifiedOptIn(boolean verifiedOptIn) {
        this.verifiedOptIn = verifiedOptIn;
        return this;
    }

    @Column
    public long getOptInAt() {
        return optInAt;
    }

    public Contact setOptInAt(long optInAt) {
        this.optInAt = optInAt;
        return this;
    }

    @Column
    public long getVerifySentAt() {
        return verifySentAt;
    }

    public Contact setVerifySentAt(long verifySentAt) {
        this.verifySentAt = verifySentAt;
        return this;
    }

    @Column
    public long getVerifyRejectedAt() {
        return verifyRejectedAt;
    }

    public Contact setVerifyRejectedAt(long verifyRejectedAt) {
        this.verifyRejectedAt = verifyRejectedAt;
        return this;

    }

    @Column
    public boolean isVerifiedEmail() {
        return verifiedEmail;
    }

    public Contact setVerifiedEmail(boolean verifiedEmail) {
        this.verifiedEmail = verifiedEmail;
        return this;
    }

    @Column
    public boolean isOptedOut() {
        return optedOut;
    }

    public Contact setOptedOut(boolean optedOut) {
        this.optedOut = optedOut;
        return this;
    }

    @Column
    public boolean isDisabled() {
        return disabled;
    }

    public Contact setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    @Column
    public boolean isTotallyOptedOut() {
        return totallyOptedOut;
    }

    public Contact setTotallyOptedOut(boolean totallyOptedOut) {
        this.totallyOptedOut = totallyOptedOut;
        return this;
    }

    @Column(length = 50)
    public String getHonorific() {
        return honorific;
    }

    public Contact setHonorific(String honorific) {
        this.honorific = honorific;
        return this;
    }

    @Column(columnDefinition = "longtext")
    @Converter(cls= JsonMapConverter.class)
    public Map<String, Object> getExtra() {
        return extra;
    }

    public Contact setExtra(Map<String, Object> extra) {
        this.extra = extra;
        return this;
    }
}
