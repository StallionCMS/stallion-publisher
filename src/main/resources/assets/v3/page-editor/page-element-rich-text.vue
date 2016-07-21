
<style lang="scss">
 .page-element-rich-text-vue {
     
 }
</style>


<template>
    <div class="page-element-rich-text-vue">
        <div class="form-group">
            <tinymce-editor v-ref:editor :html="element.content" :widgets="element.widgets" :editor-id="'md-element-editor-' + element.name" :change-callback="markdownChanged"></tinymce-editor>
        </div>
    </div>
</template>

<script>
 module.exports = {
     props: {
         updateCallback: Function,
         element: Object
     },
     data: function() {
         return {

         }
     },
     ready: function() {
         
     },
     methods: {
         markdownChanged: function() {
             var data = this.$refs.editor.getData();
             this.updateCallback(data.html, data.html, {}, data.widgets);
         }
     }
 };
</script>


