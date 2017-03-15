<template>
    <div class="image-picker">
        <button class="btn btn-default"  v-on:click="showModal">{{ image.thumbUrl ? labelSelected : label }}</button>
        <span v-if="image.thumbUrl">
            <a target="_blank" :href="image.url"><img style="max-width: 40px; max-height: 40px;" v-bind:src="image.thumbUrl"></a>
        </span>
        <span v-if="!image.thumbUrl">
            
        </span>
        <modal-base v-if="modalShown" v-on:close="modalShown=false" tag="image-selector" title="Pick an image" :callback="handleSelected"></modal-base>
    </div>
</template>
    
<script>
 module.exports = {
     props: {
         value: {
             default: function() {
                 return {};
             }
         },
         label: {
             default: 'Choose Image'
         },
         labelSelected: {
             default: 'Change Image'
         }
     },
     data: function() {
         return {
             modalShown: false
         }
     },
     computed: {
         image: function() {
             return this.value || {};
         }
     },
     methods: {
         showModal: function() {
             this.modalShown = true;
         },
         handleSelected: function(imageInfo) {
             console.log('handleSelected ', imageInfo);
             //this.value = imageInfo;
             this.$emit('input', imageInfo);
         }
     }
 };
</script>
