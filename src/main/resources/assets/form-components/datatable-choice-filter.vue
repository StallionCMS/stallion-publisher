<style lang="scss">

</style>
<template>
    <div class="publisher-style-guide-vue presto-details-page">
        <select :multiple="multiple" :choices="choices">
            <option v-for="choice in choices" :value="choice.value">{{ choice.label }}</option>
        </select>
    </div>
</template>
<script>
 module.exports = {
     props: {
         fieldName: {
             required: true,
             type: String
         },
         multiple: {
             default: false,
             type: Boolean
         },
         datatable: {
             required: true,
             type: Object
         },
         choices: Array,
         config: {},
         change: Function
     },
     data: function() {
         
         return {};
     },
     methods: {
         syncFromRoute: function() {
             
         }
     },
     watch: {
         value: function(val, old) {
             var self = this;
             if (typeof(val) === typeof([])) {
                 val = [val];
             }
             self.datatable.clearFilter(this.fieldName);
             if (val) {
                 self.datatable.clearFilter(this.fieldName);
                 self.datatable.addFilter(this.fieldName, val, 'any');
             }
             self.datatable.navigate({page: 1});
             if (this.change) {
                 this.change(val, old);
             }
         }
     }
 }
</script>
