## Instructions For Using Lytics library

Lytics library is an analytics library that can save custom events into files inside the device, so later on a person can consult those files.

Lytics is a listener for any kind of event, so you also need to prepare a LyticEventListener and set lytic instance as it.

The only necessary thing to be able to put an event ready for lytics is to cumply Event abstract class. However this library already has a general purpose ConcreteEvent that can be used for any custom event and already provides json serialization to string.

In order to setup lytics:

    //prepare usefu variables in the beginning
    private lateinit var lyticsApp: Lytics
    private lateinit var mGameInfo: GameInfo
    private lateinit var mEventListener: LyticEventListener
    private lateinit var lastInitEvent:Event
    private var startTimeMatch:Long = 0
    private var finishTimeOfTheMatch:Long = 0
    
    // Lion lytics  - demonstration purposes - prepare lytics instance (this is inside onCreate() )
    val MOCK_USER_ID: String = "2022"
    mGameInfo = GameInfo(MOCK_USER_ID, 30) // in seconds
    lyticsApp = setupLytics(mGameInfo, applicationContext)
    lifecycle.addObserver(lyticsApp) // let lytics app to observe lifecycle
    mEventListener = lyticsApp       // make lytics app your app LyticsEventListener

    //Have a setup function
    private fun setupLytics(gameInfo: GameInfo, context: Context): Lytics {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Lytics(gameInfo, context)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }
    
    //setup some events (for example):
     private fun sendInitEvent(): Unit {
        var ie: ConcreteEvent = ConcreteEventBuilder.builder()
            .withName("init")
            .withParameter("os_version", Build.VERSION.SDK_INT)
            .withParameter("advertising_id","00000000-0000-0000-0000-000000000000")
            .withParameter("advertising_tracking_enabled", false)
            .withParameter("device_display_height", Resources.getSystem().getDisplayMetrics().heightPixels) // (it excludes the top bar)
            .withParameter("device_display_width",Resources.getSystem().getDisplayMetrics().widthPixels)
            .build()

        Log.d("MainActivity.kt", ie.asJSONString())
        lastInitEvent = ie //see here how the last init event created is saved to be used later on
        mEventListener.onEvent(ie)
    }
    
    //Use the the events when needed, for example, in onCreate we need a sendInitEvent() for init events.
    
    How and where events are created and used depends on the necessities of the developer. Lytics ust saves in json the events inside files in format <user_id>_<filecreationtimestamp>
