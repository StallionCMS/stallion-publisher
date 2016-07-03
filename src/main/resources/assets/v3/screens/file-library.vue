
<style>

</style>


<template>
    <div class="file-library">
        <h3>File Library {{ispicker}}</h3>
        <loading-div v-if="$loadingRouteData"></loading-div>
        <h3 v-if="!$loadingRouteData && !items.length">No files yet</h3>
        <div class="form-group">
            <a href="#/file-upload" class="btn btn-primary btn-xl">Upload new file</a>
        </div>
        <table v-if="!$loadingRouteData && items.length" class="pure-table table table-striped">
            <thead>
                <tr>
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
                        <a v-if="!ispicker" class="btn btn-xs btn-default" href="{{item.url}}" target="_blank">open</a>
                        <a v-if="ispicker" class="btn btn-xs btn-default" href="javascript:;" v-on:click="pickFile(item)">Select</a>
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
                    <td style="height: 70px;">
                        <img v-if="item.type==='image'" v-bind:src="item.thumbUrl" style="max-height: 50px; max-width:50px;">
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
 module.exports = {
     props: {
         ispicker: false,
         callback: Function
     },
     data: function() {
         return {
             items: [],
             page: 1,
             pager: null,
             withDeleted: false
         }
     },
     route: {
         data: function(transition) {
             this.fetchData(function() {
                 transition.next();
             });
         }
     },
     created: function() {
         this.fetchData();
     },
     methods: {
         pickFile: function(file) {
             this.callback(file);
         },
         moment: function(a, b, c) {
             return moment(a, b, c);
         },
         smartFormatDate: function(date) {
             var m = moment(date);
             return m.fromNow();
         },         
         fetchData: function(callback) {
             var self = this;
             stallion.request({
                 url: '/st-publisher/files?page=' + self.page + '&deleted=' + self.withDeleted,
                 method: 'GET',
                 success: function(o) {
                     self.page = o.pager;
                     self.items = o.pager.items;
                     if (callback) {
                         callback();
                     }
                 }
             });
         }
     }
 };
</script>




