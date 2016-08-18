<template>
    <select class="select2-field-vue select2-target {{ className }}" :multiple="multiple" v-model="value">
        <option v-for="opt in selectValues" :value="opt.value">{{ opt.label }}</option>
    </select>
</template>

<script>
 module.exports = {
     props: {
         multiple: {
             default: false
         },
         selectOptions: Object,
         selectValues: Array,
         className: '',
         value: {
             sync: true
         },
         change: Function
     },
     data: function() {
         var self = this;
         var selectValues = [];
         (this.selectValues || []).forEach(function(opt) {
             if (typeof(opt) === 'string') {
                 selectValues.push({
                     value: opt,
                     label: opt
                 });
             } else {
                 selectValues.push(opt);
             }
         });
         this.selectValues = selectValues;
         this.options = this.options || {};
         this.selectOptions = this.selectOptions || {};
         return {};
     },
     ready: function() {
         var self = this;
         console.log('selectOptions for select2 ', this.selectOptions);
         $(this.$el)
               .select2(this.selectOptions)
               .on('change', function() {
                   // Not sure why this doesn't automatically update
                   self.value = $(this).val();
                   if (self.change) {
                       self.change($(this).val());
                   }
               })
             ;
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
