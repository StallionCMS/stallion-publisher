<style>
 
</style>



<template>
    <div>
        <loading-div v-if='$loadingRouteData'></loading-div>
        <h3 v-if='!$loadingRouteData && !items.length'>No contacts found</h3>        
        <div v-if="!$loadingRouteData">
            <h3>Contacts</h3>
            <table v-if='!loading && items.length' class="comments-table table table-striped">
                <thead>
                    <tr>
                        <th></th>                    
                        <th>Name</th>                    
                        <th>Email</th>
                        <th>Created</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for='item in items'>
                        <td>
                            <a href="#/contact-details/{{item.id}}" class="btn btn-default btn-xs">Details</a>
                        </td>
                        <td>
                            <div>{{item.displayName}}</div>
                        </td>
                        <td>
                            <div>{{item.email}}</div>
                        </td>
                        <td>
                            {{moment(item.createdTicks).fromNow()}}
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script>
 module.exports = {
     data: function() {
         return {
             items: [],
             pager: null,
             page: 1,
             withDeleted: false
         };
     },
     route: {
         data: function(transition) {
             var self = this;
             this.fetchData(function() {
                 transition.next();
             });
         }
     },
     methods: {
         moment: moment,
         smartFormatDate: function(date) {
             var m = moment(date);
             return m.fromNow();
         },
         fetchData: function(callback) {
             var self = this;
             stallion.request({
                 url: '/st-publisher/contacts/list?page=' + self.page + '&deleted=' + self.withDeleted,
                 success: function (o) {
                     self.pager = o.pager;
                     self.items = o.pager.items;
                     if (callback) callback();
                 }
             });
         }
     }
 }
</script>
