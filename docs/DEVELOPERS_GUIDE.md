# Stallion Publisher Developer's Guide

Right now, the best way to understand Stallion Publisher from a development perspective is to read the code.

### Java Code Structure

The first place to start is with the ["boot" function in the PublisherPlugin class](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/java/io/stallion/publisher/PublisherPlugin.java#L92). This is the main function that runs when the application first loads. Here, the data model controllers are registered, the endpoints are registered, and template tags for the jinja templates are all registered.

Other important classes are:

* [UploadedFileEndpoints](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/java/io/stallion/publisher/content/UploadedFileEndpoints.java) and [UploadRequestProcessor](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/java/io/stallion/publisher/content/UploadRequestProcessor.java) which handle image and other file uploads from users of the content management system.
* [Content](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/java/io/stallion/publisher/content/Content.java) -- the main model for all pages, posts, and other content that users of the system write.
* [ContentEndpoints](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/java/io/stallion/publisher/content/ContentEndpoints.java) -- the endpoints handling the authoring of Content objects
* [ContentVersion](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/java/io/stallion/publisher/content/ContentVersion.java) and [ContentVersionController](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/java/io/stallion/publisher/content/ContentsVersionController.java) -- handles saving past versions of the Content object

The main packages in the Java project are:

* [io.stallion.publisher.content](https://github.com/StallionCMS/stallion-publisher/tree/master/src/main/java/io/stallion/publisher/content) -- contains the code for composing and rendering a single page of content
* [io.stallion.publisher.contacts](https://github.com/StallionCMS/stallion-publisher/tree/master/src/main/java/io/stallion/publisher/contacts) -- handles form submissions, and public visitors subcribing to things, such as comment thread updates
* [io.stallion.publisher.comments](https://github.com/StallionCMS/stallion-publisher/tree/master/src/main/java/io/stallion/publisher/comments) -- handles comment threads on pages and managing of comments.


### Useful Vue Components

The UI uses the Vue Javascript framework exstensively. There are a number of Vue components that you might find useful to use in your app.

#### st-data-table

A fully featured datatable that integrates with the backend Stallion data controllers. Has support for paging, infinite scroll, sorting, searching, editing cells, and rendering custom cells.

[Source code](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/resources/assets/ui-components/st-data-table.vue)

See this [code file here for an example use](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/resources/assets/app-screens/tomes-table.vue).

And here is what the output of the above code looks like:

<img src="https://raw.githubusercontent.com/StallionCMS/stallion-publisher/master/docs/datatable-example.png">


#### modal-base

[Source code](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/resources/assets/ui-components/modal-base.vue)

[See an example here](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/resources/assets/text-editor/insert-link-modal.vue).

And here is what the above example looks like:

<img src="https://raw.githubusercontent.com/StallionCMS/stallion-publisher/master/docs/modal-example.png">

#### autogrow-textarea

[Source](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/resources/assets/form-components/autogrow-textarea.vue)

A text area that gets bigger as you entere new lines.

#### datetime-picker

[Source](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/resources/assets/form-components/datetime-picker.vue)

A picker for choosing a date from a calendar, the time, and time zone.

#### image-picker-field

[Source](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/resources/assets/form-components/image-picker-field.vue)

A picker for choosing an image from the image library, or uploading a new image.

#### image-library

[Source](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/resources/assets/form-components/image-library.vue)

Choose an image or upload new images.

#### select2-field

[Source](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/resources/assets/form-components/select2-field.vue)

Integrates the Javascript/jQuery select2 choice field, which supports autocomplete, multiple select, etc.

#### tinymce-editor

[Source](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/resources/assets/text-editor/tinymce-editor.vue)

A full rich text editor that outpus HTML.

#### markdown-editor

[Source](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/resources/assets/text-editor/markdown-editor.vue)

A full markdown editor with support for inserting links, images, widgets, etc.







