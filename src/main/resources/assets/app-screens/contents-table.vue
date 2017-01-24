<style>
 
</style>



<template>
    <div class="contents-table-vue table-page">
        <h3 v-if="isLoading">Loading &hellip;</h3>
        <div v-if="!isLoading">
            <st-data-table :columns="columns" :label="label" :browser-url-template="'#/posts'" :data-url="dataUrl" :route="$route" table-class="table">
                <div class="actions-slot" slot="actions">
                    <a href="#/new-post" v-if="isPosts" class="btn btn-primary btn-lg">New Post</a>
                    <a href="#/new-page" v-if="isPages" class="btn btn-primary btn-lg">New Page</a>
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
             isLoading: false,
             withDeleted: false,
             page: 1,
             pager: null,
             label: 'post',
             dataUrl: '',
             items: [],
             theRoute: {},
             searchTerm: '',
             columns: [
                 {
                     actions: [{
                         className: 'btn btn-default btn-xs',
                         label: 'Edit',
                         getLink: function(item) {
                             return '#/edit-content/' + item.id;
                         }
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
     },
     watch: {
         '$route': function() {
             this.onRoute();
         }
     },
     created: function() {
         this.onRoute();
     },
     methods: {
         onRoute: function() {
             console.log('contents-table on route');
             var self = this;
             var url =  '/st-publisher/content/posts';
             var isPosts = this.$route.path.indexOf('/posts') > -1;
             label = 'post';
             if (!isPosts) {
                 label = 'page';
                 var url = '/st-publisher/content/pages';
             }
             self.dataUrl = url;
             self.label = label;
             self.isPosts = isPosts;
             self.isPages = !isPosts;
             self.isLoading = false;
             //debugger;
             //self.theRoute = {params: this.$route.params, query: this.$route.query};
             console.log('onRoute!!');
             ///self.theRoute = {params: {}, query: {search: 'asdfasdf'}};
         }
     }
 }
</script>
