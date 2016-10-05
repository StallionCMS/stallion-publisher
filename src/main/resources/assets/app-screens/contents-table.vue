<style>
 
</style>



<template>
    <div class="contents-table-vue table-page">
        <h3 v-if="$loadingRouteData">Loading &hellip;</h3>
        <div v-if="!$loadingRouteData">
            <st-data-table :table-definition="tableDefinition" :columns="columns" :label="label" :browser-url-template="'#!/posts'" :data-url="dataUrl" :route="$route" table-class="table">
                <div class="actions-slot" slot="actions">
                    <a href="#!/new-post" v-if="isPosts" class="btn btn-primary btn-lg">New Post</a>
                    <a href="#!/new-page" v-if="isPages" class="btn btn-primary btn-lg">New Page</a>
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
             label: 'post',
             dataUrl: '',
             items: [],
             searchTerm: '',
             columns: [
                 {
                     actions: [{
                         className: 'btn btn-default btn-xs',
                         label: 'Edit',
                         getLink: function(item) {
                             return '#!/edit-content/' + item.id;
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
     route: {
         data: function(transition) {
             var url = '/st-publisher/content/posts';
             var isPosts = this.$route.path.indexOf('/posts') > -1;
             label = 'post';
             if (!isPosts) {
                 label = 'page';
                 var url = '/st-publisher/content/pages';
             }
             transition.next({
                 dataUrl: url,
                 label: label,
                 isPosts: isPosts,
                 isPages: !isPosts
             })
         }
     }
 }
</script>
