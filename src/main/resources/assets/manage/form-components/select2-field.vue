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
         choices: Array,
         config: Object,
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
         if (this.config && !this.selectOptions) {
             this.selectOptions = this.config;
         }
         if (this.choices && !this.selectValues) {
             this.selectValues = this.choices;
         }
         
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
     watch: {
         'selectValues': function(newValues, oldValues) {
             var self = this;
             var $select = $(this.$el);
             newValues.forEach(function(val) {
                 var value = val;
                 var label = val;
                 if (val.value) {
                     value = val.value;
                     label = val.label;
                 }
                 var $opt = $select.find('option[value="' + value + '"]');
                 if ($opt.length) {
                     return;
                 } else {
                     $select.append($('<option></option>').attr('value', value).html(label));
                 }
             });
             $select.trigger('change');
         }
     },
     methods: {
         addOption: function(option) {
             var hasOption = false;
             this.selectValues.forEach(function(existing) {
                 if (existing === option || (existing.value === option) || (existing == option.value) || (existing.value == option.value)) {
                     hasOption = true;
                     return false;
                 }
             });
             if (!hasOption) {
                 this.selectValues.push(option);
             }
         },
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
