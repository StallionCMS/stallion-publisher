<style lang="scss">

</style>

<template>
    <div class="ui-demo-vue">
        <form @submit.prevent="onSave">
            <div class="form-group">
                <label>Title</label>
                <autogrow-textarea v-model="book.title"></autogrow-textarea>
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
                 author: 'Charles Dickens',
                 title: 'A Tale of Two Cities',
                 description: 'One of the finest novels of all time',
                 publishedAt: (new Date().getTime() / 1000) - (2 * 86400),
                 categories: ['science']
         };

         if (localStorage.demoBook) {
             book = JSON.parse(localStorage.demoBook);
         }
         
         return {
             authors: [
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
         'book.categories': 'onSave'
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
                 this.book.publishedAt
             );
             localStorage.demoBook = JSON.stringify(this.book);
             
         }
     }
 }
</script>
