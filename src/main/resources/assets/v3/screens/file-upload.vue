


<style>

</style>


<template>
    <div>
        <h3>Upload file</h3>
        <form action="/st-publisher/upload-file"
              class="dropzone"
              id="my-awesome-dropzone">
            
        </form>        
        
    </div>
</template>

<script>
 module.exports = {
     compile: function() {
         this.dropzone = new Dropzone($(this.$el).find('.dropzone'), {
             dictDefaultMessage: "Drag one more more files here. Or click to open a file picker."
         });
     }
 };
</script>




