
<style>

</style>


<template>
    <div class="page-element-text">
        <div class="form-group">
            <image-collection-field :on-change="collectionUpdated" v-model="element.data"></image-collection-field>
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
     created: function() {
         
     },
     methods: {
         collectionUpdated: function(data) {
             this.updateCallback(data.html, '', data);
         }
     }
 };
</script>


