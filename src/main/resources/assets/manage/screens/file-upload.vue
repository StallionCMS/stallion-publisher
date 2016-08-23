


<style>

</style>


<template>
    <div class="file-upload-vue">
        <div class="st-breadcrumbs">
            <a href="#!/files">File Library</a> <span>&#8250;</span> <span>Upload file</span>
        </div>
        <h3>Upload files</h3>
        <form action="/st-publisher/files/upload-file"
              class="my-dropzone st-file-dropzone dropzone"
              id="my-awesome-dropzone">
        </form>        
        <div class="p">
            <a  href="#!/files" class="btn btn-default btn-xl">Return to File Library</a>
        </div>
</template>

<script>
 module.exports = {
     ready: function() {

     },
     route: {
         activate: function() {
             console.log('file uploader loading!');
             
             this.dropzone = new Dropzone($(this.$el).find('.st-file-dropzone').get(0), {
                 dictDefaultMessage: "Drag one more more files here. Or click to open a file picker.",
                 init: function() {
                     this.on("uploadprogress", function(file, percent, c, d) {
                         if (percent === 100) {
                             $(file.previewTemplate).find(".dz-progress").html("Processing...");
                         }
                     });
                 }
             });
         }
     }
 };
</script>




