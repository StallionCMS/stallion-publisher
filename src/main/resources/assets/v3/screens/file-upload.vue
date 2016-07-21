


<style>

</style>


<template>
    <div>
        <h3>Upload file?</h3>
        <form action="/st-publisher/files/upload-file"
              class="my-dropzone st-file-dropzone dropzone"
              id="my-awesome-dropzone">
            
        </form>        
        
    </div>
</template>

<script>
 module.exports = {
     ready: {

     },
     route: {
         activate: function() {
             console.log('file uploader loading!');
             //debugger;
             this.dropzone = new Dropzone($(this.$el).find('.st-file-dropzone').get(0), {
                 dictDefaultMessage: "Drag one more more files here. Or click to open a file picker."
             });
         }
     }
 };
</script>




