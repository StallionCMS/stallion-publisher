
<style lang="scss">
 .page-element-rich-text-vue {
     
 }
</style>


<template>
    <div class="page-element-rich-text-vue">
        <div class="form-group">
            <tinymce-editor ref="editor" :html="element.content" :widgets="element.widgets" :editor-id="'md-element-editor-' + element.name" :change-callback="markdownChanged" :tiny-options="options.tinyOptions" :name="element.name" :on-init="options.onInit" :on-setup="options.onSetup" :options="options.options"></tinymce-editor>
        </div>
    </div>
</template>

<script>
 module.exports = {
     props: {
         updateCallback: Function,
         element: Object,
         options: {
             type: Object,
             default: function() {
                 return {};
             }
         }
         
     },
     data: function() {
         if (!this.options.tinyOptions) {
             this.options.tinyOptions = {};
         }
         if (!this.options.options) {
             this.options.options = {};
         }

         return {

         }
     },
     methods: {
         markdownChanged: function() {
             var data = this.$refs.editor.getData();
             this.updateCallback(data.html, data.html, {}, data.widgets);
         }
     }
 };
</script>


