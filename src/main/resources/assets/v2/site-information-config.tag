<site-information-config>
    <h2>Site Information</h2>
    <form onsubmit={saveSettings} name="settingsForm">
        <div class="form-group">
            <label>Site Title</label>
            <input class="form-control" name="siteTitle">
        </div>
        <div class="form-group">
            <label>Site Description</label>
            <textarea class="form-control" name="siteDescription"></textarea>
        </div>
        <div class="form-group">
            <button type="submit" class="btn btn-md btn-primary">Save Changes</button>
        </div>
    </form>
    <script>
     var self = this;
     self.mixin('databind');

     self.saveSettings = function(evt) {
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
</site-information-config>
