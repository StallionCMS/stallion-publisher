<template>
    <div class="image-uploader">
        <h3 class="title">Upload file</h3>
        <form action="/st-publisher/upload-file"
              class="image-dropzone dropzone"
              id="my-image-dropzone">
        </form>        
    </div>
</template>

<script>
 module.exports = {
     props: {
         callback: Function
     },
     compile: function() {
         var self = this;
         self.dropzone = new Dropzone(self['my-image-dropzone'], {
             dictDefaultMessage: "Drag one more more files here. Or click to open a file picker.",
             uploadMultiple: false,
             //             parallelUploads: true,
             maxFiles: 1,
             acceptedFiles: 'image/*,.jpg,.png,.svg,.gif',
             init: function() {
                 this.on("success", function(file, response) { 
                     //var o = JSON.parse(response);
                     self.callback(response);
                     this.removeFile(file);
                 });
             },
         });

     }
 };
</script>
