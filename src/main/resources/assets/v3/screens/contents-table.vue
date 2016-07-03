<style>
 
</style>



<template>
    <div>
    <h3 v-if="!isPages">Posts</h3>
    <h3 v-if="isPages">Pages</h3>    
    <h3 v-if="$loadingRouteData">Loading &hellip;</h3>
    <h3 v-if="!$loadingRouteData && !items.length">No posts yet</h3>
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
        
    </table>
        
    </div> 
</template>

<script>
 module.exports = {
     data: function() {
         console.log('contents table data func ', this.$route.name);
         return {
             isPages: false,
             isPosts: false,
             withDeleted: false,
             page: 1,
             pager: null,
             items: []
         };
     },
     route: {
         data: function(transition) {
             var self = this;
             console.log('route contents table');
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
         moment: function(a, b, c) {
             return moment(a, b, c);
         },
         fetchData: function(callback) {
             var url = '/st-publisher/posts'
             if (this.$route.name === 'pages') {
                 url = '/st-publisher/pages'
             }
             url += '?page=' + this.page + '&deleted=' + this.withDeleted;
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
                     console.log('error loading dashboard', o, xhr);
                 }
             });
         }
     }
 }
</script>
