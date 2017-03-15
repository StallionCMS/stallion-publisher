
<style lang="scss">
 .file-library-vue {
     .file-library {
         margin-top: 0px;
     }
     .image-thumbnail {
         max-height: 50px;
         max-width: 50px;
     }
     tbody tr {
         height: 66px;
     }
 }

     
</style>


<template>
    <div class="file-library-vue table-page">
        <loading-div v-if="isLoading"></loading-div>
        <div v-if="!isLoading">
            <st-data-table ref="table" :title="'File Library'" :columns="columns" :label="'file'" :browser-url-template="'#!/files'" :data-url="'/st-publisher/files/library'" :route="$route" table-class="table">
                <div class="actions-slot" slot="actions">
                    <a href="#/file-upload" class="btn btn-primary btn-xl">Upload new file</a>
                </div>
            </st-data-table>
        </div>
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
             isLoading: false,
             columns: [
                 {
                     label: 'Open',
                     component: {
                         template: '<a class="btn btn-default btn-xs" :href="item.url" target="blank">Open</a>'
                     }
                 },
                 {
                     title: 'Name',
                     field: 'name',
                 },
                 {
                     title: 'Extension',
                     field: 'extension',
                 },
                 {
                     title: 'Uploaded At',
                     field: 'uploadedAt',
                     render: function(item) {
                         return moment(item.uploadedAt * 1000).fromNow();
                     }
                 },
                 {
                     component: {
                         template: '<span><img class="image-thumbnail" v-if="item.type===\'image\'" v-bind:src="item.thumbUrl"></span>'
                     }
                 }
             ]
         }
     },
     created: function() {

     },
     watch: {
         '$route': function() {
             this.isPicker = false;
         }
     },
     methods: {
         pickFile: function(file) {
             this.callback(file);
         },
         uploadComplete: function(file) {
             this.showUploader = false;
             if (this.callback) {
                 this.callback(file);
             }
             this.fetchData();
         }
     }
 };
</script>




