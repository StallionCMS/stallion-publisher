<style>


</style>

<template>
    <div>
        <h3>Image Library</h3>
        <h3 v-if="$loadingRouteData">Loading &hellip;</h3>
        <h3 v-if="!$loadingRouteData && !items.length">No posts yet</h3>
        <table v-if="!$loadingRouteData && items.length" class="table comments-table">
            <thead>
                <tr>
                    <th></th>
                    <th></th>                    
                    <th>Name</th>
                    <th></th>                    
                    <th>Uploaded</th>     
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="item in items">
                    <td>
                        <a class="btn btn-primary" href="javascript:;" v-on:click="selectImage(item)">Choose</a>
                    </td>
                    <td>
                        <img v-bind:src="item.thumbUrl" style="max-width: 100px; max-height: 100px;">
                    </td>
                    <td>
                        {{item.name}}
                    </td>
                    <td>
                        {{item.extension}}
                    </td>
                    <td>
                        {{moment(item.uploadedAt * 1000).fromNow()}}
                    </td>
                    <td>
                        <a href="{{item.url}}" target="_blank">open</a>
                    </td>

                </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
 module.exports = {
     props: {
         callback: Function
     },
     data: function() {
         return {
             pager: null,
             page: 1,
             withDeleted: true,
             items: [],
         }
     },
     created: function() {
         this.fetchData();
     },
     methods: {
         fetchData: function() {
             var self = this;
             stallion.request({
                 url: '/st-publisher/images?page=' + self.page + '&deleted=' + self.withDeleted,
                 success: function (o) {
                     self.pager = o.pager;
                     self.items = o.pager.items;
                 }
             });
         },
         selectImage: function(item) {
             if (this.callback) {
                 this.callback(item);
             }
         },
         moment: function(a, b, c) {
             return moment(a, b, c);
         }
     }
 };

</script>
