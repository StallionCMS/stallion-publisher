<style lang="scss">
 .moving-field-label {
     position:absolute;
     
 }
 .moving-field {
     border: 1px solid transparent;
     outline: none;
     -moz-box-shadow:    0px 0px 0px 0px #FFF;
     -webkit-box-shadow: 0px 0px 0px 0px #FFF;
     box-shadow:         0px 0px 0px 0px #FFF;
     padding: 0px 0px 0px 0px;
     margin-top: 2em;
 }
 .moving-field:active, .moving-field:focus {
     border: 1px solid transparent;
     outline: none;
     -moz-box-shadow:    0px 0px 0px 0px #FFF;
     -webkit-box-shadow: 0px 0px 0px 0px #FFF;
     box-shadow:         0px 0px 0px 0px #FFF;
 }
 .moving-field.active-field {

 }
 .moving-field.nonempty, .moving-field.focused, .moving-field:active, .moving-field:focus {
     padding-top: 2em;
     margin-top: 0em;
 }
 .moving-field.empty, .moving-field {

 } 
</style>

<template>
    <div class="ui-demo-vue">
        <form @submit.prevent="onSave">
            <div class="form-group">
                <autogrow-text v-field-label="'Title'"  v-model="book.title"></autogrow-text>
            </div>
            <div class="form-group">
                <label>Description</label>
                <autogrow-textarea v-model="book.description"></autogrow-textarea>
            </div>
            <div class="form-group">
                <label>Author</label>
                <div>
                    <select2-field v-model="book.author" :choices="authors" :config="{width: 500}">
                        <option value="Charles Dickens">Charles Dickens</option>
                        <option value="Edward Gibbon">Edward Gibbon</option>
                        <option value="Tom Clancy">Tom Clancy</option>
                    </select2-field>
                    <button type="input"  @click="book.author='Tom Clancy'">Set to Tom</button>
                </div>
            </div>
            <div class="form-group">
                <label>Published At</label>
                <div>
                    <date-picker v-model="book.publishedAt"></date-picker>
                </div>
            </div>
            <div class="form-group">
                <label>Expires At</label>
                <div>
                    <datetime-picker v-model="book.expiresAt"></datetime-picker>
                </div>
            </div>
            <div class="form-group">
                <label>Categories</label>
                <div>
                    <select2-field v-model="book.categories" :choices="categories"  :config="{width: 500}" :multiple="true">
                        <option value="history">History</option>
                        <option value="geography">Geography</option>
                        <option value="science">Science</option>
                        <option value="fiction">Fiction</option>
                    </select2-field>
                    <button type="input" @click="book.categories=['science', 'fiction']">Set to Science & Fiction</button>
                </div>
            </div>
            <div class="form-group">
                <button type="submit" class="btn btn-primary btn-xl">Save</button>
            </div>
        </form>
    </div>
</template>

<script>
 module.exports = {
     data: function() {
         var book = {
             author: 'Edward Gibbon',
             title: 'A Tale of Two Cities',
             description: 'One of the finest novels of all time',
             publishedAt: (new Date().getTime() / 1000) - (2 * 86400),
             expiresAt: (new Date().getTime() / 1000) + (2 * 86400),
             categories: ['science']
         };

         if (localStorage.demoBook) {
             book = JSON.parse(localStorage.demoBook);
         }
         if (!book.publishedAt) {
             book.publishedAt = (new Date().getTime() / 1000) - (2 * 86400);
         }
         if (!book.expiresAt) {
             book.expiresAt = (new Date().getTime() / 1000) - (2 * 86400);
         }
         console.log('initial book categories ', book.categories);
         
         return {
             authors: [
                 '',
                 'Charles Dickens',
                 'Edward Gibbon',
                 'Tom Clancy'
             ],
             categories: [
                 'history',
                 'geography',
                 'science',
                 'fiction'
             ],
             book: book
         };
     },
     watch: {
         '$route': 'loadBook',
         'book.categories': 'onSave',
         'book.publishedAt': function(newVal) {
             console.log('new published at ', newVal);
         },
         'book.expiresAt': function(newVal) {
             console.log('new expired at ', newVal);
         }         
     },
     methods: {
         loadBook: function() {
             if (localStorage.demoBook) {
                 this.book = JSON.parse(localStorage.demoBook);
             }
         },
         onSave: function() {
             console.log(
                 'book ',
                 this.book,
                 this.book.title,
                 this.book.categories,
                 this.book.author,
                 this.book.description,
                 this.book.publishedAt,
                 this.book.expiresAt
             );
             localStorage.demoBook = JSON.stringify(this.book);
             
         }
     }
 }
</script>
