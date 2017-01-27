<template>
    <select :class="'select2-field-vue select2-target' + className" :multiple="multiple" v-model="value">
        <option v-for="opt in selectOptions" :value="opt.value">{{ opt.label }}</option>
    </select>
</template>

<script>
 module.exports = {
     props: {
         multiple: {
             default: false
         },
         choices: {
             type: Array,
             default: function() {
                 return [];
             }
         },
         config: {
             type: Object,
             default: function() {
                 return {};
             }
         },
         //selectOptions: Object,
         //selectValues: Array,
         className: '',
         value: {
             default: function() {
                 return '';
             }
         }
     },
     data: function() {
         var self = this;
         var selectOptions = [];
         (this.choices || []).forEach(function(opt) {
             if (typeof(opt) === 'string') {
                 selectOptions.push({
                     value: opt,
                     label: opt
                 });
             } else {
                 selectOptions.push(opt);
             }
         });
         this.selectOptions = selectOptions;
         //this.selectValues = selectValues;
         //this.options = this.options || {};
         //this.selectOptions = this.selectOptions || {};
         return {};
     },
     mounted: function() {
         var self = this;
         console.log('config for select2 ', this.config);
         $(this.$el)
               .select2(this.config)
               .on('change', function() {
                   // Not sure why this doesn't automatically update
                   var defaultVal = '';
                   if (self.multiple) {
                       defaultVal = [];
                   }
                   self.$emit('input', $(self.$el).val() || defaultVal);
                   if (self.change) {
                       self.change($(this).val());
                   }
               })
             ;
     },
     watch: {
         'selectOptions': function(newValues, oldValues) {
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
             this.selectOptions.forEach(function(existing) {
                 if (existing === option || (existing.value === option) || (existing == option.value) || (existing.value == option.value)) {
                     hasOption = true;
                     return false;
                 }
             });
             if (!option.value) {
                 option = {value: option.value, label: option.label};
             }
             if (!hasOption) {
                 this.selectOptions.push(option);
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
