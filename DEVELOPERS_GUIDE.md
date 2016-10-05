# Stallion Publisher Developer's Guide

Right now, the best way to understand Stallion Publisher from a development perspective is to read the code.

### Java Code Structure

The first place to start is with the ["boot" function in the PublisherPlugin class](https://github.com/StallionCMS/stallion-publisher/blob/master/src/main/java/io/stallion/publisher/PublisherPlugin.java#L92). This is the main function that runs when the application first loads. Here, the data model controllers are registered, the endpoints are registered, and template tags for the jinja templates are all registered.

Other important classes are:

* UploadedFileEndpoints and UploadRequestProcessor which handle image and other file uploads from users of the content management system.
* Content -- the main model for all pages, posts, and other content that users of the system write.
* ContentEndpoints -- the endpoints handling the authoring of Content objects
* ContentVersion and ContentVersionController -- handles saving past versions of the Content object

The main packages in the Java project are:

* io.stallion.publisher.content -- contains the code for composing and rendering a single page of content
* io.stallion.publisher.contacts -- handles form submissions, and public visitors subcribing to things, such as comment thread updates
* io.stallion.publisher.comments -- handles comment threads on pages and managing of comments.


### Important Vue Components

The UI uses the Vue Javascript framework exstensively. There are a number of Vue components that you might find useful to use in your app.

#### st-data-table

Example usage:


Example UI:



#### modal-base

Example usage:


Example result:



#### autogrow-textarea

#### datetime-picker

#### image-picker-field

#### image-library


#### select2-field



#### tinymce-editor


#### markdown-editor






