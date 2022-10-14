# miniclip_challenge
Private Repository to Write The Process of Creating Two APIs for mini clip challenge 
---
(Disclaimer: This document will probably have lots of versions as it will be built dynamically and accordingly to the necessity to add new formats of the sections along the sections themselves. I will try to maintain the document free of typos, but the my keyboard is broken)

~~(The project has some bug related to the .png files. So when I tried to produce a release version of the apk, gradle was giving an error of the type "/ic_launcher.png: AAPT: error: file failed to compile.". So I am letting the debug version, and here is some tracking of the issue (it is not new, but still happening)~~: 

- https://stackoverflow.com/questions/65574926/ic-launcher-png-aapt-error-file-not-found-in-android-studio-v4-1-1
- https://issuetracker.google.com/issues/172048751

I had to solve a bug where the png files available in the miniclip challenge project were giving an AAPT error. I found a soluton here: https://stackoverflow.com/questions/46177560/failed-to-read-png-signature-file-does-not-start-with-png-signature

What I had to do was to rename some of the png files to jpg, because they were in fact, jpg. So I was able to get aar files.

09102022 
- A new disclaimer: mid way of this challenge I got Covid. So I lost a couple of days, and that may be seen reflected in my commits. I tried to make it to affect my performance minimally, but I got very tired. In a good side, I was already done with Advertads by time, and the analytics library sounded easier since from the beginning, so I will *probably* be able to finish it in 2 days. 
- It is very important to notice that the code will have comments that aren't the type of thing that goes on javadocs. I had to have those comments, aside from, for example, the "pure" class description to assure I was explaining the process, and trying to facilitate the understanding about certain decisions.

Initial proposed sections:

1. Reasoning about the challenge (include resources consulted in order to come up with an idea to solve)
2. Step-by-step guide on how it was solved (or tried to be solved). 
3. General Tasks

More notation in-text may happen, as for example, to denote something that was difficult to solve. Instead of separating it into sections, it may be incorporated directly into the text itself, maintaining the flow of it.


## Reasoning About The Challenge

It has been asked to create two APIs, one for *advertisements* and one for *analytics*.
Each one looks like to use noticiably different concepts:

1. For *advertisements*: 
   - We will have to *download* content at run time and fill a size configurable banner ad with that content in a configurable amount of time, by the which, in the end of it, another banner comes into place. So this tasks is oriented to *download* things on *background* while application is running without to disturb its performance or make it noticiable to the user displaying it (gamer, in this case). 
   - For the purpose of the specific app provided, the banner must be presented in a part of the screen that does not disturb the game session. Although some banners can be made visible and then to occupy part of the screen dinamically (like poping up from the bottom), it sounds better to have it already at the start of the game session in a fixed place and already visible from the start. An always-visible banner is one of the challenge constraints anyway.
   - The app will need to be notified about any change coming from API (in particular showing and hiding banners). 

2. For *analytics*:
   - This library is guided around Observer design pattern and android Activities lifecycle. Activities have 6 states and the transition to each state has a callback function (that can bee overriden) to have things done when entering to a given state (the most common is onCreate(), for example). Our API must work on certain state transitions of the app, depending on the event, but also depending on the app inner state (as it is a game, if the game is over, for example).
   
   
The first thing to look for is Google recommended libraries for such two APIs. So one way to solve the challenge is to use already well-known APIs if they are available. When one does that, it is bringing the complexity that the third-party API can have, meaning additional overhead. The benefits must balance depending on the size of the project and the time to learn and to incorporate the API. Sounds like cheating? Depends: if one finds a well-stabilished API with great features, one can use it. If it is open-source, one can use the time that was spared into creating an API from zero and, istead, to help the well-known one to evolve either for the community or for your own necessities (each one of these choices have goods and bads). 

The second thing to look for is the building blocks for a custom API for each one of the libraries asked. For example: which metodology to run a task on a separated thread will be the best in order to download the banners without to disturb th app performance?

After I choose the path I will try to follow, I will break down the steps of each one library and checkmark each one completed with a time stamp of it. It is a two week sprint, for two libraries. 

27092022

I have found how to built android libraries. 

These are some tutorials explaining how to do it in general:
- Google: https://developer.android.com/studio/projects/android-library
- As an Addon: https://medium.com/profusion-engineering/how-to-build-and-use-an-android-sdk-add-on-8b86573f8c61
- https://guides.codepath.com/android/Building-your-own-Android-library
- https://blog.instabug.com/create-and-distribute-android-library/
- https://www.raywenderlich.com/52-building-an-android-library-tutorial

I am used to build APIs for Cloud services (like a Flask application). For Java, exposing with interfaces. 
The challenge is challenging as I am used to built android applications instead (client, in other words). I was never promped to offer a library for another android application, so I had to research how it is done with android studio tools.



## Step-by-step Guide

I will reinforce this aspect: since I am entry-level, I had to research a lot before starting to code. And what it may come from that research is just a small version of the complexity it holds for a starter. 

What I want to also achieve is to show that I am aware of meaningful aspects of such libraries, namely: being aware of lifecycles, using extra-permissions (only if necessary), avoiding to run long tasks on main thread, storing (caching) information if possible, among others. 


1. For *advertisements*: 
   - The final purpose of the research was to evalute the main (helper) components necessary to make a custom view that can load ads and present itself in a customizable way also being somewhat open to be observed about certain events of itself. So it has to be some sort of lifecycle of its own that listeners can get aware of. The helper components may be in form of interfaces or abstract classes, some may be concrete classes specialized in something (as to download the banners), in a SOLID fashion. 
   - The library will be called advertads. In order to build it, first we will build a custom view. That view will have listener to respond to interaction with banner ad (even if the interaction is with the app it is in, like exemplified in challenge text).
     - Resources: 
       - To remember how to make a custom view: https://codelabs.developers.google.com/codelabs/advanced-android-training-customize-view/index.html?index=..%2F..advanced-android-training#0
       - We may explore a bit adviews from google (the source code was not found, so I searched a similar one): https://github.com/appnexus/mobile-sdk-android/blob/master/sdk/src/com/appnexus/opensdk/AdView.java
       - Read about custom views lifecycles (also part 1 and 2 of the same text): https://proandroiddev.com/android-custom-view-level-3-81e767c8cc75
       - We could evaluate the general design of an advertisement library by inspcting a similar one (meaning the Domain Model of it):  https://github.com/appnexus/mobile-sdk-android/tree/master/sdk/src/com/appnexus/opensdk
       - We care about the resource usage of our application (from network to storage, but also in-memory usage): https://github.com/googleads/googleads-mobile-android-examples/blob/main/java/admob/BannerExample/app/src/main/java/com/google/android/gms/example/bannerexample/MyActivity.java
       - We want to save a small cache and only go to network if the resources to download aren't saved already (the following resource was more to remember File API): https://github.com/loopj/android-smart-image-view/blob/master/src/com/loopj/android/image/WebImageCache.java
       - We want to connect to external data resources via network, in a asynchronous thread, that may or not find resources: https://loopj.com/android-async-http/

Challenge description did not say certain aspects of the advertisement library. But since I already studied mobile development in masters, I was aware that, mobile devices have limited resources. Applications must consider that, thus having smallest footprint possible. That is a general concept, actually, but for mobile development, it is a bigger deal because the user's reaction to a resource-hungry application is to notice its phone getting hotter and slower, its battery dying faster, then making the user to delete the application. 

The steps taken to develop this library were:

1. Produce a small (mental) domain model for the library after reading documentation and resources mentioned above)
2. To produce helper specialized objects (classes) that can solve tasks for the "main" object, our BannerView.
3. To make BannerView aware about app lifecycle, so it doesnt not consume app memory or cpu for nothing.
4. To make android components (like fragments or activities) aware of bannr view changes (for example, dismissed)

I will write more about the code, but maybe the library's code speak for itself. For example:

- we have a ImageAdsDownloadHelper that implements the interface AdDownloader. For this download helper, the ad resources are images. It uses a URLResourcHelper to fetch the resource urls and then use them to download the images. It is a very gentle but useful reminder that I am aware of android Jetpack Rooms library (where we have a repository class helping to connect to resources in MVVM fashion).
- The ImageAdsDownloadHelper does 2 types of work (it, thus, could have been broken down it two classes): to store the resources downloaded, and to download them. This class will recover cached resources in the constructor, and will avoid to download the resources by checking the stored name of the files, compared with the url of the about-to-be-download resources. It will skip if the cached file names are the url of the resource desired (this is to show that a developer must find strategies avoid network usage if not stricly necessary. Memory is cheap, Time and mobile data not that much).
- The library needs to require (or even to inherit) permission to use network and to read and write on external storage. I required it by android manifeest declaration. 

---
2. For *analytics*:

(Update 141022 : Please, compare with the previous commit to see the text before major change in reasoning for analytics library)

I had to finish the challenge, and head got very scrambled by covid. 

For analytics library, I had to change the entire way that I was thinking into make it. Before I had stipulated factories and strategies to create events, and would enforce a client to do so if it wanted to have new customm events. That was a major burden. Instead of that, I simplified and tried to really offer a more simple and closed library.

Now what one needs to do is to create its events the way it needs and just to send to Lytics instance. Lytics as default implementation for LyticsEventListener in which it handles the events that comes and will save them properly via a Storer class. Properly means withing a specified time frequency or when the other constraints are applicable (like when app goes to the background, for example).

So, in the end, instead of proving the events described in the challenge, I simply provided the platform, and in Mastermind I implemented some of the Events and their logic to trigger just to show the library working. 

If one wants to send events to the Lytics, one can setup it as LyticEventListener, and then send events to it as pleased in any moment of the game or appication lifecycle.

I recognise that certain functions related to lifecycle my be triggered incorrectly, for example, Lytics instance is a DefaultLyfecycleObserver, so if a rotation happens, it may store the events.

Another drawback is that, while the same lytics instance can be linked to many components, and thus receiving events from many components (one just need to send them to lytics instance), such instance would be better if associated with a more central element  that could have access to many components and access to the app lifecycle instead. Anyway, it is possible to set lytics as observer of many lifecycles, so one client just needs to share lytics instance in components that may need it. 

For this library, I had to enforce certain versions of SDK (ex. api = Build.VERSION_CODES.O). Although we want to make the dependency of such things the smaller possible, for this ocasion it will not impose any major burden in use by as the versions needed are old. The times this happened was to use, for example, Instance.now().getEpochSecond(), a function to provide an easy time stamp.

I had the idea to provide an event generator, or to enforce which custom events could be created as client would first to register the event name before using it. I abandoned all of this. I have no control over which kind of events are needed and when they must be triggered, so I focused all the effort in Lytics class and its ability to save the events into files. From the commit before to this one, many classes are deleted from the project because of that.


Lytics library comes with some of the following classes:

domain.Lytics - this class has a handler for events that arrives, and this handler sends the events to a storage that will save them into a file. Before having the major change, I would provide ways to let user to have lots of handlers and storages (storers). I cut all of that and focused into finishing the saving ability of the library. Lytics is an LyticEventListener, so any class that wants to have it as listener can simply use it and it will take care of events.

events.Event - this is an abstract class with the basics of what the event should look like. Any event extending this class will be accepted by lytics library.

events.ConcreteEvent - this is a concrete class if one want to use a out-of-the-box general purpose event. It implements the a serialization in json string format for itself. This function is called as no library for serialization was used (by this time). One could use Gson, instead. I opted to simply serialize it in a small function. 

helpers.Parameter and ParameterHolder - these classes are used in Event. Event has a parameter holder that holds many parameters. parameter class is composed by a String key and by a T value of any kind. When serializing into json string, I simply check if T type is String, and if so, it is saved with single quotes around it, otherwise it is save as is, then mimicking json format correctly.

helpers.GameInfo - it as small class to save user_id and time-frequency. Must be provided to new lytics app instance.

builders.ConcreteEventBuilder - One of the uses of the concrete event is to have the concrete event builder. One can add as many paraameters as needed before building aa concrete event. Just make sure to provide a name also, as all events must need a name (like init, session, match).

helpers.FileHelper - this class has many utilities, for example, to extract the timestamp from a standard file name of the type <user_id>_<creationtimestamp>, or creating a standard filename of that same format.

---
The project will have the code all setup to show things running, so one can go and check example of setup in the MainActivity.kt.

I had no necessity to search for new resources for this library. I could handle it by what I found while making Advertads library.

---
   
In order to show lytics working, I implemented init, session and match events, as asked by the challenge. 

I could not, however, find certaain attributes to be used, so I mock them. For instance, there is no advertising_tracking_enabled or advertising_id. This should come from an external library, and since advertads is not linked to lytics, lytics could not assume advertads could have such variables, for example. And I have no such attributes on bannerview as I implemented a view able to show ads, but image ads can come from different sources. 
   

While for the first library, I tried to implement something that is open to change, in the second, I focused into deliver small applicaton able to solve the challenge. It was a bit difficult, as I may have mentioned before, to set which part I should code solely thinking about somebody using the library out of the box, and which part I would code having in mind a fellow developer reading my code and thinking about extending it. For the advertads library, I had more time and I was feeling better (no covid), so I started with the idea of letting info on how to extend it. For the second, I just tried to solve it. Also info that is pertained to the challenge is put over the code, so, not necessarily, the javadoc of a function or class would be exactly like it is written in a non-challenge situation. I hope to have opportunity to explict what is what during the interview if any is not clear.
   
   
## General Tasks

Here I will put each of the general tasks to try complete the challenge.

- [x] Check if there are official supported APIs for analytics and ads. (SOLVED: Not gonna use any).
- [x] Check methods to implement function calls outside android main thread (eg asyncTasks (deprecated?), etc)
- [x] Check methods to implement functional calls supporting event-driven design (eg RxJava). 
- [x] Imagine a Domain Model for the application (BannerView and classes to implement helping specialized tasks/concerns)
   
## How to install Advertads

Both libraries are independent of each other and agnostic to the surrounding app. To install, one must add the library as regularly would do via android studio. As the library is not hosted in any public repository (like mavenCentral(), for instance), it must be added by hand after downloaded.

As an AAR is put available, one can follow this tutorial to use it (it is just the regular "adding a library" procedure): https://developer.android.com/studio/projects/android-library
   
## How to install Lytics

Both libraries are independent of each other and agnostic to the surrounding app. To install, one must add the library as regularly would do via android studio. As the library is not hosted in any public repository (like mavenCentral(), for instance), it must be added by hand after downloaded.

As an AAR is put available, one can follow this tutorial to use it (it is just the regular "adding a library" procedure): https://developer.android.com/studio/projects/android-library
