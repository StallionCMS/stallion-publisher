<style>
 
</style>



<template>
    <div class="settings-author-vue table-page">
        <loading-div v-if="$loadingRouteData"></loading-div>
        <div v-if="!$loadingRouteData">
            <st-data-table v-ref:table :table-definition="tableDefinition" :columns="columns" :label="'author'" :browser-url-template="'#!/settings/authors'" :data-url="'/st-publisher/authors/list'" :route="$route" table-class="table">
                <div class="actions-slot" slot="actions">
                    <a href="#!/settings/author/new" class="btn btn-primary btn-lg">New Author</a>
                </div>
            </st-data-table>
        </div>
    </div>
</template>

<script>
 module.exports = {
     data: function() {
         return {
             columns: [
                 {
                     actions: [{
                         label: 'Details',
                         className: 'btn btn-default btn-xs',
                         getLink: function(item) {
                             return '#!/settings/author/' + item.id;
                         }
                     }]
                 },
                 {
                     title: 'Username',
                     field: 'user.username'
                 },
                 {
                     title: 'Name',
                     field: 'user.displayName'
                 },
                 {
                     title: 'Email',
                     field: 'user.email'
                 },
                 {
                     title: 'Role',
                     field: 'user.role'
                 },
                 {
                     title: 'Created',
                     render: function(wrapped) {
                         return moment(wrapped.user.createdTicks).fromNow();
                     }
                 },
                 {
                     title: 'Status',
                     field: 'user.status'
                 }
             ]
         };
     },
     route: {
         data: function() {
         }
     },
     methods: {
         
     }
 }
</script>
