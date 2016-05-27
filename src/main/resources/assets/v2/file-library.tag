<file-uploader>
    <div>
        <h3>Upload file</h3>
        <form action="/st-publisher/upload-file"
              class="dropzone"
              id="my-awesome-dropzone">
            
        </form>        
    </div>
    <script>
     var self = this;

     self.on('mount', function() {
         self.dropzone = new Dropzone(".dropzone", {
             dictDefaultMessage: "Drag one more more files here. Or click to open a file picker."
         });
     });
   
    </script>
</file-uploader>

<file-library>
    <div>
        <h3>File Library</h3>
        <h3 if={loading}>Loading &hellip;</h3>
        <h3 if={!loading && !items.length}>No posts yet</h3>
        <div class="form-group">
            <a href="#/file-upload" class="btn btn-primary btn-xl">Upload new file</a>
        </div>
        <table if={!loading && items.length} class="pure-table comments-table">
            <thead>
                <tr>
                    <th></th>                    
                    <th>Name</th>
                    <th></th>                    
                    <th>Uploaded</th>                    
                </tr>
            </thead>
            <tbody>
                <tr each={item in items}>
                    <td>
                        <a href="{item.fullUrl}" target="_blank">open</a>
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

     smartFormatDate = function(date) {
         var m = moment(date);
         return m.fromNow();
     }
     
     
     this.fetchData = function() {
         stallion.request({
             url: '/st-publisher/files?page=' + self.page + '&deleted=' + self.withDeleted,
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
</file-library>
