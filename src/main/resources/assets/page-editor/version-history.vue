



<template>
    <div class="version-history">
        <loading-div v-if="loading"></loading-div>
        <div v-if="!loading">
            <h3>Version history</h3>
            <table class="table table-striped" style="width:100%;">
                <thead>
                    <th></th>
                    <th>Date</th>
                    <th>Author</th>
                    <th>#&nbsp;Words</th>
                    <th>Diff</th>
                </thead>
                <tbody>
                    <tr v-for='item in pager.items'>
                        <td>
                            <a style="width:50px;margin-bottom:.5em;" class="btn btn-default btn-xs" href="/st-publisher/content/{{item.postId}}/view-version/{{item.id}}" target="_blank">view</a><br>
                            <a style="width:50px;margin-bottom:.5em;" class="btn btn-default btn-xs" @click='restoreVersion(item)'>restore</a><br>
                        </td>
                        <td>{{ moment(item.versionDate * 1000).fromNow() }}</td>
                        <td>{{ item.versionAuthorName }}</td>
                        <td>{{ item.wordCount }}</td>
                        <td style="overflow:hidden;">{{ item.diff }}</td>
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
             loadAll: true
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
                 url: '/st-publisher/content/' + self.contentId + '/load-versions?loadAll=' + self.loadAll,
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
                 url: '/st-publisher/content/make-version-most-recent',
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
