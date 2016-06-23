

<image-selector>
    <div>
        <ul class="nav nav-tabs" role="tablist">        
            <li role="presentation" class="active"><a class="" href="javascript:;" onclick={showTab.bind(this, 'library')}>Image Library</a></li>
            <li role="presentation"><a href="javascript:;"  onclick={showTab.bind(this, 'upload')}>Upload</a></li>
            <li show={!opts.hideurltab} role="presentation"><a href="javascript:;" onclick={showTab.bind(this, 'url')}>Web Address (URL)</a></li>
        </ul>
    </div>
    <div if={tab==="library"}>
        <image-library callback={selectImage}></image-library>
    </div>
    <div if={tab==="upload"}>
        <image-uploader callback={selectImage}></image-uploader>
    </div>
    <div if={tab==="url"}>
        <div class="form-group"></div>
        <div class="form-group">
            <label>Insert the URL of the image here:</label>
            <input type="text" class="form-control" name="src" >
        </div>
        <div class="form-group">
            <button class="btn btn-primary" onclick={urlChange}>Use this image</button>
        </div>
    </div>
    <script>
     var self = this;
     self.tab = 'library';


     self.showTab = function(tab) {
         self.update({tab: tab});
     }

     self.urlChange = function() {
         self.selectImage({url: self.src.value});
     }

     self.selectImage = function(imageInfo) {
         if (self.opts.callback) {
             self.opts.callback(imageInfo);
         } else {
             console.log("No image selection callback defined");
         }
     };
    </script>
</image-selector>


<image-uploader>
    <div>
        <h3 class="title">Upload file</h3>
        <form action="/st-publisher/upload-file"
              class="image-dropzone dropzone"
              id="my-image-dropzone">
            
        </form>        
    </div>
    <script>
     var self = this;

     self.on('mount', function() {
         self.dropzone = new Dropzone(self['my-image-dropzone'], {
             dictDefaultMessage: "Drag one more more files here. Or click to open a file picker.",
             uploadMultiple: false,
             //             parallelUploads: true,
             maxFiles: 1,
             acceptedFiles: 'image/*,.jpg,.png,.svg,.gif',
             init: function() {
                 this.on("success", function(file, response) { 
                     //var o = JSON.parse(response);
                     self.opts.callback(response);
                     this.removeFile(file);
                 });
             },
         });
     });

     /*
     self.on('before-unmount', function() {
         console.log('unmount image uploader');
         self.dropzone.
     });
     */

     
    </script>

</image-uploader>


<image-library>
    <div>
        <h3>File Library</h3>
        <h3 if={loading}>Loading &hellip;</h3>
        <h3 if={!loading && !items.length}>No posts yet</h3>
        <table if={!loading && items.length} class="pure-table comments-table">
            <thead>
                <tr>
                    <th></th>
                    <th></th>                    
                    <th>Name</th>
                    <th></th>                    
                    <th>Uploaded</th>     
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <tr each={item in items}>
                    <td>
                        <a class="btn btn-primary" href="javascript:;" onclick={selectImage.bind(this, item)}>Choose</a>
                    </td>
                    <td>
                        <img src="{item.thumbUrl}" style="max-width: 100px; max-height: 100px;">
                    </td>
                    <td>
                        {item.name}
                    </td>
                    <td>
                        {item.extension}
                    </td>
                    <td>
                        {moment(item.uploadedAt * 1000).fromNow()}
                    </td>
                    <td>
                        <a href="{item.url}" target="_blank">open</a>
                    </td>

                </tr>
            </tbody>
        </table>
    </div>
    <script>
     var self = this;
     self.loading = true;
     self.items = null;
     self.pager = null;
     self.page = 1;
     self.withDeleted = false;

     self.selectImage = function(item) {
         if (self.opts.callback) {
             self.opts.callback(item);
         } else {
             console.log('no image callback defined');
         }
     };

     smartFormatDate = function(date) {
         var m = moment(date);
         return m.fromNow();
     }
     
     
     this.fetchData = function() {
         stallion.request({
             url: '/st-publisher/images?page=' + self.page + '&deleted=' + self.withDeleted,
             success: function (o) {
                 self.pager = o.pager;
                 self.items = o.pager.items;
                 self.loading = false;
                 self.update();
             },
             error: function(o, form, xhr) {
                 console.log('error loading dashboard', o, xhr);
             }
         });

     };
     
     this.on('mount', function(){
         self.fetchData();
     });     

    </script>
    
</image-library>



<image-simple-formatting>
    <div>
        <form onsubmit={onContinue}>
            <div class="form-group">
                <label>Caption (optional)</label>
                <input type="text" class="form-control" caption="caption" />
            </div>
            <div class="form-group">
                <label>Link (optional)</label>
                <input type="text" class="form-control" name="link"" />
            </div>
            <div class="form-group">
                <button class="btn btn-primary">Continue</button>
            </div>
        </form>
    </div>
    <script>
     var self = this;
     self.mixin('databind');

     self.onContinue = function(evt) {
         evt.preventDefault();
         self.opts.callback(self.getFormData());
     }
    </script>
</image-simple-formatting>
