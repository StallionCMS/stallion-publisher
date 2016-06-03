<extra-html-config>
    <h3>Extra HTML and CSS</h3>
    <form onsubmit={save}>
        <div class="form-group">
            <label>Head HTML</label>
            <textarea class="form-control" name="headHtml"></textarea>
        </div>
        <div class="form-group">
            <label>Footer HTML</label>
            <textarea class="form-control" name="footerHtml"></textarea>
        </div>
        <div class="form-group">
            <button class="btn btn-primary">Save Changes</button>
        </div>
    </form>
    <script>
     var self = this;
     self.mixin('databind');

     self.save = function(evt) {
         evt.preventDefault();
         stallion.request({
             url: '/st-publisher/update-site-settings',
             method: 'POST',
             form: self.settingsForm,
             data: self.processForm(),
             success: function(o) {
                 stallion.showSuccess("Saves change");
             }
         });
     }

     self.on('mount', function() {
         stallion.request({
             url: '/st-publisher/site-settings',
             method: 'GET',
             success: function(o) {
                 console.log('updateData ', o);
                 self.updateData(o);
             }
         });
     });
    </script>
</extra-html-config>
