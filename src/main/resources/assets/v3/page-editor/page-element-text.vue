
<style>

</style>


<template>
    <div class="page-element-text">
        <div class="form-group">
            <autogrow-textarea v-model="value"></autogrow-textarea>
        </div>
    </div>
</template>

<script>
 module.exports = {
     props: {
         updateCallback: Function,
         element: Object
     },
     data: function() {
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


