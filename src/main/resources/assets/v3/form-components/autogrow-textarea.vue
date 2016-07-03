<template>
    <textarea class="autogrow-textarea form-control" v-model="value"></textarea>
</template>

<script>
 module.exports = {
     props: {
         value: {
             twoWay: true
         }
     },
     compiled: function() {
         stallion.autoGrow({}, $(this.$el));
     }
 };
</script>
