 
<style>

</style>


<template>
    <div class="page-element-text">
        <div class="form-group">
            <image-picker-field :value.sync="element.data.image"></image-picker-field>
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
         console.log('page-element-iamge', this.element);
         if (!this.element.data) {
             this.element.data = {};
         }
         return {

         }
     },
     ready: function() {
         
     },
     methods: {

     },
     watch: {
         'element.data.image': function() {
             var image = this.element.data.image;
             var html = '';
             if (image) {
                 var $ele = $('<img>').attr('src', image.mediumUrl);
                 html = $ele.get(0).outerHTML;
             }
             this.updateCallback(html, '', this.element.data);
         }
     }
 };
</script>


