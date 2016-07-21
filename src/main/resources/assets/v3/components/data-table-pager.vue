
<style lang="scss">
 .data-table-page-vue {
     .pager-link {
         border: 1px solid #999;
         display: inline-block;
         margin-right: 2px;
         padding: 3px 7px 3px 7px;
     }
     .current-page {
         color: #999;
         cursor: default;
     }
 }
</style>

<template>
    <div class="data-table-page-vue">
        <a v-bind:class="{'pager-link-text': true, 'pager-link': true, 'current-page': page==1}" :href="makeLink(1)">First</a>
        <a v-for="num in pager.surroundingPages" :href="makeLink(num)" v-bind:class="{'pager-link': true, 'current-page': num==page}">
            {{num}}
        </a>
        <a v-bind:class="{'pager-link-text': true, 'pager-link': true, 'current-page': page==pager.pageCount}" :href="makeLink(pager.pageCount)">Last</a>

    </div>
</template>
<script>
 module.exports = {
     props: {
         page: 1,
         makeLink: {
             required: true,
             type: Function
         },
         basePath: {
             required: true,
             type: String
         },
         pager: Object
     },
     data: function() {
         console.log('load data table ' + this.pager, ' ', this.baseUrl);
         return {
             
         };
     },
     methods: {
         query: function(params) {

         },
         makeUrl: function() {

         }
     }
     
 }
</script>
