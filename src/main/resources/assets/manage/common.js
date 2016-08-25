

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
    
    stPublisher.generateUUID = function() {
       var d = new Date().getTime();
       if(window.performance && typeof window.performance.now === "function"){
           d += performance.now(); //use high-precision timer if available
       }
       var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
           var r = (d + Math.random()*16)%16 | 0;
           d = Math.floor(d/16);
           return (c=='x' ? r : (r&0x3|0x8)).toString(16);
       });
       return uuid;
    };

    stPublisher.showVueModal = function(opts) {
        
    };

    stPublisher.toTitleCase = function(str) {
        return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
    }
    
    stPublisher.widgetRegistry = function() {
        var self = this;


        

    }();
    
}());
