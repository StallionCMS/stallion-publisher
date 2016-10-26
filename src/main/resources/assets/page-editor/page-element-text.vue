
<style>

</style>


<template>
    <div class="page-element-text">
        <div class="form-group">
            <autogrow-text v-model="value" :placeholder="options.placeholder || ''"></autogrow-text>
        </div>
    </div>
</template>

<script>
 module.exports = {
     props: {
         updateCallback: Function,
         element: Object,
         options: Object
     },
     data: function() {
         this.options = this.options || {};
         return {
             value: this.element.content || ''
         }
     },
     ready: function() {
         
     },
     methods: {

     },
     watch: {
         value: function() {
             console.log('update parent ', this.value);
             this.updateCallback(this.value, '', {});
         }
     }
 };
</script>


