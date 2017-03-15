
<style>

</style>


<template>
    <div class="page-element">
        <component :is="elementTag" :element="elementClone" :update-callback="update" :options="options"></component>
    </div>
</template>

<script>
 module.exports = {
     props: {
         changeCallback: Function,
         elementName: '',
         element: Object,
         options: {
             type: Object,
             default: function() {
                 return {};
             }
         }
     },
     data: function() {
         var elementTag = 'page-element-' + this.element.type.replace(/_/g, '-').toLowerCase();
         return {
             elementTag: elementTag,
             elementClone: JSON.parse(JSON.stringify(this.element || {}))
         }
     },
     methods: {
         update: function(content, rawContent, data, widgets) {
             this.element.content = content;
             this.element.rawContent = rawContent || '';
             this.element.data = data || {};
             this.element.widgets = widgets || [];
             this.changeCallback(this.element, this);
         }
     }
 };
</script>


