<style>
 .comments-table-vue .st-data-table-vue .data-table-header {
     .table-actions {
         float: left;
     }
 }
</style>



<template>
    <div class="comments-table-vue  table-page">
        <loading-div v-if="$loadingRouteData"></loading-div>
        <div v-if="!$loadingRouteData">
            <st-data-table v-ref:table :table-definition="tableDefinition" :columns="columns" :label="'comment'" :browser-url-template="'#!/comments'" :data-url="'/st-publisher/comments/list'" :route="$route" table-class="table">
                <div slot="actions" class="table-actions" style="">
                    <select class="form-control" v-model="customFilter">
                        <option value="">All Comments</option>
                        <option value="pending">Pending Comments</option>
                        <option value="approved">Approved Comments</option>
                        <option value="deleted">Deleted Comments</option>
                    </select>
                </div>
            </st-data-table>
        </div>
    </div>
</template>

<script>
 var AuthorComponent = Vue.extend({
     props: {
         item: Object
     },
     template: '<div><div>{{ item.authorDisplayName }}</div><div>{{ item.authorEmail }}</div><div>{{ item.authorWebSite }}</div></div>'
 });
 module.exports = {
     data: function() {
         var self = this;
         return {
             customFilter: '',
             columns: [
                 {
                     component: {
                         template: '<div><a class="btn btn-default btn-xs" target="blank" href="{{ item.permalink }}">View</a> <a v-if="!item.approved" @click="approve" class="btn btn-default btn-xs" href="javascript:;" @click="approve">Approve</a> <a v-if="!item.deleted" class="btn btn-default btn-xs" href="javascript:;" @click="deleteComment">Delete</a> ',
                         methods: {
                             approve: function() {
                                 var self = this;
                                 var comment = this.item;
                                 stallion.request({
                                     url: '/st-publisher/comments/' + comment.id + '/restore-and-approve',
                                     method: 'POST',
                                     success: function() {
                                         comment.approved = true;
                                         comment.deleted = false;
                                     }
                                 });
                             },
                             deleteComment: function() {
                                 var row = this;
                                 stallion.request({
                                     url: '/st-publisher/comments/' + this.item.id + '/delete',
                                     method: 'POST',
                                     success: function() {
                                         row.item.approved = false;
                                         row.item.deleted = true;
                                         self.$refs.table.items.$set(row.item.$index, row.item);
                                     }
                                 });
                             }
                         }
                     }
                 },
                 {
                     title: 'State',
                     field: 'state',
                     render: function(item) {
                         return stPublisher.toTitleCase(item.state.toLowerCase());
                     }
                 },
                 {
                     title: 'Created',
                     field: 'createdTicks',
                     render: function(item) {
                         return moment(item.createdTicks).fromNow();
                     }
                 },
                 {
                     title: 'Author',
                     component: AuthorComponent
                 },
                 {
                     title: 'Comment',
                     field: 'bodyMarkdown'
                 }
             ]
         };
     },
     route: {
         data: function() {

         }
     },
     methods: {
         
     },
     watch: {
         'customFilter': function(customFilter, b) {
             console.log('changed customFilter ', customFilter, b);
             this.$refs.table.navigate({customFilter: customFilter});
         }
     }
 }
</script>

