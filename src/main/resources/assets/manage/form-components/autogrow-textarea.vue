<template>
    <textarea class="autogrow-textarea form-control"></textarea>
</template>

<script>
 module.exports = {
     ready: function() {
         stallion.autoGrow({}, $(this.$el));
     }
 };
</script>
