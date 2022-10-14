package com.miniclip.mastermind.task

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.miniclip.mastermind.task.elements.Board
import com.miniclip.mastermind.task.types.ClueType
import com.miniclip.mastermind.task.types.GameState
import com.thelion.advertads.interfaces.BannerListener
import com.thelion.advertads.views.BannerView
import com.thelion.advertads.workers.ImageAdsDownloadHelper
import com.thelion.advertads.workers.URLResourceHelper
import com.thelion.lytics.builders.ConcreteEventBuilder
import com.thelion.lytics.domain.Lytics
import com.thelion.lytics.events.ConcreteEvent
import com.thelion.lytics.events.Event
import com.thelion.lytics.helpers.GameInfo
import com.thelion.lytics.interfaces.LyticEventListener
import java.time.Instant

class MainActivity : AppCompatActivity(), BannerListener {
    private val board: Board = Board()
    private lateinit var table:TableLayout
    private lateinit var btnNewGame:Button
    private lateinit var btnSubmit:Button
    private lateinit var btnClear:Button
    private lateinit var smallLogo:ImageView
    private lateinit var bigLogo:ImageView
    private var firstNewGameClick = true

    // Lion advertads
    private lateinit var bannerView: BannerView


    // Lion lytics
    private lateinit var lyticsApp: Lytics
    private lateinit var mGameInfo: GameInfo
    private lateinit var mEventListener: LyticEventListener
    private lateinit var lastInitEvent:Event
    private var startTimeMatch:Long = 0
    private var finishTimeOfTheMatch:Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        table = findViewById(R.id.gameTable)
        smallLogo = findViewById(R.id.logo_small)
        bigLogo = findViewById(R.id.logo)

        btnNewGame = findViewById(R.id.buttonNewGame)
        btnNewGame.setOnClickListener {
            onNewButtonClick()
        }

        btnSubmit = findViewById(R.id.buttonSubmit)
        btnSubmit.setOnClickListener {
            onSubmitButtonClick()
        }

        btnClear = findViewById(R.id.buttonClear)
        btnClear.setOnClickListener {
            onClearButtonClick()
        }

        // Lion advertads
        bannerView = findViewById(R.id.bannerAd)
        setupBannerView()

        // Lion lytics  - demonstration purposes
        val MOCK_USER_ID: String = "2022"
        mGameInfo = GameInfo(MOCK_USER_ID, 30) // in seconds
        lyticsApp = setupLytics(mGameInfo, applicationContext)
        lifecycle.addObserver(lyticsApp) // let lytics app to observe lifecycle
        mEventListener = lyticsApp       // make lytics app your app LyticsEventListener

        sendInitEvent()

    }


    override fun onResume() {
        super.onResume()
        // when app goes back from the background
        sendInitEvent()
    }

    override fun onStop() {
        super.onStop()
        sendSessionEvent()
    }

    override fun onDestroy() {
        // when app is closed
        super.onDestroy()
        sendSessionEvent()
        endOfMatchTime()
        sendMatchEvent(board.state, true)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle(R.string.exit_prompt_title)
            .setMessage(getString(R.string.exit_prompt_message))
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes) { _, _ ->
                finish()
                endOfMatchTime()
                sendMatchEvent(board.state, true)

            }.show()
    }

    private fun onNewButtonClick() {

        startOfMatchTime() // start counting match time

        if (firstNewGameClick) {
            AlertDialog.Builder(this)
                .setTitle(R.string.how_to_play_title)
                .setMessage(getString(R.string.how_to_play_message))
                .setPositiveButton(R.string.ok) { _, _ ->
                    btnNewGame.text = getString(R.string.restart_game)

                    btnSubmit.visibility = View.VISIBLE
                    btnClear.visibility = View.VISIBLE
                    bigLogo.visibility = View.INVISIBLE
                    smallLogo.visibility = View.VISIBLE

                    firstNewGameClick = false

                    // Lion demonstration purposes
                    bannerView.visibility = View.VISIBLE

                    board.populateGame(table, applicationContext)


                }.show()
        } else {
            AlertDialog.Builder(this)
                .setTitle(R.string.restart_game)
                .setMessage(getString(R.string.restart_message))
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes) { _, _ ->
                    board.resetBoard()
                    board.populateGame(table, applicationContext)

                    //startOfMatchTime() // start counting match time

                }.show()
        }
    }

    private fun onSubmitButtonClick() {
        when(board.state) {
            GameState.PLAYING -> {
                if (board.checkRow()) {
                    Toast.makeText(applicationContext, board.getClueAmountForRowAndType(board.currentRow, ClueType.LOCATION).toString()
                    + " location(s), " + board.getClueAmountForRowAndType(board.currentRow, ClueType.COLOR).toString() + " color(s)",
                    Toast.LENGTH_SHORT).show()
                    board.nextRow()
                } else {
                    Toast.makeText(applicationContext, R.string.incomplete_row, Toast.LENGTH_SHORT).show()
                }

                board.populateGame(table, applicationContext)
            }
            GameState.WON -> {
                endOfMatchTime()
                sendMatchEvent(board.state, false)

                AlertDialog.Builder(this)
                    .setTitle(R.string.win_msg)
                    .setMessage(getString(R.string.win_popup))
                    .setPositiveButton(R.string.restart_game) { _, _ ->
                        board.resetBoard()
                        board.populateGame(table, applicationContext)

                        startOfMatchTime() // start counting match time
                    }.show()
            }
            GameState.LOST -> {
                endOfMatchTime()
                sendMatchEvent(board.state, false)

                AlertDialog.Builder(this)
                    .setTitle(R.string.lost_msg)
                    .setMessage(getString(R.string.lose_popup))
                    .setPositiveButton(R.string.restart_game) { _, _ ->
                        board.resetBoard()
                        board.populateGame(table, applicationContext)

                        startOfMatchTime() // start counting match time
                    }.show()
            }
        }
    }

    private fun onClearButtonClick() {
        board.clearCurrentRow()
        board.populateGame(table, applicationContext)

        bannerView.triggerBannerView() // lion - one call to trigger or to dismiss (I put it here to demonstrate)

    }


    /*
    ------ all functions bellow are either to show the setup of the library
            or helper functions to mimic certain aspects of the game ----------

    ------ The helper part is really necessary for analytics,
            as it depends of each game implementation
            and how client will use provide events to it.
            I created the events that way for demonstration purpose  --------
     */

    // Lion advertads
    /**
     *  helper function settint up the banner view with the options it can have (for instance, setSizeAdImages)
     */
    private fun setupBannerView(){
        bannerView.setmAdDownloader(ImageAdsDownloadHelper.getInstance(getApplicationContext()))
        bannerView.setUrlResourceHelper(URLResourceHelper())
        bannerView.setmBannerListener(this);
        lifecycle.addObserver(bannerView)
        bannerView.downloadAds(this.applicationContext)
        bannerView.setSizeAdImages(300,100)
        bannerView.prepareAdImages()
        // Here I want to show the add just when the person clicks to start a game and them keep it there
        // If I let it there, it starts with activity taking the portion designed to it
        // So it complies what is asked, but for demonstration, I want to start it only when player press play.
        bannerView.visibility = View.GONE
        bannerView.setOnClickListener(bannerView)
    }

    /**
     * Helper function to set Lytics app (it needs an sdk_int version bigger than O (here) just because I used Instant.now()
     * So in the end, if such thing started to become a burden, I could try to find another way to implement the time stamping,
     * for example
     */
    // Lion - lytics - demonstration purposes
    private fun setupLytics(gameInfo: GameInfo, context: Context): Lytics {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Lytics(gameInfo, context)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    /**
     * The sendInitEvent must be called when application first runs or goes back from background
     * When it first runs, it needs to be triggered from launcher activity
     * When it comes back, must be triggered via lifecycle callback
     */
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

    /**
     * The sendMatchEvent must be called when the user finishes a mastermind match or closes the app during the game or goes to background
     * When it finishes a match, it can be triggered from most convenient object to do so
     * When it closes the app during the game or goes to background, must be launched via lifecycle callback
     */
    private fun sendMatchEvent(status:GameState, onAppClosing:Boolean): Unit {

        Log.d("Lytics", "match event " + this.startTimeMatch)
        // simple way to check which is the condition of the game
        // it should be implemented in a better way, it is to show events working
        var result: String = if (onAppClosing && (status == GameState.PLAYING)) {
           "Left"
        } else {
            when (status) {
                GameState.WON -> "Victory"
                GameState.LOST -> "Defeat"
                GameState.PLAYING -> "Pause"
            }
        }

        var me: ConcreteEvent = ConcreteEventBuilder.builder()
            .withName("match")
            .withParameter("duration", this.finishTimeOfTheMatch)
            .withParameter("result",result)
            .build()

        mEventListener.onEvent(me)
    }

    /**
     * The sendSessionEvent must be called when the application is closed or when the application goes to background
     * When it is closed, it needs to be triggered via lifecycle callback
     * When it goes to background, must be launched via lifecycle callback
     */
    private fun sendSessionEvent(): Unit {
        var se: ConcreteEvent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ConcreteEventBuilder.builder()
                .withName("session")
                .withParameter("duration", Instant.now().getEpochSecond() - lastInitEvent.timeStamp)
                .build()

        } else {
            TODO("VERSION.SDK_INT < O")
        }

        mEventListener.onEvent(se)
    }


    /**
     * helper function to denote the timestamp of the beginning of a match
     */
    private fun startOfMatchTime() {
        startTimeMatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant.now().epochSecond
            //I wll use this just to provide some match duration. It could be anything marking time.
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    /**
     * helper function to denote the timestamp of the end of a match
     */
    private fun endOfMatchTime(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            finishTimeOfTheMatch = Instant.now().epochSecond - startTimeMatch
        }
    }

    /*
------------------------- BannerListener interface  (see the interface) -------------------------
    */

    // Lion - BannerListener - demonstration purposes
    override fun onBannerAdClicked() {
        Toast.makeText(applicationContext, "Banner clicked",
            Toast.LENGTH_SHORT).show()
    }

    override fun onAdsLoaded() {
        Toast.makeText(applicationContext, "Ads Loaded",
            Toast.LENGTH_SHORT).show()
    }

    override fun onBannerAdTriggered() {
        Toast.makeText(applicationContext, "Banner Triggered",
            Toast.LENGTH_SHORT).show()
    }

    override fun onBannerAdDismissed() {
        Toast.makeText(applicationContext, "Banner Dismissed",
            Toast.LENGTH_SHORT).show()
    }
}