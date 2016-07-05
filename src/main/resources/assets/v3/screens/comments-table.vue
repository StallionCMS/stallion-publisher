<style>
 
</style>



<template>
    <div>
        <h3>Comments</h3>
        <loading-div v-if="$loadingRouteData"></loading-div>
        <h3 v-if='!$loadingRouteData && !items.length'>No comments yet</h3>
        <table v-if='!$loadingRouteData && items.length' class="pure-table comments-table table table-striped">
            <thead>
                <tr>
                    <th></th>                    
                    <th>State</th>                    
                    <th>Created</th>
                    <th>Author</th>
                    <th>Comment</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for='item in items'>
                    <td>
                        <span v-if='!item.approved' style="width: 80px;display:inline-block;"><a  class="btn btn-xs btn-default" href="{{item.fullUrl}}" target="_blank">approve</a></span>
                        <span v-if='item.approved' style="width: 80px;display:inline-block;"><a class="btn btn-xs btn-default" href="{{item.fullUrl}}" target="_blank">un-approve</a></span>
                        <span style="width: 40px;display:inline-block;"><a v-if='!item.deleted' class="btn btn-xs btn-default" href="{{item.fullUrl}}" target="_blank">trash</a></span>
                    </td>
                    <td>
                        <span v-if='item.approved'>Approved</span>
                        <span v-if='!item.approved'>{{item.state.replace('_', ' ')}}</span>
                    </td>
                    <td>
                        {{moment(item.createdTicks).fromNow()}}
                    </td>
                    <td>
                        <div>{{item.authorDisplayName}}</div>
                        <div>{{item.authorEmail}}</div>
                        <div>{{item.authorWebSite}}</div>
                    </td>
                    <td style="height: 70px;">
                        {{item.bodyMarkdown}}
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
 module.exports = {
     data: function() {
         return {
             page: 1,
             pager: null,
             items: [],
             withDeleted: false
         };
     },
     route: {
         data: function(transition) {
             this.fetchData(function() {
                 transition.next();
             });
         }
     },
     methods: {
         moment: moment,
         fetchData: function(callback) {
             var self = this;
             stallion.request({
                 url: '/st-publisher/comments/list?page=' + self.page + '&deleted=' + self.withDeleted,
                 success: function (o) {
                     self.pager = o.pager;
                     self.items = o.pager.items;
                     if (callback) {
                         callback(o);
                     }
                 },
                 error: function(o, form, xhr) {
                     console.log('error loading dashboard', o, xhr);
                 }
             });
         },
         smartFormatDate: function(date) {
             var m = moment(date);
             return m.fromNow();
         }
     }
 }
</script>

