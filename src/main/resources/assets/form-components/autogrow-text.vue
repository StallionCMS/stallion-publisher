<template>
    <textarea @keydown.enter.prevent.stop="onEnter" class="autogrow-textarea form-control autogrow-single-line autogrow-short" @input="onInput"></textarea>
</template>

<script>
 module.exports = {
     props: {
         value: ''
     },
     mounted: function() {
         this.$el.value = this.value;
         stallion.autoGrow({}, $(this.$el));
     },
     methods: {
         onEnter: function(evt) {
             evt.stopPropagation();
             evt.preventDefault();
             console.log('enter clicked');
             // do nothing, eat the enter key
             return false;
         },
         onInput: function(a, b) {
             console.log('onInput', a, b);
             this.$emit('input', this.$el.value);
         }
     },
     watch: {
         value: function(newVal) {
             this.$el.value = newVal;
         }
     }
 };
</script>
