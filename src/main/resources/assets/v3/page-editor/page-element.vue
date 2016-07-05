
<style>

</style>


<template>
    <div class="page-element">
        <page-element-image v-if="element.type==='image'" :element="elementClone" :update-callback="update"></page-element-image>
        <page-element-image-collection v-if="element.type==='image-collection'" :element="elementClone" :update-callback="update"></page-element-image-collection>
        <page-element-markdown v-if="element.type==='markdown'" :element="elementClone" :update-callback="update"></page-element-markdown>
        <page-element-text v-if="element.type==='text'" :element="elementClone" :update-callback="update"></page-element-text>
    </div>
</template>

<script>
 module.exports = {
     props: {
         changeCallback: Function,
         elementName: '',
         element: Object
     },
     data: function() {
         return {
             elementClone: JSON.parse(JSON.stringify(this.element || {}))
         }
     },
     ready: function() {
         
     },
     methods: {
         update: function(content, rawContent, data, widgets) {
             this.element.content = content;
             this.element.rawContent = rawContent || '';
             this.element.data = data || {};
             this.element.widgets = widgets || [];
             this.changeCallback();
         }
     }
 };
</script>


