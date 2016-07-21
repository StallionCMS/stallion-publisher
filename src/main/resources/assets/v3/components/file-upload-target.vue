


<style>

</style>


<template>
    <div>
        <form v-bind:action="formAction"
              class="my-dropzone st-file-dropzone dropzone"
              id="my-awesome-dropzone">
            
        </form>        
    </div>
</template>

<script>
 module.exports = {
     props: {
         options: Object,
         formAction: "/st-publisher/files/upload-file"
     },
     ready: function() {
         var opts = this.options || {};
         if (!opts.dictDefaultMessage) {
             opts.dictDefaultMessage = "Drag one more more files here. Or click to open a file picker.";
         }
         this.dropzone = new Dropzone($(this.$el).find('.st-file-dropzone').get(0), opts);
     }
 };
</script>




