<template>
    <div class="image-picker">
        <button class="btn btn-default"  v-on:click="showModal">Choose Image</button>
        <span v-if="value.thumbUrl">
            <img style="max-width: 40px; max-height: 40px;" v-bind:src="value.thumbUrl">
        </span>
        <span v-if="!value.thumbUrl">
            No image selected.
        </span>
        <modal-base v-if="modalShown" :shown.sync="modalShown" tag="image-selector" title="Pick an image" :callback="handleSelected"></modal-base>
    </div>
</template>
    
<script>
 module.exports = {
     props: {
         value: {
             twoWay: true
         }
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
         handleSelected: function(imageInfo) {
             console.log('handleSelected ', imageInfo);
             this.value = imageInfo;
         }
     }
 };
</script>
