
<style>

</style>


<template>
    <div class="page-element-text">
        <div class="form-group">
            <markdown-editor ref="editor" :markdown="element.rawContent" :widgets="element.widgets" :editor-id="'md-element-editor-' + element.name" :change-callback="markdownChanged"></markdown-editor>
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
         return {

         }
     },
     ready: function() {
         
     },
     methods: {
         markdownChanged: function() {
             var data = this.$refs.editor.getData();
             this.updateCallback('', data.markdown, {}, data.widgets);
         }
     }
 };
</script>


