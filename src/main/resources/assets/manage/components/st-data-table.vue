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
             width: 300px;
             margin-left: 20px;
             .form-control {
                 width: 250px;
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
         [slot="actions"], [slot="filters"] {
             display: inline-block;
             float: right;
             text-align: right;
             margin-left: 20px;
         }
     }
     .cell-action-link {
         display: inline-block;
         margin-right: 5px;
     }
     .cell-editable {
         border-left: 1px solid transparent;
         border-right: 1px solid transparent;
     }
     .cell-editable:hover {
         cursor: pointer;
         border: 1px dashed #ccc;
     }
     thead {
         background-color: #f9f9f9;
         border-top: 1px solid #DDD;
     }
     tbody>tr:nth-of-type(even) {
         background-color: #f9f9f9;
     }         

     th.sortable:hover {
         background-color: #eee;
         cursor: pointer;
     }
     th.sorted {
         background-color: #ccc;
     }
     th.sorted.sorted-desc:after {
         content: " \25BE";
     }
     th.sorted.sorted-asc:after {
         content: " \25B4";
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
    <div class="st-data-table-vue" v-bind:class="{'fixed-headers': this.infiniteScroll}">
        <div class="data-table-header">
            <slot name="header">
                <slot name="title">
                    <h2 class="table-title">{{ titleComputed }}</h2>
                </slot>
                <slot name="search">
                    <form @submit.prevent="doSearch" class="table-search form-inline">
                        <div class="input-group search-input-group">
                            <input type="text" class="form-control search-field" placeholder="Search for {{ label }}" v-model="searchTerm">
                            <span v-show="searchTerm && searchTerm.length >= 3" @click="navigate({page: 1, searchTerm: ''})" class="cancel-search">✕</span>
                            <button type="submit" class="btn btn-primary input-group-addon search-button"><i class="material-icons">search</i></button>
                        </div>
                    </form>
                </slot>
                </slot>
                <slot name="actions">

                </slot>
                <slot name="filters">
                    
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
                    <th v-bind:class="[sortField===col.field ? 'sorted' : '', sortDirection==='desc' ? 'sorted-desc': '', col.sortable ? 'sortable' : '', sortDirection==='asc' ? 'sorted-asc': '', 'header-' + col.className, 'st-header-cell']" v-for="col in columns" @click="sortColumn(col)" v-if="!col.hidden">{{ col.title }}</th>
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
                        <div v-if="searchTerm">No {{ labelPluralComputed }} found matching search term "{{ searchTerm }}".</div>
                        <div v-else>No {{ labelPluralComputed }} found.</div>
                    </td>
                </tr>
            </tbody>
            <tbody v-if="!loading && items.length">
                <tr v-for="(rowNumber, item) in items" v-if="!item.$hidden" track-by="id" class="data-table-row-index-{{ rowNumber }} data-table-row-id-{{ item.id }}">
                    <td v-for="(colNumber, col) in columns" @click="onCellClicked(item, col, rowNumber, colNumber, $event)" @dblclick="onCellDoubleClicked(item, col, rowNumber, colNumber, $event)" v-bind:class="[col.editableComponent? 'cell-editable': '', col.className]" v-if="!col.hidden" :style="col.$widthWithPadding ? 'width: ' + (col.$widthWithPadding) + 'px;': ''">
                        <template v-if="item.$isEditing === colNumber">
                            <component :is="col.editableComponent" :item="item" :col="col" :row-number="rowNumber" :col-number="colNumber" :callback="onEditCallback" :refresh="refresh" :cancel="cancelEdit"></component>
                        </template>
                        <template v-if="item.$isEditing !== colNumber">
                            <template v-if="col.component">
                                <component :is="col.component" :item="item" :col="col" :row-number="rowNumber" :col-number="colNumber"></component>
                            </template>
                            <template v-if="col.getLink">
                                <a v-bind:href="col.getLink(item)">{{ col.getLabel(item) }}</a>
                            </template>
                            <template v-if="col.actions">
                                <a v-for="action in col.actions" class="cell-action-link {{ action.className }}" v-show="action.shown ? action.shown(item) : true " v-bind:href="action.getLink ? action.getLink(item) : 'javascript:;' " @click="action.click ? action.click(item, col, rowNumber, colNumber) : triggerItemEvent(action.event, item, col, rowNumber, colNumber)">{{ action.getLabel ? action.getLabel(item) : action.label }}</a>
                            </template>
                            <template v-if="!col.component && !col.actions && !col.getLink">
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
                        All matching {{ labelPluralComputed }} have been loaded.
                    </td>
                </tr>
                <tr v-if="isScrolling">
                    <td colspan="{{ columns.length }}">
                        Fetching additional {{ labelPluralComputed }} &hellip;
                    </td>
                </tr>
            </tbody>
            <tfoot>
                <tr>
                    <td colspan="{{ columns.length }}">
                        <data-table-pager :make-link="makePageLink" v-if="pager!==null && !infiniteScroll" :page="page" :pager="pager" :base-path="'!/posts'" :page="page"></data-table-pager>
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
         singleClickEditing: {
             default: false
         },
         extraDataFields: {
             type: Array,
             default: function() {
                 return [];
             }
         },
         isAttached: true,
         initialDataLoaded: false,
         labelPlural: '',
         labelPluralCompueted: '',
         searchTerm: String,
         sortDirection: String,
         sortField: String,
         customFilter: String,
         page: Number,
         route: Object,
         title: String,
         titleComputed: String,         
         filters: Array,
         filterBys: Array,
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
         var index = 0;
         this.columns.forEach(function(col) {
             col.$index = index;
             index++;
             if (col.component && col.component.template) {
                 if (!col.component.props) {
                     col.component.props = {item: Object};
                 }
                 col.component = Vue.extend(col.component);
             }
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
         this.labelPluralComputed = this.labelPlural || this.label + 's';
         this.titleComputed = this.title || this.toTitleCase(this.labelPluralComputed);
         this.sortField = this.sortField || '';
         this.sortDirection = this.sortDirection || '';
         this.searchTerm = this.searchTerm || '';
         this.filters = this.filters || [];
         this.page = this.page || 1;

         // If route exists, parse from route
         if (this.route) {
             this.updateFromRoute();
         }

         // Build the data object


         
         var data = {
             items: [],
             loading: true,
             hasMore: false,
             isScrolling: false,
             scrollPage: 1,
             pager: {}
         };

         this.extraDataFields.forEach(function(field) {
             data[field] = '';
         });

         data.filtersByField = {};
         this.filters.forEach(function(filter) {
             data.filtersByField[filter.name] = filter.value;
         });
         return data;

     },
     created: function() {
         console.log('created, load initial data');
         if (!this.route) {
             this.loadInitialData();
         }
     },
     compiled: function() {
     },
     ready: function() {
     },
     attached: function() {
         var self = this;
         this.isAttached = true;
         if (this.initialDataLoaded) {
             this.afterAttachedAndLoaded();
         }
     },

     methods: {
         noop: function() {

         },
         doSearch: function() {
             //this.page = 1;
             this.navigate({page: 1});
         },
         afterAttachedAndLoaded: function() {
             var self = this;
             setTimeout(function() {
                 var $table = $(self.$el).find('table');
                 if (self.infiniteScroll) {
                     self.scrolify($table, $(window).height() - (40 + 28 + 38 + 20));
                 }
             }, 50);
         },
         onEditCallback: function(item, field, value) {
         },
         onCellDoubleClicked: function(item, col, rowNumber, colNumber, $event) {
             if (col.editableComponent && !this.singleClickEditing) {
                 item.$isEditing = colNumber;
                 this.items.$set(rowNumber, item)
             }
         },
         onCellClicked: function(item, col, rowNumber, colNumber, $event) {
             if (col.editableComponent && this.singleClickEditing) {
                 item.$isEditing = colNumber;
                 this.items.$set(rowNumber, item)
             }
         },
         cancelEdit: function(item, col) {
             item.$isEditing = false;
             this.items.$set(item.$index, item)
         },
         sortColumn: function(col) {
             if (!col.sortable) {
                 return;
             }
             if (col.field === this.sortField) {
                 if (this.sortDirection === 'asc') {
                     this.sortDirection = 'desc';
                 } else {
                     this.sortDirection = 'asc';
                 }
             } else {
                 this.sortField = col.field;
                 this.sortDirection = 'asc';
             }
             this.page = 0;
             this.navigate({page: 1});
         },
         renderCell: function(item, col, index) {
             if (typeof(col) === 'string') {
                 return item[col];
             } else if (col.render) {
                 return col.render(item, col, index);
             } else {
                 var format = col.format || '';
                 var value = item[col.field];
                 var index = col.field.indexOf('.');
                 if (value === undefined && index > -1) {
                     value = item[col.field.substr(0, index)][col.field.substr(index + 1)];
                 }
                 if (!format) {
                     return value;
                 }
                 if (format === 'number') {
                     if (value.toLocaleString) {
                         return value.toLocaleString();
                     } else {
                         return value;
                     }
                 } else if (format === 'dollars') {
                     try {
                         var fmt = new Intl.NumberFormat(
                             "en-US",
                             { style: "currency", currency: "USD",
                               minimumFractionDigits: 2 });
                         return fmt.format(value);
                     } catch(e) {
                         console.log(e);
                     }
                     return '$' + value;
                 } else if (format.indexOf('moment:') === 0 || format.indexOf('epochSeconds:') === 0) {
                     // See if value is in seconds since the epoch instead of milliseconds
                     if (!value) {
                         return '';
                     }
                     if (format.indexOf('epochSeconds:') === 0) {
                         value = value * 1000;
                     }
                     var timeZone = moment.tz.guess();
                     if (stPublisherAdminContext && stPublisherAdminContext.user && stPublisherAdminContext.timeZoneId) {
                         timeZone = stPublisherAdminContext.user.timeZoneId;
                     }
                     return moment.tz(value, timeZone).format(format.substr(format.indexOf(':') + 1));
                 } else if(format) {
                     return sprintf(format, value);
                 }
                 return value;
             }
         },
         clearFilter: function(field) {
             var filters = [];
             this.filters.forEach(function(filter) {
                 if (filter.name !== field) {
                     filters.push(filter);
                 }
             });
             this.filters = filters;
         },
         clearAllFilters: function() {
             this.filters = [];
             this.filterBys = '';
         },
         clearFiltersAndRefresh: function() {
             this.clearAllFilters();
             this.refresh();
         },
         addFilter: function(field, value, op) {
             op = op || '=';
             this.filters.push({name: field, value: value, op: op});
         },
         toTitleCase: function(str) {
             return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
         },
         triggerItemEvent: function(event, item, col, rowNumber, colNumber) {
             if (!event) {
                 return;
             }
             this.$dispatch(event, item, col, rowNumber, colNumber);
         },
         refresh: function() {
             console.log('refresh!!!');             
             this.loading = true;
             this.loadInitialData();
         },
         loadInitialData: function() {
             var self = this;
             this.loading = true;
             function callback(pager) {
                 var index = 0;
                 pager.items.forEach(function(item) {
                     item.$index = index;
                     item._index = index;
                     index++;
                 });
                 self.pager = pager;
                 self.items = pager.items;
                 
                 self.loading = false;
                 self.hasMore = pager.hasNextPage;
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
                 var index = self.items.length;
                 pager.items.forEach(function(item) {
                     item.$index = index;
                     item._index = index;
                     index++;
                 });
                 self.items = self.items.concat(pager.items);
                 //self.items = [{}];
                 //console.log('found new items ', pager.items.length, pager.items[pager.items.length-1].title);
                 //self.pager = pager;
                 //self.$set('items', pager.ite);
                 self.hasMore = pager.hasNextPage;
                 self.fetchingMore = false;
                 self.isScrolling = false;
                 
             };
             this.scrollPage++;
             self.doFetchData(callback, this.scrollPage);
         },
         doFetchData: function(callback, page) {
             var self = this;
             
             page = page || this.page;
             if (this.loadData) {
                 this.loadData(this, page, callback);
             } else {
                 var url = this.dataUrl;
                 if (!url.indexOf('?') > -1) {
                     url += '?';
                 }
                 url += '&page=' + page;
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
                 if (this.filters) {
                     url += '&filters=' + encodeURIComponent(JSON.stringify(this.filters));
                 }
                 if (this.customFilter) {
                     url += '&customFilter=' + encodeURIComponent(this.customFilter);
                 }
                 if (this.filterBys) {
                     this.filterBys.forEach(function(f) {
                         url += '&filter_by=' + f;
                     });
                 }
                 stallion.request({
                     url: url,
                     success: function(o) {
                         o.pager.items.forEach(function(item) {
                             item.$isEditing = false;
                             item.$hidden = false;
                         });
                         self.extraDataFields.forEach(function(field) {
                             console.log('check extra data field ', field);
                             if (o[field] !== undefined) {
                                 console.log('set extra ', field, o[field]);
                                 self[field] = o[field];
                             }
                         });
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
             var parts = url.split('?');
             StallionApplicationRouter.go({
                 path: parts[0],
                 query: parts[1]
             });
         },
         navigate: function(newOpts) {
             var self = this;
             newOpts = newOpts || {};
             Object.keys(newOpts).forEach(function(name) {
                 self[name] = newOpts[name];
             });
             var url = this.makePageLink(this.page);
             console.log('URL ', url);
             if (url.indexOf('#!') === 0) {
                 url = url.substr(2);
             }
             var parts = url.split('\\?');
             
             StallionApplicationRouter.go({
                 path: parts[0],
                 query: parts[1]
             });
         }, 
         makeUrl: function(page, searchTerm, filters, sortField, sortDirection) {
             var self = this;
             var sort = '';
             if (sortField) {
                 sort = sortField;
                 if (sortDirection.toLowerCase() === 'desc') {
                     sort = '-' + sort;
                 }
             }
             var template = this.browserUrlTemplate;
             if (template.indexOf('?') === -1) {
                 template += '?search={{searchTerm}}&filters={{filters}}&sort={{sort}}&page={{page}}&customFilter={{customFilter}}';
             }
             var url = template;
             var params = {
                 page: page,
                 searchTerm: searchTerm,
                 filters: encodeURIComponent(JSON.stringify(filters)),
                 sort: sort,
                 customFilter: self.customFilter
             };
             console.log("FILTERS ", encodeURIComponent(params.filters));
             Object.keys(params).forEach(function(key) {
                 url = url.replace(new RegExp('{{\\s*' + key + '\\s*}}', 'g'), params[key] || '');
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
                 $(this).attr("data-item-original-width", $(this).width());

             });
             var i = 0;
             oTbl.find('tbody tr:eq(0) td').each(function(){
                 $(this).attr("data-item-original-width",$(this).width());
                 $(this).attr("data-original-width-with-padding",$(this).outerWidth());
                 self.columns[i].$widthWithPadding = $(this).outerWidth();
                 console.log('set width', $(this).width(), self.columns[i].field, self.columns[i].title);
                 i++;                 
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
             i = 0;
             oTbl.find('tbody tr:eq(0) td').each(function(){
                 $(this).width($(this).attr("data-item-original-width"));
                // $(this).width(self.columns[i].$width);
                 //$(this).css({'width': self.columns[i].$width + 'px !important'});
                 i++;
             });        
             $(this.$el).find(".scrollable-wrapper").on('scroll', function() {
                 if(($(this).scrollTop() + $(this).innerHeight() + 20) >= $(this)[0].scrollHeight) {
                     self.appendData();
                 }
             });
         },
         updateFromRoute: function() {
             console.log('update from route');
             this.page = parseInt(this.route.params.page || this.route.query.page || 1, 10) || 1;
             this.searchTerm = this.route.params.search || this.route.query.search || '',
             this.sortField = this.route.params.sort || this.route.query.sort || '';
             if (this.sortField.indexOf('-') === 0) {
                 this.sortDirection = 'desc';
                 this.sortField = this.sortDirection.substr(1);
             } else {
                 this.sortDirection = 'asc';
             }
             this.customFilter = this.route.query.customFilter || '';
             this.filters = [];
             if (this.route.query.filters) { 
                 try {
                     this.filters = JSON.parse(decodeURIComponent(this.route.query.filters));
                 } catch(e) {
                     stallion.showError('Invalid "filters" in query');
                 }
             }
             if (this.route.query.filter_by) {
                 this.filterBys = [this.route.query.filter_by];
             }
             this.refresh();
             window.scrollTo(0, 0);
         }
     },
     watch: {
         route: function(cur, prev) {
             console.log('route changed');
             this.updateFromRoute();
         },
         label: function() {
             this.labelPluralComputed = this.labelPlural || this.label + 's';
             this.titleComputed = this.title || this.toTitleCase(this.labelPluralComputed);
             
         },
         searchTerm: function(cur, prev) {
             if (this.loading) {
                 return;
             }
             if (cur !== prev) {
                 if (cur === '' || cur.length >= 3) {
                     this.page = 1;
                     this.loadInitialData();
                 }
             }
         } 
     },
     events: {
         'cell-value-updated': function(item, field, value, col) {
             item.$isEditing = false;
             if (field) {
                 item[field] = value;
             }
             this.items.$set(item.$index, item);             
             return true;
             
         },
         'clear-filters': function() {
             console.log('clear filters');
             this.clearFiltersAndRefresh();
         }
         
     }

 };
</script>
