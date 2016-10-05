<style lang="scss">
 .cell-text-editable-vue {
     input {
         border: 1px solid #999;
         padding: 0px 2px 0px 2px;
         width: 100%;
     }
 }
</style>
<template>
    <form @submit.prevent="submit" class="form-inline cell-text-editable-vue">
        <input type="text" @blur="cancel(item, col)"  v-model="value" autofocus="autofocus">
    </form>
</template>
<script>
 module.exports = {
     props: {
         item: Object,
         col: Object,
         cancel: Function
     },
     data: function() {
         return {
             value: this.item[this.col.field]
         };
     },
     attached: function() {
         var self = this;
         setTimeout(function() {
             $(self.$el).find('input').focus();
         }, 20);
     },
     methods: {
         submit: function() {
             this.$dispatch('cell-value-updated', this.item, this.col.field, this.value, this.col);
         }
     }
 };
</script>
