<template-widgets-config>
    <div show={loading}>
        Loading &hellip;
    </div>
    <div show={!loading}>
        <h1>Template Widgets</h1>
        <h3 if={!modules.length}>No template widgets</h3>
        <table if={modules.length} class="pure-table table table-striped">
            <thead>
                <tr>
                    <th></th>                    
                    <th>Name</th>
                    <th></th>                    
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <tr each={item in modules}>
                    <td>
                        <a class="btn btn-xs btn-default" href="#/settings/edit-widget/{item.name}" >Edit</a>
                    </td>
                    <td>
                        {item.label || item.name}
                    </td>
                    <td>

                    </td>
                    <td>

                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <script>
     var self = this;
     var loading = true;
     self.modules = [];
     
     self.on('mount', function() {
         stallion.request({
             url: '/st-publisher/global-modules/list',
             method: 'GET',
             success: function(o) {
                 self.update({modules: o.modules});
             }
         });
     });
     
    </script>
</template-widgets-config>


<template-widgets-edit>
    <div show={loading}>
        <h3>Loading global module for editing &hellip;</h3>
    </div>
    <div show={!loading}>
        <div class="row">
            <div class="col-sm-4">
                <h3 style="margin-bottom: 0em;">Edit module {module.label || module.name.replace('_', ' ') }</h3>
                <h6 style=""">{module.name}</h6>
                <p><span id="lastAutosaveSpan">{lastAutoSaveAt}</span></p>
            </div>
            <div class="col-sm-2" style="text-align:right;">
                <p>
                    <button onclick={publish} style="margin-top: 1.2em;" class="btn btn-primary btn-xl">Publish Changes</button>
                </p>
                <p>
                    <a href="/st-publisher/global-modules/preview?versionId={versionId}" target="_blank" class="btn btn-default btn-xl">Preview Changes</a>  
                </p>
                
            </div>
            <div class="col-sm-6">
            </div>
        </div>
        <div class="row">
            <div class="col-sm-6">
                <markdown-editor onchange={onMarkdownChange} name="markdownEditor" ></markdown-editor>
            </div>
        </div>
    </div>
    <script>
     var self = this;
     self.loading = true;
     self.module = {};
     self.lastAutoSaveAt = '';
     self.versionId = null;

     self.on('mount', function() {
         stallion.request({
             url: '/st-publisher/global-modules/get-draft/' + self.opts.name,
             method: 'GET',
             success: function(o) {
                 self.versionId = o.id;
                 //self.update({module: o, loading: false});
                 self.update({loading: false, module: o});
                 self.tags.markdownEditor.setData(o.rawContent, o.widgets);
                 
             }
         });

     });

     self.onMarkdownChange = function() {
         console.log('markdown change');
         debouncedAutoSave();
     };

     self.publish = function() {
         stallion.request({
             url: '/st-publisher/global-modules/publish/' + self.opts.name,
             method: 'POST',
             data: {versionId: self.versionId},
             success: function(o) {
                 stallion.showSuccess("Global module has been published!");
             }
         });
     }

     function saveDraft() {
         console.log('saveDraft');
         var data = self.tags.markdownEditor.getData();

         stallion.request({
             url: '/st-publisher/global-modules/update-draft/' + self.opts.name,
             method: 'POST',
             data: {
                 widgets: data.widgets,
                 rawContent: data.markdown
             },
             success: function(o) {
                 self.update({versionId: o.id, lastAutoSaveAt: 'Last auto-saved at ' + moment().format('hh:mm:ss a')});
             }
         });
     };


     var debouncedAutoSave = stPublisher.debounce(saveDraft, 1000, false);
    </script>
</template-widgets-edit>
