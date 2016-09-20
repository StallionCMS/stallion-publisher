<style lang="scss">
 .datatable-daterange-filter-vue {
     
 }
</style>
<template>
    <div class="datatable-daterange-filter-vue">
        <date-picker :value.sync="rangeStart" ></date-picker> to <date-picker :value.sync="rangeEnd"></date-picker>
    </div>
</template>
<script>
 module.exports = {
     props: {
         fieldName: {
             required: true,
             type: String
         },
         rangeStart: String,
         rangeEnd: String,
         syncOnChange: {
             default: true,
             type: Boolean
         },
         datatable: {
             required: true,
             type: Object
         }
     },
     data: function() {
         return {};
     },
     methods: {
         toDatatable: function() {
             dt.clearFilter(this.fieldName);
             dt.addFilter(this.fieldName, moment(this.rangeStart).format('YYYY-MM-DD') + ' 00:00:00', '>');
             dt.addFilter(this.fieldName, moment(this.rangeEnd).format('YYYY-MM-DD') + ' 23:59:59', '<=');
             self.datatable.navigate({page: 1});
             return;
         },
         syncFromRoute: function() {
             
         }
     },
     watch: {
         rangeEnd: function(val, old) {
             if (this.syncOnChange) {
                 this.toDatatable();
             }
         },
         rangeStart: function(val, old) {
             if (this.syncOnChange) {
                 this.toDatatable();
             }
         }
     }
 }
</script>
