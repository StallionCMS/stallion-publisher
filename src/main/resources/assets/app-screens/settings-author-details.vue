<style lang="scss">
 .settings-author-details-vue {

 }
</style>

<template>
    <div class="settings-author-details-vue">
        <h2>Author Details</h2>
        <form @submit.prevent="saveAuthor">
            <div class="form-group">
                <label>Given Name</label>
                <input class="form-control" type="text" v-model="user.givenName">
            </div>
            <div class="form-group">
                <label>Family Name</label>
                <input class="form-control" type="text" v-model="user.familyName">
            </div>
            <div class="form-group">
                <label>Email</label>
                <input class="form-control" type="email" v-model="user.email">
            </div>
            <div class="form-group">
                <label>Author blurb</label>
                <autogrow-textarea v-model="profile.bioMarkdown"></autogrow-textarea>
            </div>
            <div class="form-group">
                <button type="submit" class="btn btn-primary">Save</button>
                <button type="saveAndInvite" class="btn btn-primary">Save & Invite</button>
            </div>
        </form>
    </div>
</template>

<script>
 module.exports = {
     data: function() {
         return {
             isLoading: true,
             profile: {},
             user: {},
             userId: 0
         }
     },
     created: function() {
         this.onRoute();
     },
     methods: {
         onRoute: function() {
             if (this.$route.params.userId) {
                 this.userId = this.$route.params.userId;
                 this.fetchData();
             } else {
                 this.isLoading = false;
             }
         },
         fetchData: function() {
             var self = this;
             stallion.request({
                 url: '/st-publisher/authors/get/' + this.userId,
                 method: 'GET',
                 success: function(o) {
                     self.profile = o.profile;
                     self.user = o.user;
                     self.isLoading = false;
                 }
             });
         },
         saveAndInvite: function() {
             this.saveAuthor(true);
         },
         saveAuthor: function(invite) {
             var self = this;
             stallion.request({
                 url: '/st-publisher/authors/save',
                 method: 'POST',
                 data: {
                     authorProfile: this.profile,
                     user: this.user,
                     invite: invite === true,
                     userId: this.userId
                 },
                 success: function(o) {
                     stallion.showSuccess("User saved");
                     self.userId = o.id;
                 }
             });
         }
     }
 };
</script>
