# miniclip_challenge
Private Repository to Write The Process of Creating Two APIs for mini clip challenge 
---
(Disclaimer: This document will probably have lots of versions as it will be built dynamically and accordingly to the necessity to add new formats of the sections along the sections themselves. I will try to maintain the document free of typos, but the my keyboard is broken)

09102022 - A new disclaimer: mid way of this challenge I got Covid. So I lost a couple of days, and that may be seen reflected in my commits. I tried to make it to affect my performance minimally, but I got very tired. In a good side, I was already done with Advertads by time, and the analytics library sounded easier since from the beginning, so I will *probably* be able to finish it in 2 days. 

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

1. For *analytics*:

I thought since from the beginning that this one would be less difficult to implement, and in part I was right. The difficult is to have lots of choices to provide a maintable future for the library. I tried to do that with complexity that may look overwhelming at first, but hopefully helps to maintain the app as long as more events are needed. 

I used Factory and Strategy to build the events. Each event has its own concrete factory with a concrete strategy to create it. Events itself are represented in a interface and concrete class. Since all events are basically built the same, we could have a very simplistic approach not differentiating them aside for the type. I chose to have factories and strategies for each one hoping it come in handy in future (imaginatively speaking). 

We also have two elements that clients need to use: one is domain.Lytic class and the other is domain.EventGenerator class. 
The lytic class will provide handling for the events emited from event generator, being a listener to it, and event generator will be called in appropriate moments of the fragment, activities lifecycle or app logic. After buiding the BannerView, that was pretty straighforward to perceive and conceive.

I tried to reinforce the types of the Events anyway with a typedefenum class that is being used across the library in such a way that if one wants to create an event of certain kind, the string passed must be one of the actual possible ones defined in typedefenum. I also use it inside the factories. It is used across the library, as I said, in various forms. That typedefenum allows user to choose between INIT, SESSION, and MATCH event types.

That all was just the very basic part of the library, the core for the funcionality. The second part is to find a way to denote which parameters are possible for each type of event. 

Lastly, to incorporate listeners for the EventGenerator so it is called in the right time. 

Lots of classes, like event, strategies, factories, have either an abstract class or interface counterpart. That means we want to connect these elements via abstraction, not via hard relationships. If in future somebody comes up with more strategies, factories and etc, it will propably work just fine. 

I also have provided smaller helper classes, like GameInfo, Parameter and ParametersHolder. We want to save the parameters in a parameter holder, instead of using just a List<Parameter> loosely. In GameInfo, the client can setup userid and the time to save the the events on disk. To save the events, I will use the same strategy as in bannerview, for demonstration purposes. I will find the cache storage of the app and save the files there. Before saving them I will transform the event in a string with JSON format. I am not intending to provide any desserization feature as it was not asked. But that can be made using smply Gson or similars.

For the time scheduled to save the events, I will use the same clock I used in bannerView for demonstration purposes. What is important here is to have a parallel thread that can be stopped if the app is finished, very much like we did in the banner view as that custom view was a lifecycle observer. So any type of clock that does that will be fine for us. We want to avoid any type of taxing thing running as zombie even after app is gone.

Lastly, but not least, I could not find a sugar way to setup the elements of the library. One needs to make a function of the kind setup and make the steps to build the lytic and the event generator. Event generator has a builder that can be used. Builder provides 3 main factories for events, and a fourth method to send any string. Since Event Generator checks if the String is part of EventType typedefenums. If it is not, it WILL NOT COMPLAIN, because I put that function for just a remainder that if we instead wanted to provided only one function accepting event type argument, we coud, as event generator checks if the string provided is a event type. Lytic class accepts a GameInfo as argument, so one need to prepare the gameinfo and give to the Lytic. 

The project will have the code all setup to show things running, so one can go and check example of setup withing the MainActivity.kt.

I had no necessity to search for new resources for this library. I could handle it by what I found while making Advertads library.

## General Tasks

Here I will put each of the general tasks to try complete the challenge.

- [x] Check if there are official supported APIs for analytics and ads. (SOLVED: Not gonna use any).
- [x] Check methods to implement function calls outside android main thread (eg asyncTasks (deprecated?), etc)
- [x] Check methods to implement functional calls supporting event-driven design (eg RxJava). 
- [x] Imagine a Domain Model for the application (BannerView and classes to implement helping specialized tasks/concerns)
