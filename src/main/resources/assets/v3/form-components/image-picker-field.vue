<template>
    <div class="image-picker">
        <button class="btn btn-default"  v-on:click="showModal">Choose Image</button>
        <span v-if="src">
            <img style="max-width: 40px; max-height: 40px;" v-bind:src="src">
        </span>
        <span v-if="!src">
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
         return {
             src: '',
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
             this.src = imageInfo.thumbUrl;
         }
     }
 };
</script>
