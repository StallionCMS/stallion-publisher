<style>
 
</style>



<template>
    <div>
        <loading-div v-if='$loadingRouteData'></loading-div>
        <h3 v-if='!$loadingRouteData && !items.length'>None found</h3>        
        <div v-if="!$loadingRouteData">
            <h3>Global Modules</h3>
            <table v-if='!loading && items.length' class="comments-table table table-striped">
                <thead>
                    <tr>
                        <th></th>                    
                        <th>Name</th>                    
                        <th></th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for='item in items'>
                        <td>
                            <a href="#/contact-details/{{item.id}}" class="btn btn-default btn-xs">Edit</a>
                        </td>
                        <td>
                            <div>{{item.label || item.name }}</div>
                        </td>
                        <td>

                        </td>
                        <td>

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
                 url: '/st-publisher/content/global-modules/list',
                 success: function (o) {
                     self.items = o.modules;
                     if (callback) callback();
                 }
             });
         }
     }
 }
</script>
