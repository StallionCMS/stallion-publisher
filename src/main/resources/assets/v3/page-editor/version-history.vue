



<template>
    <div class="version-history">
        <loading-div v-if="loading"></loading-div>
        <div v-if="!loading">
            <h3>Version history</h3>
            <table class="table table-striped" >
                <thead>
                    <th></th>
                    <th>Date</th>
                    <th>Author</th>
                    <th>Word Count</th>
                    <th>Diff</th>
                </thead>
                <tbody>
                    <tr v-for='item in pager.items'>
                        <td>
                            <a style="width:100px;margin-bottom:.5em;" class="btn btn-default btn-xs" href="/st-publisher/posts/{{item.postId}}/view-version/{{item.id}}" target="_blank">view</a><br>
                            <a style="width:100px;margin-bottom:.5em;" class="btn btn-default btn-xs" @click='restoreVersion(item)'>restore as draft</a><br>
                        </td>
                        <td>{{ moment(item.versionDate * 1000).fromNow() }}</td>
                        <td>{{ item.versionAuthorName }}</td>
                        <td>{{ item.wordCount }}</td>
                        <td>{{ item.diff }}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script>
 module.exports = {
     props: {
         callback: {
             type: Function,
             required: true
         },
         contentId: {
             type:Number,
             required: true
         }
     },
     data: function() {
         return {
             loading: true,
             pager: null,
             loadAll: false
         }
     },
     ready: function() {
         this.loadVersions();
     },
     methods: {
         moment: moment,
         loadVersions: function() {
             var self = this;
             stallion.request({
                 url: '/st-publisher/posts/' + self.contentId + '/load-versions?loadAll=' + self.loadAll,
                 success: function(result) {
                     self.pager = result.pager;
                     self.loading = false;
                 }
             });
         },
         restoreVersion: function(version) {
             var self = this;
             stallion.request({
                 method: 'POST',
                 url: '/st-publisher/posts/make-version-most-recent',
                 data: {
                     postId: self.contentId,
                     versionId: version.id
                 },
                 success: function(version) {
                     self.callback(version);
                 }
             });
         }
     }
 }
</script>
