




(function() {

    function setZoomOnLoad() {
        var body = document.body;
        
        var query = window.location.search;
        var i = query.indexOf('zoom=');
        var end = query.indexOf('&', i);
        if (end === -1) {
            end = undefined;
        }
        if (i  > -1) {
            var zoom = query.substr(i + 5, end);
            body.setAttribute("style", "zoom: " + zoom + ";");
            console.log('zoom set', zoom);            
        }
    }


    console.log('add listener');
    window.addEventListener("load", setZoomOnLoad);
}());
