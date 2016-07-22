<template>
    <textarea @keydown.enter.prevent.stop="onEnter" class="autogrow-textarea form-control autogrow-single-line autogrow-short"></textarea>
</template>

<script>
 module.exports = {
     ready: function() {
         stallion.autoGrow({}, $(this.$el));
     },
     methods: {
         onEnter: function(evt) {
             evt.stopPropagation();
             evt.preventDefault();
             console.log('enter clicked');
             // do nothing, eat the enter key
             return false;
         }
     }
 };
</script>
