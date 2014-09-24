Grid Image Search - android app
================

Grid Image Search is a basic android app that allows a user to search for images on web using simple filters. The app utilizes [Google Image Search API] (https://developers.google.com/image-search/). Please note that API has been officially deprecated as of May 26, 2011.
The following **required** functionality is completed:
* [x]	User can **search for images** by specifying a query and launching a search. On successful search the grid of images with short titles is displayed. The images are adjusted to fit gridview squares.
* [x]	User can **scroll down to see more images**. The maximum number of images is 64 (limited by API).
* [x]	User can **update search preferences**. Search preferences are set to “any” originally. Once user updates them they are persisted and  **applied to subsequent searches**. The following Search preferences are offered:
  * Size (_icon_, _small_, _medium_, _large_, _extra-large_)
  * Color filter (_black_, _blue_, _brown_, _gray_, _green_ etc... as per Google API)
  * Type (_faces_, _photo_, _clip art_, _line art_))
  * Site (_espn.com_)
* [x]	User can **see a larger image version** by clicking on image in gridview. Image is adjusted to fit the screen width.

The following **optional** features are implemented:
* [x]	ActionBar SearchView for specifying query and triggering search
* [x]	User can **share an image** on detailed image view. Share Action Provider is used.
* [x]	Modal dialog is used for setting search preferences.
* [x]	New search is triggered if search preferences are set and query is not empty.
* [x]	To improve user experience with “full” image loading on Image Details activity, loading spinner and image placeholder if image cannot be loaded are used.
* [x]	Improved error handing with relevant messaging and logging on internet not available and error response from API.
* [x]	Improved look and fill and user experience (hiding soft keyboard on search, populating query on Action Search closed/open, portrait mode for search activity, sitename validation etc)


The app was tested on HTC One (Android 4.1.2) and on AVDs.

Walkthrough of implemented user stories:


![Video Walkthrough](grid_image_search_app_demo.gif)

GIF created with [LiceCap](http://www.cockos.com/licecap/).


The following open-source libraries were used for the project:
-	[Android Asynchronous Http Client] (http://loopj.com/android-async-http/)

-	[Picasso - image downloading and caching library for Android] (http://square.github.io/picasso/)

Points to consider for future development:
-	Improve handling of QPS-rate-exceeded-response from API e.g. wait and re-try or allow user to trigger loading relevant image result page
-	Adjust look of of spinner dropdowns for preferences settings.
-	The app does not do well with very small images e.g. icons (it will enlarge them). It needs to handle icons/small images better while allowing for zoom-in because many of these images are hardly visible on mobile while they are of real size.
-	Experiment with [the StaggeredGridView] (https://github.com/f-barth/AndroidStaggeredGrid)
-	Experiment with [zoom or pan images]  (https://github.com/MikeOrtiz/TouchImageView) displayed in full-screen detail view

