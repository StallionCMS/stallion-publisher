<style>
 
</style>



<template>
    <div>
        <h3 v-if="$loadingRouteData">Loading &hellip;</h3>
        <div v-if="!$loadingRouteData">
            <st-data-table :table-definition="tableDefinition" :columns="columns" label="post" :browser-url-template="'#!/posts'" :data-url="'/st-publisher/content/posts'" :route="$route" table-class="table table-striped">
                <div class="actions-slot" slot="actions">
                    <button class="btn btn-primary">Add Article</button>
                </div>
            </st-data-table>
        </div>
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
             searchTerm: '',
             columns: [
                 {
                     actions: [{
                         className: 'btn btn-default btn-xs',
                         label: 'Edit'
                     }]
                 },
                 {
                     title: 'Updated At',
                     render: function(item) {
                         return moment(item.updatedAt).fromNow();
                     }
                 },
                 {
                     title: 'Title',
                     field: 'title'
                 },
                 {
                     title: 'Status',
                     render: function(item) {
                         if (item.currentlyPublished) {
                             return 'Published';
                         } else if (!item.currentlyPublished && item.draft) {
                             return 'Draft';
                         } else {
                             return 'Scheduled';
                         }
                     }
                 }
             ]
         };
     }
     /*
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
     }*/
 }
</script>
