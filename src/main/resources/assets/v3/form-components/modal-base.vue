


<template>
    <div class="modal {{cssClass}}" role="dialog" aria-labelledby="myLargeModalLabel">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <slot name="header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 v-if="title!==null" class="modal-title">{{ title }}</h4>
                    </slot>
                </div><!-- end .modal-header -->
                <div class="modal-body">
                    <slot name="body">
                        <component v-ref:bodyComponent :is="tag" :callback="modalCallback">
                        </component>
                    </slot>
                </div>
                <div class="modal-footer">
                    <slot name="footer">
                        <button class="btn btn-primary btn-large" v-on:click="saveChanges">Save Changes</button>
                        <a href="javascript:;" v-on:click="close" style="float:right;">Cancel</a>
                    </slot>
                </div>
            </div><!-- end .modal-content -->
        </div><!-- end .modal-dialog -->
    </div><!-- end .modal -->
</template>


<script>
 module.exports = {
     props: {
         shown: {
             twoWay: true
         },
         title: null,
         tag: '',
         cssClass: '',
         callback: Function,
         saveLabel: 'Save'
     },
     data: function() {
         return {
             modal: null
         }
     },
     compiled: function() {
         console.log('compiled modal');
         var self = this;
         console.log('tag ', this.tag);
         var $ele = $(self.$el);
         $ele.on("dragover", function(e) { e.preventDefault();  e.stopPropagation(); });
         $ele.on("drop", function(e) { e.preventDefault(); e.stopPropagation(); });
         $ele.modal({
             show: false,
             backdrop: true,
             width: 800
         });
         $ele.on('hidden.bs.modal', function(e) {
             self.shown = false;
         });
     },
     attached: function() {
         console.log('attached modal ', this.shown);
         if (this.shown) {
             $(this.$el).modal('show');
         }
     },
     methods: {
         modalCallback: function() {
             this.callback.apply(this, arguments);
             this.close();
         },
         saveChanges: function() {
             if (this.$refs.bodycomponent && this.$refs.bodycomponent.saveChanges) {
                 var result = this.$refs.bodycomponent.saveChanges();
                 this.callback(result);
             } else if (this.callback) {
                 this.callback();
             }
             this.close();
         },
         close: function() {
             $(this.$el).modal('hide');
         }
     },
     watch: {
         shown: function(newVal, old) {
             if (newVal) {
                 $(this.$el).modal('show');
             } else {
                 $(this.$el).modal('hide');
             }
         }
     }
 }
     
</script>
