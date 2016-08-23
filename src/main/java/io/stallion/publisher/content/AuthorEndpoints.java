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

import java.util.List;
import java.util.Map;

import static io.stallion.utils.Literals.*;

import com.fasterxml.jackson.annotation.JsonView;
import io.stallion.dataAccess.filtering.Pager;
import io.stallion.requests.validators.SafeMerger;
import io.stallion.restfulEndpoints.*;
import io.stallion.services.Log;
import io.stallion.users.IUser;
import io.stallion.users.Role;
import io.stallion.users.User;
import io.stallion.users.UserController;
import io.stallion.utils.json.RestrictedViews;

import javax.ws.rs.*;

@Path("/authors")
@MinRole(Role.ADMIN)
@Produces("application/json")
public class AuthorEndpoints implements EndpointResource {

    @GET
    @Path("/list")
    @JsonView(RestrictedViews.Owner.class)
    public Object listAuthors() {
        List<UserAuthor> authors = AuthorProfileController.instance().listAllAuthors();

        return map(val("pager", new Pager().setCurrentItems(authors)));
    }

    @GET
    @Path("/get/:userId")
    @JsonView(RestrictedViews.Owner.class)
    public Object getAuthorContext(@PathParam("userId") Long userId){

        Map ctx = map();
        ctx.put("user", UserController.instance().forIdOrNotFound(userId));
        ctx.put("profile", AuthorProfileController.instance().getOrCreate(userId));

        return ctx;
    }

    @POST
    @Path("/save")
    public Object save(@ObjectParam SaveData data){
        IUser user;
        if (!empty(data.getUserId())) {
            user = updateUser(data.getUserId(), data.getUser(), data.getAuthorProfile());
        } else {
            user = createUser(data.isInvite(), data.getUser(), data.getAuthorProfile());
        }
        return user;
    }

    public IUser createUser(boolean sendInvite, User updatedUser, AuthorProfile updatedProfile) {
        IUser user = new SafeMerger()
                .email("email")
                .optional("givenName", "familyName", "role")
                .merge(updatedUser);
        AuthorProfile profile = new SafeMerger()
                .optional("bioMarkdown")
                .merge(updatedProfile);
        user.setUsername(user.getEmail());
        user.setDisplayName(user.getGivenName() + " " + user.getFamilyName());
        user.setRole(Role.STAFF);
        user = UserController.instance().createUser(user);
        profile.setUserId(user.getId());
        AuthorProfileController.instance().save(profile);

        return user;
    }

    public IUser updateUser(Long userId, User updatedUser, AuthorProfile updatedProfile) {
        IUser user = UserController.instance().forIdOrNotFound(userId);
        user = new SafeMerger()
                .optional("email", "givenName", "familyName", "role")
                .merge(updatedUser, user);
        AuthorProfile profile = AuthorProfileController.instance().getOrCreate(userId);
        profile = new SafeMerger()
                .optional("bioMarkdown")
                .merge(updatedProfile, profile);
        user.setDisplayName(user.getGivenName() + " " + user.getFamilyName());
        UserController.instance().save(user);
        profile.setUserId(user.getId());
        AuthorProfileController.instance().save(profile);
        return user;
    }


    public static class SaveData {
        private Long userId;
        private User user;
        private AuthorProfile authorProfile;
        private boolean invite = false;

        public boolean isInvite() {
            return invite;
        }

        public SaveData setInvite(boolean invite) {
            this.invite = invite;
            return this;
        }

        public Long getUserId() {
            return userId;
        }

        public SaveData setUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        public User getUser() {
            return user;
        }

        public SaveData setUser(User user) {
            this.user = user;
            return this;
        }

        public AuthorProfile getAuthorProfile() {
            return authorProfile;
        }

        public SaveData setAuthorProfile(AuthorProfile authorProfile) {
            this.authorProfile = authorProfile;
            return this;
        }
    }
}
