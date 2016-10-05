




(function() {
    var gallery = {};

    gallery.init = function() {
        gallery.initColumns();
        gallery.initGrid();
        gallery.initSlider();

        gallery.initPhotoswipe();

    }
    
    gallery.initColumns = function() {
        gallery.fitColumnsToPage();
        $(window).resize(gallery.fitColumnsToPage);
    };

    gallery.fitColumnsToPage = function() {
        $('.st-image-collection.st-image-collection-default.image-columns').each(function() {
            var colWidth = $(this).parent().width() || 400;
            var maxRatio = 0;
            var minRatio = 1000000;
            var maxWidth = parseInt(colWidth / 2.5, 10);
            if (colWidth <= 500) {
                maxWidth = colWidth - 20;
            }
            
            var maxHeight = maxWidth;
            
            var images = [];
            var imgBySrc = {};
            var $ul = $(this);
            $ul.find('.item').each(function(i) {
                i++;
                var $li = $(this);
                var $img = $li.find('img');
                var width = parseInt($img.attr('data-width'), 10);
                var height = parseInt($img.attr('data-height'), 10);
                var ratio = width / height || 1;
                var newHeight = height;
                var newWidth = width;
                if (ratio > 1) {
                    if (width > maxWidth) {
                        newWidth = maxWidth;
                    }
                    newHeight = parseInt(newWidth / ratio, 10);
                } else {
                    if (height > maxHeight) {
                        newHeight = maxHeight;
                    }
                    newWidth = parseInt(newHeight * ratio, 10);
                }

                $img.css({height: newHeight + 'px', width: newWidth + 'px'});
                if (colWidth > 500) {
                    $li.css({'width': '50%'});
                } else {
                    $li.css({'width': '100%'});
                }

            });
        });


    };

 
    gallery.initGrid = function() {
        gallery.fitGridToPage();
        $(window).resize(gallery.fitGridToPage);
    };


    gallery.fitGridToPage = function() {
        $('.st-image-collection.st-image-collection-default.image-grid').each(function() {
            var images = [];
            var imgBySrc = {};
            var $ul = $(this);
            
            $ul.find('img').each(function() {
                var $img = $(this);
                console.log('push img ', $img);
                var ratio = parseInt($img.attr('data-width'), 10) / parseInt($img.attr('data-height'), 10) || 1;
                images.push({src: $img.attr('src'), data: {}, ratio: ratio});
                imgBySrc[$img.attr('src')] = $img;
            });

            var targetHeight = 250;
            if ($ul.hasClass('image-grid-medium')) {
                targetHeight = 450;
            }
            var width = ($(this).parent().width() || 400) - 20;
            var rows = perfectLayout(images, width, targetHeight, {margin: 0});
            console.log('width ', width);
            console.log('rows' , rows);
            rows.forEach(function(row) {
                if (!row.length) {
                    row = [row];
                }
                row.forEach(function(imgRow) {
                    var $img = imgBySrc[imgRow.src];
                    $img.height(imgRow.height);
                    $img.width(imgRow.width);
                    console.log('imgRwo ', imgRow);
                });
            });
            
        });

    };
    

    gallery.initSlider = function() {
        $('.st-image-collection-wrapper.image-slider').each(function() {
            console.log('add unslider ', this);
            $(this).unslider({autoplay: true, delay: 7000, arrows: false});
        });
    };

    gallery.initPhotoswipe = function() {
        if ( $('.st-image-collection.with-slideshow').length === 0) {
            return;
        }
        console.log('init photo swipe');
        require([ 
            'photoswipe', 
            'photoswipe_ui_default' 
        ], function( PhotoSwipe, PhotoSwipeUI_Default ) {
            //      var gallery = new PhotoSwipe( someElement, PhotoSwipeUI_Default ...
            //      gallery.init() 
            //      ...
            $('.st-image-collection.with-slideshow').each(function(galleryIndex) {
                var slides = [];
                var galleryId = 'st-gallery-' + galleryIndex;
                $(this).find('.item').each(function() {
                    var $li = $(this)
                    var $img = $li.find('img');
                    slides.push({
                        src: $img.attr('data-original-src'),
                        msrc: $img.attr('src'),
                        w: parseInt($img.attr('data-original-width'), 10),
                        h: parseInt($img.attr('data-original-height'), 10),
                        title: $li.find('.image-caption').html()
                    });
                });

                var opener = function(index, evt) {
                    evt.preventDefault();
                    evt.stopPropagation();
                    
                    var $target = $(PHOTO_SWIPE_HTML).attr('id', 'photo-swipe-' + galleryId);
                    
                    $(document.body).append($target);
                    var gallery = new PhotoSwipe(
                        $target.get(0),
                        PhotoSwipeUI_Default,
                        slides,
                        {
                            index: index,
                            closeOnScroll: false,
                            galleryUID: galleryId
                        }
                    );
                    gallery.init();
                }

                // TODO: Get current image from URL
                
                $(this).find('.item img').each(function(index) {
                    var $img = $(this)
                    var itemOpener = opener.bind(this, index);
                    $img.click(itemOpener);
                });


                
            });
        });
    };

    var PHOTO_SWIPE_HTML = "<div class=\"pswp\" tabindex=\"-1\" role=\"dialog\" aria-hidden=\"true\">\n\n    <!-- Background of PhotoSwipe. \n         It's a separate element as animating opacity is faster than rgba(). -->\n    <div class=\"pswp__bg\"></div>\n\n    <!-- Slides wrapper with overflow:hidden. -->\n    <div class=\"pswp__scroll-wrap\">\n\n        <!-- Container that holds slides. \n            PhotoSwipe keeps only 3 of them in the DOM to save memory.\n            Don't modify these 3 pswp__item elements, data is added later on. -->\n        <div class=\"pswp__container\">\n            <div class=\"pswp__item\"></div>\n            <div class=\"pswp__item\"></div>\n            <div class=\"pswp__item\"></div>\n        </div>\n\n        <!-- Default (PhotoSwipeUI_Default) interface on top of sliding area. Can be changed. -->\n        <div class=\"pswp__ui pswp__ui--hidden\">\n\n            <div class=\"pswp__top-bar\">\n\n                <!--  Controls are self-explanatory. Order can be changed. -->\n\n                <div class=\"pswp__counter\"></div>\n\n                <button class=\"pswp__button pswp__button--close\" title=\"Close (Esc)\"></button>\n\n                <button class=\"pswp__button pswp__button--share\" title=\"Share\"></button>\n\n                <button class=\"pswp__button pswp__button--fs\" title=\"Toggle fullscreen\"></button>\n\n                <button class=\"pswp__button pswp__button--zoom\" title=\"Zoom in/out\"></button>\n\n                <!-- Preloader demo http://codepen.io/dimsemenov/pen/yyBWoR -->\n                <!-- element will get class pswp__preloader--active when preloader is running -->\n                <div class=\"pswp__preloader\">\n                    <div class=\"pswp__preloader__icn\">\n                      <div class=\"pswp__preloader__cut\">\n                        <div class=\"pswp__preloader__donut\"></div>\n                      </div>\n                    </div>\n                </div>\n            </div>\n\n            <div class=\"pswp__share-modal pswp__share-modal--hidden pswp__single-tap\">\n                <div class=\"pswp__share-tooltip\"></div> \n            </div>\n\n            <button class=\"pswp__button pswp__button--arrow--left\" title=\"Previous (arrow left)\">\n            </button>\n\n            <button class=\"pswp__button pswp__button--arrow--right\" title=\"Next (arrow right)\">\n            </button>\n\n            <div class=\"pswp__caption\">\n                <div class=\"pswp__caption__center\"></div>\n            </div>\n\n        </div>\n\n    </div>\n\n</div>";
    
    $(document).ready(gallery.init);


}());
