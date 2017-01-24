<template>
    <div class="image-collection-field">
        <button class="btn btn-default"  v-on:click="showModal">Edit Image Collection</button>
        <span v-if="value.images">
            {{ value.images.length }} image(s) in collection.
        </span>
        <span v-if="!value.images">
            No images in collection.
        </span>
        <modal-base v-if="modalShown" :shown.sync="modalShown" title="Image Collection" :callback="finishConfigure">
            <div slot="body">
                <image-collection-configure ref="collection" :data="value"></image-collection-configure>
            </div>
        </modal-base>
    </div>
</template>
    
<script>
 module.exports = {
     props: {
         value: {
             twoWay: true
         },
         onChange: Function
     },
     data: function() {
         if (!this.value) {
             this.value = {}
         }
         return {
             modalShown: false
         }
     },
     methods: {
         showModal: function() {
             this.modalShown = true;
         },
         finishConfigure: function(imageInfo) {
             this.value = this.$refs.collection.getData();
             if (this.onChange) {
                 this.onChange(this.value);
             }
         }
     }
 };
</script>
