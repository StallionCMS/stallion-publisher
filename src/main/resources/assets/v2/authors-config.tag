<authors-config>
    <h3>All Authors</h3>
    <div class="form-group">
        <a href="#/settings/new-author" class="btn btn-default btn-xl">Create New Author</a>
    </div>
    <table class="users-table table table-striped">
        <thead>
            <tr>
                <th>
                    Username
                </th>
                <th>
                    Display name
                </th>
                <th>
                    Email
                </th>
                <th>
                    Role
                </th>
                <th>
                    Created
                </th>
                <th>
                    Status
                </th>
            </tr>
        </thead>
        <tbody if={loading}>
            <tr if={loading}>
                <td colspan="5">Loading users…</td>
            </tr>
        </tbody>
        <tbody if={!loading && users.length === 0}>
            <tr>
                <td colspan="5">No users found</td>
            </tr>
        </tbody>
        <tbody if={!loading && users.length > 0}>
            <tr each={user in users}  class="clickable-row user-row user-row-{user.id}" onclick={rowClick} data-user-id="{user.id}">
                <td>{user.username}</td>
                <td>{user.displayName}</td>
                <td>{user.email}</td>
                <td>{user.role}</td>
                <td>{formatCreatedAt(user.createdAt)}</td>
                <td class="user-status">
                    
                    <span if={!user.deleted && !user.disabled && user.approved && !user.predefined}>normal</span>
                    <span if={user.deleted}>deleted</span>
                    <span if={user.predefined}>built-in user</span>
                    <span if={user.disabled}>disabled</span>
                    <span if={!user.approved}>pending approval</span>
                </td>
            </tr>
        </tbody>
        <tfoot if={pager}>
            <tr>
                <td colspan="6" if={pager.pageCount > 1}>
                    <a class={pager-link-text: true, pager-link: true, current-page: page==1} href="#/1">First</a>
                    <a each={num in pager.surroundingPages} href="#/settings/authors/{num}" class={pager-link: true, current-page: num==page}>
                        {num}
                    </a>
                    <a class={pager-link-text: true, pager-link: true, current-page: page==pager.pageCount} href="#/{pager.pageCount}">Last</a>
                </td>
            </tr>
            <tr>
                <td colspan="6">
                    <label><input type="checkbox" id="include-deleted" onclick={includeDeleted} > Show deleted users?</label>
                </td>
            </tr>
        </tfoot>
    </table>
    <script>
     var self = this;
     self.pager = null;
     self.users = [];
     self.loading = true;
     self.page = self.opts.page || 1;
     self.withDeleted = 'false';

     rowClick = function(evt) {
         var userId = parseInt($(evt.target).parents('.user-row').attr('data-user-id'), 10);
         window.location.hash = "#/settings/author/" + userId;
     }
     
     self.formatCreatedAt = function(mils, format) {
         var format = format || "mmm d, yyyy";
         if (mils === 0) {
             return '';
         }
         return dateFormat(new Date(mils), format);
     };

     self.includeDeleted = function(evt) {
         self.withDeleted = $(evt.target).is(':checked');
         self.fetchData();
     };

     this.fetchData = function() {
         stallion.request({
             url: '/st-publisher/authors?page=' + self.page + '&withDeleted=' + self.withDeleted,
             success: function (o) {
                 self.pager = o.pager;
                 self.users = self.pager.items;
                 self.loading = false;
                 self.update();
             }
         });

     };
     
     this.on('mount', function(){
         self.fetchData();
     });
    </script>
</authors-config>

<user-view>
    <h3 if={loading}>Loading user…</h3>
    <div>
        <a href="#/settings/authors">&#171; return to all users</a>
    </div>
    <div class="pure-g"  if={!loading}>
        <div class="pure-u-2-3">
            <h3 if={!userId}>Create new user</h3>            
            <h3 if={userId}>Edit user: {user.email} - {user.displayName}</h3>
            <form id="st-update-user-form" name="updateUserForm" class="pure-form pure-form-stacked" onsubmit={submit}>
                <fieldset>
                    <div class="st-bottom-space">
                        <label for="displayName">Display Name</label>
                        <input name="displayName" type="text" class="pure-input-1">
                    </div>
                    <div class="st-bottom-space">
                        <label for="givenName">Given Name</label>
                        <input name="givenName" type="text" class="pure-input-1">
                    </div class="st-bottom-space">
                    <div class="st-bottom-space">
                        <label for="familyName">Family Name</label>
                        <input name="familyName" type="text" class="pure-input-1">
                    </div>
                    <div class="st-bottom-space">
                        <label for="email">Email</label>
                        <input name="email" type="email" placeholder="Email" class="pure-input-1">
                    </div>
                    <div class="st-bottom-space">
                        <label for="email">Username</label>
                        <input name="username" type="text" class="pure-input-1">
                    </div>
                    <div class="st-bottom-space">
                        <label for="state">Role</label>
                        <select name="role" class="pure-input-1">
                            <option value="ANON">ANON</option>
                            <option value="CONTACT">CONTACT</option>
                            <option value="REGISTERED">REGISTERED</option>
                            <option value="MEMBER">MEMBER</option>
                            <option value="STAFF_LIMITED">STAFF_LIMITED</option>
                            <option value="STAFF">STAFF</option>
                            <option value="ADMIN">ADMIN</option>
                        </select>
                    </div>
                    <p>
                        <button type="submit" class="st-button-submit  pure-button pure-button-primary">Save changes</button>
                        <button onclick="this.form.submited=this.value;" value="save-and-return" type="submit" class="st-button-submit st-submit-and-return  pure-button pure-button-primary">Save and return</button>
                   </p>
                </fieldset>
            </form>        
        </div>
        <div class="pure-u-1-3 user-management-actions">
            <h4>Actions</h4>
            <div if={!resetSent && !user.deleted}>
                <button onclick={forcePasswordReset} title="Will null-out the users password and send them an email asking them to reset it." class="pure-button">Force password reset.</button>
            </div>
            <div if={resetSent && !user.deleted}>
                Password reset!
            </div>
            <div if={!user.disabled}>
                <button onclick={disableUser} class="pure-button">Disable user</button>
            </div>
            <div if={user.disabled}>
                <button onclick={enableUser} class="pure-button">Enable user</button>
            </div>
            <div if={!user.approved}>
                <button onclick={approveUser} class="pure-button">Approve user</button>
            </div>
            <div if={user.approved}>
                <button onclick={unapproveUser} class="pure-button">Un-approve user</button>
            </div>
            <div  if={!user.deleted}>
                <button onclick={deleteUser} class="pure-button">Delete user</button>
            </div>
            <div if={user.deleted}>
                <button onclick={restoreUser} class="pure-button">Restore user</button>
            </div>
        </div>
    </div>
    <script>
     var self = this;
     self.mixin('databind');
     self.user = {};
     self.loading = true;
     self.resetSent = false;
     self.userId = self.opts.userId;

     submit = function(evt) {
         evt.stopPropagation();
         evt.preventDefault();
         var returnAfter = false;
         if (evt.target.submited === 'save-and-return') {
             returnAfter = true;
         }
         var data = self.getFormData();
         console.log('form submit', data, self.updateUserForm);
         if (!self.userId) {
             stallion.request({
                 url: '/st-admin/users/admin-create-user',
                 method: 'POST',
                 data: data,
                 form: self.updateUserForm,
                 success: function(user) {
                     self.opts.formData = $.extend({}, user);
                     self.user = user;
                     self.userId = user.id;
                     self.update();
                     if (returnAfter) {
                         window.location.hash = "/settings/authors";
                     }
                 }
             });
             
         } else {
             stallion.request({
                 url: '/st-admin/users/update-user/' + self.userId,
                 method: 'POST',
                 data: data,
                 form: self.updateUserForm,
                 success: function(user) {
                     self.opts.formData = $.extend({}, user);
                     self.user = user;
                     self.update();
                     if (returnAfter) {
                         window.location.hash = "/settings/authors";
                     }
                 }
             });
         }
         return false;
     };


          

     this.on('mount', function(){
         if (!self.userId) {
             self.update({loading: false, user: {}});
             return;
         }
         stallion.request({
             url: '/st-admin/users/view-user/' + self.opts.userId,
             success: function (user) {
                 self.loading = false;
                 self.opts.formData = $.extend({}, user);
                 self.user = user;
                 self.update();
             }
         });
     });

     
     
     /** Sends the target user a password reset email so they can reset their own password. */
     forcePasswordReset = function(evt) {
         stallion.request({
             url: '/st-admin/users/force-password-reset/' + self.opts.userId,
             method: 'POST',
             success: function(o) {
                 self.update({resetSent: true});
             }
         });

     };

     deleteUser = function(evt) {
         stallion.request({
             url: '/st-admin/users/toggle-user-deleted/' + self.opts.userId,
             method: 'POST',
             data: {deleted: true },
             success: function(o) {
                 self.user.deleted = true;
                 self.update();
             }
         });
     };

     restoreUser = function(evt) {
         stallion.request({
             url: '/st-admin/users/toggle-user-deleted/' + self.opts.userId,
             method: 'POST',
             data: {deleted: false },
             success: function(o) {
                 self.user.deleted = false;
                 self.update();
             }
         });
     };
 
     approveUser = function(evt) {
         stallion.request({
             url: '/st-admin/users/toggle-user-approved/' + self.opts.userId,
             method: 'POST',
             data: {approved: true},
             success: function(o) {
                 self.user.approved = true;
                 self.update();
             }
         });
     };

     unapproveUser = function(evt) {
         stallion.request({
             url: '/st-admin/users/toggle-user-approved/' + self.opts.userId,
             method: 'POST',
             data: {approved: false },
             success: function(o) {
                 self.user.approved = false;
                 self.update();
             }
         });
     };
     
    
     disableUser = function(evt) {
         stallion.request({
             url: '/st-admin/users/toggle-user-disabled/' + self.opts.userId,
             method: 'POST',
             data: {disabled: true},
             success: function(o) {
                 self.user.disabled = true;
                 self.update();
             }
         });
     };

     enableUser = function(evt) {
         stallion.request({
             url: '/st-admin/users/toggle-user-disabled/' + self.opts.userId,
             method: 'POST',
             data: {disabled: false },
             success: function(o) {
                 self.user.disabled = false;
                 self.update();
             }
         });
     };
     
    
    </script>
</user-view>

