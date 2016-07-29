<style lang="scss">
 .st-data-table-vue {
     .data-table-header {
         padding-right: 20px;
         .table-title {
             display: inline-block;
             margin: 0px 0px 0px 20px;
             font-weight: 200;
             vertical-align: -15%;
         }
         form {
             display: inline-block;
         }
         .search-input-group {
             width: 500px;
             margin-left: 20px;
             .form-control {
                 width: 450px;
                 height: 40px;
             }
         }
         .btn {
             height: 40px;
             padding-top: 5px;
             float: right;
         }    
         
         .btn.search-button {
             display: inline-block;
             margin-left: -1px;
             float: left;
         }
         .cancel-search {
             position: relative;
             position: absolute;
             margin-left: -27px;
             z-index: 10;
             display: inline-block;
             height: 50px;
             padding-top: 7px;
             width: 30px;
             font-size: 20px;
             color: #aaa;
             cursor: pointer;
         }
         .cancel-search:hover {
             color: #555;
         }
                  
         [slot="actions"] {
             display: inline-block;
             float: right;
             text-align: right;
         }
     }
     
 }
 .st-data-table-vue.fixed-headers {
     thead, .data-table-header {

     }
     .data-table-header {

     }
     table.st-data-table {
         margin: 0px;
     }
     .table-wrap {
         margin-top: 2em;
         background-color: #f9f9f9;
         border-top: 1px solid #ddd;
         border-bottom: 1px solid #ddd;
     }
     tbody {

     }
     tfoot {
         display: none;
     }

 }
 
</style>
<template>
    <div class="st-data-table-vue fixed-headers" v-bind:class="{'fixed-headers': this.infiniteScroll}">
        <div class="data-table-header">
            <slot name="header">
                <h2 class="table-title">{{ title }}</h2>
                <form @submit.prevent="doSearch" class="table-search form-inline">
                    <div class="input-group search-input-group">
                        <input type="text" class="form-control search-field" placeholder="Search for article" v-model="searchTerm">
                        <span v-show="searchTerm && searchTerm.length >= 3" @click="searchTerm=''" class="cancel-search">✕</span>
                        <button type="submit" class="btn btn-primary input-group-addon search-button"><i class="material-icons">search</i></button>
                    </div>
                </form>
                <slot name="filters">

                </slot>                
                <slot name="actions">

                </slot>
            </slot>
        </div>
        <div class="table-wrap">
        <table class="st-data-table table {{ tableClass }}">
            <colgroup>
                <col v-for="col in columns" :style="col.style"></col>
            </colgroup>
            <thead>
                <tr>
                    <th v-for="col in columns">{{ col.title }}</th>
                </tr>
            </thead>
            <tbody v-if="loading">
                <tr>
                    <td  colspan="{{ columns.length }}">
                        Loading data &hellip;
                    </td>
                </tr>
            </tbody>
            <tbody v-if="!loading && !items.length">
                <tr>
                    <td colspan="{{ columns.length }}">
                        <div v-if="searchTerm">No {{ labelPlural }} found matching search term "{{ searchTerm }}".</div>
                        <div v-else>No {{ labelPlural }} found.</div>
                    </td>
                </tr>
            </tbody>
            <tbody v-if="!loading && items.length">
                <tr v-for="(rowNumber, item) in items" v-if="!item.$hidden" track-by="id">
                    <td v-for="(colNumber, col) in columns" @dblclick="onCellDoubleClicked(item, col, rowNumber, colNumber, $event)">
                        <template v-if="item.$isEditing">
                            <component :is="col.editableComponent" :item="item" :col="col" :row-number="rowNumber" :col-number="colNumber" :callback="onEditCallback" :refresh="refresh"></component>
                        </template>
                        <template v-if="!item.$isEditing">
                            <template v-if="col.component">
                                <component :is="col.component" :item="item" :col="col" :row-number="rowNumber" :col-number="colNumber"></component>
                            </template>
                            <template v-if="col.actions">
                                <button v-for="action in col.actions" class="{{ action.className }}" @click="triggerItemEvent(action.event, item, col, rowNumber, colNumber)">{{ action.label }}</button>
                            </template>
                            <template v-if="!col.component && !col.actions">
                                <template v-if="col.allowHtml">
                                    {{{ renderCell(item, col, rowNumber, colNumber) }}}
                                </template>
                                <template v-else>
                                    {{ renderCell(item, col, rowNumber, colNumber) }}
                                </template>
                            </template>
                        </template>
                    </td>
                </tr>
                <tr v-if="!hasMore">
                    <td colspan="{{ columns.length }}">
                        All matching {{ labelPlural }} have been loaded.
                    </td>
                </tr>
                <tr v-if="isScrolling">
                    <td colspan="{{ columns.length }}">
                        Fetching additional {{ labelPlural }} &hellip;
                    </td>
                </tr>
            </tbody>
            <tfoot>
                <tr>
                    <td colspan="{{ columns.length }}">
                        <data-table-pager :make-link="makePageLink" "v-if="pager!==null" :page="page" :pager="pager" :base-path="'!/posts'" :page="page"></data-table-pager>
                    </td>
                </tr>
            </tfoot>
        </table>
        </div><!-- /table-wrap -->
    </div>
</template>
<script>
 module.exports = {
     props: {
         label: {
             type: String,
             required: true
         },
         tableClass: {
             type: String,
             default: 'table'
         },
         dataUrl: {
             required: false,
             type: String             
         },
         loadData: {
             type: Function
         },
         browserUrlTemplate: {
             required: true,
             type: String
         },
         columns: {
             type: Array,
             required: true
         },
         autoSearch: {
             default: false
         },
         isAttached: true,
         initialDataLoaded: false,
         labelPlural: '',
         searchTerm: String,
         sortDirection: String,
         sortField: String,
         page: Number,
         route: Object,
         filters: Array,
         infiniteScroll: true
     },
     data: function() {
         var self = this;

         if (!this.loadData && !this.dataUrl) {
             console.log("either load-data callback function is required or 'data-url' property must be passed in.");
             return;
         }

         // Set defaults

         
         var columnsNew = [];
         this.columns.forEach(function(col) {
             if (col === '__row_selector') {
                 columnsNew.push({
                     isRowSelector: true
                 });
             } else if (typeof(col) === 'string') {
                 columnsNew.push({
                     field: col,
                     title: self.toTitleCase(col)
                 });
             } else {
                 if (!col.title && col.field) {
                     col.title = self.toTitleCase(col.field);
                 }
                 columnsNew.push(col);
             }
         });
         this.columns = columnsNew;
         this.labelPlural = this.labelPlural || this.label + 's';
         this.title = this.title || this.toTitleCase(this.labelPlural);
         this.sortField = this.sortField || '';
         this.sortDirection = this.sortDirection || '';
         this.searchTerm = this.searchTerm || '';
         this.filters = this.filters || [];
         this.page = this.page || 1;

         // If route exists, parse from route

         if (this.route) {
             this.page = parseInt(this.route.params.page || this.route.query.page || 1, 10) || 1;
             this.searchTerm = this.route.search || '',
             this.sortField = this.route.sort || '';
             if (this.sortField.indexOf('-') === 0) {
                 this.sortDirection = 'desc';
                 this.sortField = this.sortDirection.substr(1);
             } else {
                 this.sortDirection = 'asc';
             }
             this.filters = [];
             if (this.route.query.filters) { 
                 try {
                     this.filters = JSON.parse(this.route.query.filters);
                 } catch(e) {
                     stallion.showError('Invalid "filters" in query');
                     console.log(e, this.route.query.filters);
                 }
             }
         }

         // Build the data object

         var data = {
             items: [],
             loading: true,
             hasMore: false,
             isScrolling: false,
             scrollPage: 1
         };

         data.filtersByField = {};
         this.filters.forEach(function(filter) {
             data.filtersByField[filter.name] = filter.value;
         });
         return data;

     },
     attached: function() {
         
         var self = this;
         this.isAttached = true;
         if (this.initialDataLoaded) {
             this.afterAttachedAndLoaded();
         }
     },
     created: function() {
         this.loadInitialData();
     },
     methods: {
         doSearch: function() {
             this.page = 1;
             this.loadInitialData();
         },
         afterAttachedAndLoaded: function() {
             var self = this;
             setTimeout(function() {
                 var $table = $(self.$el).find('table');
                 self.scrolify($table, $(window).height() - (40 + 28 + 38 + 20));
             }, 50);
         },
         onEditCallback: function() {
             
         },
         onCellDoubleClicked: function(item, col, rowNumber, colNumber, $event) {
             if (col.editableComponent) {
                 item.$isEditing = true;
             }
         },
         renderCell: function(item, col, index) {
             if (typeof(col) === 'string') {
                 return item[col];
             } else if (col.render) {
                 return col.render(item, col, index);
             } else {
                 var format = col.format || '';
                 var value = item[col.field];
                 if (format === 'currency') {
                     return value
                 } else if (format === 'number') {
                     return value;
                 } else if (format.indexOf('string:') === 0) {
                     return value;
                 } else if (format.indexOf('moment:') === 0) {
                     
                 }
                 return value;
             }
         },
         toTitleCase: function(str) {
             return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
         },
         triggerItemEvent: function(event, item, col, rowNumber, colNumber) {
             console.log('trigger item event ', event, item, col);
             this.$dispatch(event, item, col, rowNumber, colNumber);
         },
         refresh: function() {
             this.loading = true;
             this.loadInitialData();
         },
         loadInitialData: function() {
             var self = this;
             this.loading = true;
             function callback(pager) {
                 console.log('called back! ', pager);
                 self.pager = pager;
                 self.items = pager.items;
                 self.loading = false;
                 self.hasMore = pager.hasNextPage;
                 console.log('has more data ', self.hasMore);
                 if (!self.initialDataLoaded) {
                     self.initialDataLoaded = true;
                     if (self.isAttached) {
                         self.afterAttachedAndLoaded();
                     }
                 }
             };
             self.doFetchData(callback);
         },
         appendData: function() {
             var self = this;
             if (this.isScrolling || !this.hasMore) {
                 return;
             }
             this.isScrolling = true;
             function callback(pager) {
                 self.items = self.items.concat(pager.items);
                 //self.items = [{}];
                 //console.log('found new items ', pager.items.length, pager.items[pager.items.length-1].title);
                 //self.pager = pager;
                 //self.$set('items', pager.ite);
                 self.hasMore = pager.hasNextPage;
                 console.log('has more data ', self.hasMore);
                 self.fetchingMore = false;
                 self.isScrolling = false;
                 
             };
             this.scrollPage++;
             console.log('fetch more page ', this.scrollPage);
             self.doFetchData(callback, this.scrollPage);
         },
         doFetchData: function(callback, page) {
             var self = this;
             
             page = page || this.page;
             if (this.loadData) {
                 this.loadData(this, page, callback);
             } else {
                 var url = this.dataUrl;
                 url += '?page=' + page;
                 if (this.searchTerm.length > 2) {
                     url += '&search=' + encodeURIComponent(this.searchTerm);
                 }
                 if (this.sortField) {
                     var sort = this.sortField;
                     if (this.sortDirection.toLowerCase() == 'desc') {
                         sort = '-' + sort;
                     }
                     url += '&sort=' + sort;
                 }
                 stallion.request({
                     url: url,
                     success: function(o) {
                         callback(o.pager);
                     }
                 });
             }
         },
         makePageLink: function(page) {
             return this.makeUrl(page, this.searchTerm, this.filters, this.sortField, this.sortDirection);
         },
         hardRefresh: function() {
             var url = this.makePageLink(this.page) + '&t=' + new Date().getTime();
             window.location.hash = url;
         },
         makeUrl: function(page, searchTerm, filters, sortField, sortDirection) {
             var sort = '';
             if (sortField) {
                 sort = sortField;
                 if (sortDirection.toLowerCase() === 'desc') {
                     sort = '-' + sort;
                 }
             }
             var template = this.browserUrlTemplate;
             if (template.indexOf('?') === -1) {
                 template += '?search={{searchTerm}}&filters={{filters}}&sort={{sort}}&page={{page}}';
             }
             var url = template;
             var params = {
                 page: page,
                 searchTerm: searchTerm,
                 filters: JSON.stringify(filters),
                 sort: sort
             };
             Object.keys(params).forEach(function(key) {
                 url = url.replace(new RegExp('{{\s*' + key + '\s*}}', 'g'), params[key] || '');
             });
             return url;
         },
         scrolify: function(tblAsJQueryObject, height){
             var self = this;

             var oTbl = tblAsJQueryObject;

             // for very large tables you can remove the four lines below
             // and wrap the table with <div> in the mark-up and assign
             // height and overflow property  
             var oTblDiv = $("<div/>").addClass('scrollable-wrapper');
             oTblDiv.css('height', height);
             oTblDiv.css('overflow-y','scroll');               
             oTbl.wrap(oTblDiv);

             // save original width
             oTbl.attr("data-item-original-width", oTbl.width());
             oTbl.find('thead tr th').each(function(){
                 $(this).attr("data-item-original-width",$(this).width());
             }); 
             oTbl.find('tbody tr:eq(0) td').each(function(){
                 $(this).attr("data-item-original-width",$(this).width());
             });                 


             // clone the original table
             var newTbl = oTbl.clone();

             // remove table header from original table
             oTbl.find('thead tr').remove();                 
             // remove table body from new table
             newTbl.find('tbody tr').remove();   

             oTbl.parent().parent().prepend(newTbl);
             newTbl.wrap("<div/>");

             // replace ORIGINAL COLUMN width                
             newTbl.width(newTbl.attr('data-item-original-width'));
             newTbl.find('thead tr th').each(function(){
                 $(this).width($(this).attr("data-item-original-width"));
             });     
             oTbl.width(oTbl.attr('data-item-original-width'));      
             oTbl.find('tbody tr:eq(0) td').each(function(){
                 $(this).width($(this).attr("data-item-original-width"));
             });        
             $(this.$el).find(".scrollable-wrapper").on('scroll', function() {
                 console.log('wrap scrolL!');
                 if(($(this).scrollTop() + $(this).innerHeight() + 20) >= $(this)[0].scrollHeight) {
                     console.log('fetch new data!');
                     self.appendData();
                 }
             });
             /*
             oTblDiv.on('scroll', function() {
                console.log('scrolL!');
                 if($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight) {
                     alert('end reached');
                 }
             })*/         
         }
     },
     watch: {
         searchTerm: function(cur, prev) {
             console.log('seach term changed ', cur, prev);
             if (this.loading) {
                 return;
             }
             if (cur !== prev) {
                 if (cur === '' || cur.length >= 3) {
                     console.log('reload data');
                     this.page = 1;
                     this.loadInitialData();
                 }
             }
         } 
     }
 };
</script>
