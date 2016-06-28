

//<script>
   window.stPublisher = window.stPublisher || {};
   (function() {
       // Returns a function, that, as long as it continues to be invoked, will not
       // be triggered. The function will be called after it stops being called for
       // N milliseconds. If `immediate` is passed, trigger the function on the
       // leading edge, instead of the trailing.
       stPublisher.debounce = function(func, wait, immediate) {
	   var timeout;
	   return function() {
	       var context = this, args = arguments;
	       var later = function() {
		   timeout = null;
		   if (!immediate) func.apply(context, args);
	       };
	       var callNow = immediate && !timeout;
	       clearTimeout(timeout);
	       timeout = setTimeout(later, wait);
	       if (callNow) func.apply(context, args);
	   };
       };

       stPublisher.slugify = function(text) {
           return text.toString().toLowerCase()
                      .replace(/\s+/g, '-')           // Replace spaces with -
                      .replace(/[^\w\-]+/g, '')       // Remove all non-word chars
                      .replace(/\-\-+/g, '-')         // Replace multiple - with single -
                      .replace(/^-+/, '')             // Trim - from start of text
                      .replace(/-+$/, '');            // Trim - from end of text
       };
       
   }());
//</script>



<mount-if>
    <script>
     var self = this;
     self.condition = self.opts.condition;
     self.tag = self.opts.tag;
     self.inner = null;
     self.innerId = '';
     console.log('mount with opts ', self.opts);
     if (!self.tag || self.condition === undefined) {
         throw new Error("mount-if tag must have condition attribute and tag attribute: <mount-if condition={shouldExist} tag='image-library'></mount-if> ");
     }


     self.on('mount', function() {
         console.log('mount-if mounted');
         self.checkConditionAndUpdate();
     });
     
     self.on('updated', function() {
         console.log('mount-if updated');
         self.checkConditionAndUpdate();
     });

     self.checkConditionAndUpdate = function() {
         console.log('mount-if ', self.condition, self.tag, self.opts.condition, self.opts.tag);
         if (self.condition) {
             if (self.inner === null) {
                 var $ele = $("<" + self.tag + "></" + self.tag + ">");
                 self.innerId = 'inner-mount-' + generateUUID();
                 $ele.attr('id', self.innerId).attr('name', 'inner');
                 $(self.root).append($ele);
                 setTimeout(function() { self.inner = riot.mount('#' + self.innerId, self.opts)[0] }, 5);
             }
         } else {
             if (self.inner != null) {
                 self.inner.dismount();
                 $('#' + self.innerId).remove();
                 self.inner = null;
             }
         }
     };
     
          
    </script>
</mount-if>
