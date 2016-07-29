<style lang="scss">
 .cell-select-editable-vue {
     form {
         padding: 0px;
         margin: 0px;
         display: inline-block;
     }
     .material-icons {
         font-size: 16px;
         vertical-align: -20%;
         cursor: pointer;
     }
 }
</style>
<template>
    <form @submit.prevent="submit" class="form-inline cell-select-editable-vue">
        <select @change="submit" v-model="value" style="width:75%;">
            <option :value="opt.value" v-for="opt in options">{{ opt.label }}</option>
        </select>
        <a href="javascript:;" @click.prevent.stop="cancel(item, col)" class="material-icons">cancel</i></a>
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
         var options = [];
         var colOptions = this.col.selectableValues || [];
         colOptions.forEach(function(val) {
             if (typeof(val) === 'string') {
                 options.push({value: val, label: val});
             } else {
                 options.push(val);
             }
         });
         return {
             options: options,
             value: this.item[this.col.field]
         };
     },
     methods: {
         submit: function() {
             //this.callback(this.item, this.field, this.value);
             this.$dispatch('cell-value-updated', this.item, this.col.field, this.value, this.col);
         }
     }
 };
</script>
