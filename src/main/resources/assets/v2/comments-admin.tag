
<comments-table>
    <div>
        <h3>Comments</h3>
        <h3 if={loading}>Loading &hellip;</h3>
        <h3 if={!loading && !items.length}>No comments yet</h3>
        <table if={!loading && items.length} class="pure-table comments-table table table-striped">
            <thead>
                <tr>
                    <th></th>                    
                    <th>State</th>                    
                    <th>Created</th>
                    <th>Author</th>
                    <th>Comment</th>
                </tr>
            </thead>
            <tbody>
                <tr each={item in items}>
                    <td>
                        <span if={!item.approved} style="width: 80px;display:inline-block;"><a  class="btn btn-xs btn-default" href="{item.fullUrl}" target="_blank">approve</a></span>
                        <span if={item.approved} style="width: 80px;display:inline-block;"><a class="btn btn-xs btn-default" href="{item.fullUrl}" target="_blank">un-approve</a></span>
                        <span style="width: 40px;display:inline-block;"><a if={!item.deleted} class="btn btn-xs btn-default" href="{item.fullUrl}" target="_blank">trash</a></span>
                    </td>
                    <td>
                        <span if={item.approved}>Approved</span>
                        <span if={!item.approved}>{item.state.replace('_', ' ')}</span>
                    </td>
                    <td>
                        {moment(item.createdTicks).fromNow()}
                    </td>
                    <td>
                        <div>{item.authorDisplayName}</div>
                        <div>{item.authorEmail}</div>
                        <div>{item.authorWebSite}</div>
                    </td>
                    <td style="height: 70px;">
                        {item.bodyMarkdown}
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
             url: '/st-publisher/comments/list?page=' + self.page + '&deleted=' + self.withDeleted,
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
</comments-table>
