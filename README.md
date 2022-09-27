# miniclip_challenge
Private Repository to Write The Process of Creating Two APIs for mini clip challenge 
---
(Disclaimer: This document will probably have lots of versions as it will be built dynamically and accordingly to the necessity to add new formats of the sections along the sections themselves. I will try to maintain the document free of typos, but the my keyboard is broken)

Initial proposed sections:

1. Reasoning about the challenge (include resources consulted in order to come up with an idea to solve)
2. Step-by-step guide on how it was solved (or tried to be solved). 
3. General Tasks

More notation in-text may happen, as for example, to denote something that was difficult to solve. Instead of separating it into sections, it may be incorporated directly into the text itself, maintaining the flow of it.


## Reasoning About The Challenge

It has been asked to create two APIs, one for *advertisements* and one for *analytics*.
Each one looks like to use noticiably different concepts:

1. For *advertisements*: 
   - We will have to *download* content dynamically (i.e. at run time) and fill a size configurable banner ad with that content in a configurable amount of time, by the which, in the end of it another banner comes into place. So this tasks is oriented to *download* things on *background* while application is running without to disturb its performance or make it noticiable to the user (gamer, in this case). 
   - For the purpose of the specific app provided, the banner must be presented in a part of the screen that does not disturb the game session. Although some banners can be made visible and then to occupy part of the screen dinamically (lik inflating from the bottom), it sounds better to have it already at the start of the game session in a fixed place and already visible from the start given that one of the challenge constraints is to have it always-visible anyway.
   - The app will need to be notified about any change coming from API (in particular showing and hiding banners). 

2. For *analytics*:
   - This library is guided around Observer design pattern and android Activities lifecycle. Activities have 6 states and the transition to each state has a callback function (that can bee overriden) to have things done when eeenterring to a given state (the most common is onCreate(), for example. Our API must work on certain state transitions of the app, depending on the event, but also depending on the app inner state (as it is a game, if the game is over, for example).
   
   
The first thing to look for is Google recommended libraries for such two APIs. So one way to solve the challenge is to use already well-known APIs if they are available. When one does that, it is bringing the complexity that the third-party API can have, meaning additional overhead. The benefits must balance depending on the size of the project and the time to learn and to incorporate the API. Sounds like cheating? Depends: if one finds a well-stabilished API with great features, one can use it. If it is open-source, one can use the time that was spared into creating an API from zero and, istead, to help the well-known one to evolve either for the community or for your own necessities (each one of these choices have goods and bads). 

The second thing to look for is the building blocks for a custom API for each one of the libraries asked. For example: which method to run a task on a separated thread will be the best in order to download the banners without to disturb th app performance?

After I choose the path I will try to follow, I will break down the steps of each one library and checkmark each one completed with a time stamp of it. It is a two week sprint, for two libraries. 

## Step-by-step Guide

## General Tasks

Here I will put each of the general tasks to try complete the challenge.

- [ ] Check if there are official supported APIs for analytics and ads.
- [ ] Check methods to implement function calls outside android main thread (eg asyncTasks (deprecated?), etc)
- [ ] Check methods to implement functional calls supporting event-driven design (eg RxJava). 
