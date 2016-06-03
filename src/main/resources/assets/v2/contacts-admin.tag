
<contacts-table>
    <div>
        <h3>Contacts</h3>
        <h3 if={loading}>Loading &hellip;</h3>
        <h3 if={!loading && !items.length}>No comments yet</h3>
        <table if={!loading && items.length} class="pure-table comments-table table table-striped">
            <thead>
                <tr>
                    <th></th>                    
                    <th>Name</th>                    
                    <th>Email</th>
                    <th>Created</th>
                </tr>
            </thead>
            <tbody>
                <tr each={item in items}>
                    <td>
                        <a href="#/contact-details/{item.id}" class="btn btn-default btn-xs">Details</a>
                    </td>
                    <td>
                        <div>{item.displayName}</div>
                    </td>
                    <td>
                        <div>{item.email}</div>
                    </td>
                    <td>
                        {moment(item.createdTicks).fromNow()}
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
             url: '/st-publisher/contacts/list?page=' + self.page + '&deleted=' + self.withDeleted,
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
</contacts-table>
