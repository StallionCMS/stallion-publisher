<style>
 
</style>



<template>
    <div>
    <h3 v-if="!isPages">Posts</h3>
    <h3 v-if="isPages">Pages</h3>    
    <h3 v-if="$loadingRouteData">Loading &hellip;</h3>
    <h3 v-if="!$loadingRouteData && !items.length">No posts yet</h3>
    <div class="form-group">
        <form @submit.prevent="doSearch">
            <input type="text" class="form-control" placeholder="Search for page" v-model="searchTerm">
        </form>
    </div>
    <table v-if="!$loadingRouteData && items.length" class="pure-table comments-table table table-striped">
        <thead>
            <tr>
                <th></th>
                <th>Last updated</th>
                <th>Title</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="post in items">
                <td class="page-actions">
                    <a class="btn btn-xs btn-default" href="#/edit-content/{{post.id}}"">Edit</a>
                    <a v-if="!post.currentlyPublished" class="btn-open btn btn-xs btn-default" target="_blank" href="/st-publisher/posts/{{ post.id }}/preview">Preview</a>
                    <a v-if="post.currentlyPublished" class="btn-open btn btn-xs btn-default" target="_blank" href="{{ post.previewUrl }}">View</a>
                </td>
                <td>
                    {{moment(post.updatedAt).fromNow()}}
                </td>
                <td>
                    {{post.title}}
                </td>
                <td>
                    <span v-if="post.currentlyPublished">Published</span>
                    <span v-if="!post.currentlyPublished && post.draft">Draft</span>
                    <span v-if="!post.currentlyPublished && !post.draft">Scheduled</span>
                </td>
            </tr>
        </tbody>
        <tfoot>
            <tr>
                <td colspan="4">
                    <data-table-pager :make-link="makePageLink" "v-if="pager!==null" :page="page" :pager="pager" :base-path="'!/posts'" :page="page"></data-table-pager>
                </td>
            </tr>
        </tfoot>
    </table>
        
    </div> 
</template>

<script>
 module.exports = {
     data: function() {
         return {
             isPages: false,
             isPosts: false,
             withDeleted: false,
             page: 1,
             pager: null,
             items: [],
             searchTerm: ''
         };
     },
     route: {
         data: function(transition) {
             console.log('posts page?');
             var self = this;
             if (this.$route.params.page) {
                 this.page = parseInt(this.$route.params.page, 10);
             }
             this.searchTerm = this.$route.query.search || '';
             this.fetchData(function(o) {
                 transition.next({
                     pager: o.pager, 
                     items: o.pager.items,
                     isPages: self.$route.name === 'pages',
                     isPosts: self.$route.name === 'posts'
                 });
             });
         }
     },
     methods: {
         doSearch: function() {
             this.page = 1;
             window.location.hash = '!/' + this.$route.name + '/' + this.page + '?search=' + encodeURIComponent(this.searchTerm);
         },
         moment: function(a, b, c) {
             return moment(a, b, c);
         },
         makePageLink: function(page) {
             return '#!/' + this.$route.name + '/' + page + '?search=' + encodeURIComponent(this.searchTerm || '');
         },
         fetchData: function(callback) {
             var self = this;
             var url = '/st-publisher/content/posts'
             if (this.$route.name === 'pages') {
                 url = '/st-publisher/content/pages'
             }
             url += '?page=' + this.page + '&deleted=' + this.withDeleted;
             if (this.searchTerm && this.searchTerm.length > 2) {
                 url += '&search=' + encodeURIComponent(this.searchTerm);
             }
             stallion.request({
                 url: url,
                 success: function (o) {
                     if (callback) {
                         callback(o);
                     } else {
                         self.pager = o.pager;
                         self.items = o.pager.items;
                     }
                 },
                 error: function(o, form, xhr) {

                 }
             });
         }
     }
 }
</script>
