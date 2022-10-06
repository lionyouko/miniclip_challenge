package com.miniclip.mastermind.task

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.miniclip.mastermind.task.elements.Board
import com.miniclip.mastermind.task.types.ClueType
import com.miniclip.mastermind.task.types.GameState
import com.thelion.advertads.interfaces.BannerListener
import com.thelion.advertads.views.BannerView
import com.thelion.advertads.workers.ImageAdsDownloadHelper
import com.thelion.advertads.workers.URLResourceHelper

class MainActivity : AppCompatActivity(), BannerListener {
    private val board: Board = Board()
    private lateinit var table:TableLayout
    private lateinit var btnNewGame:Button
    private lateinit var btnSubmit:Button
    private lateinit var btnClear:Button
    private lateinit var smallLogo:ImageView
    private lateinit var bigLogo:ImageView
    private var firstNewGameClick = true

    // Lion
    private lateinit var bannerView: BannerView

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
        
        bannerView = findViewById(R.id.bannerAd)
        setupBannerView()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle(R.string.exit_prompt_title)
            .setMessage(getString(R.string.exit_prompt_message))
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes) { _, _ ->
                finish()
            }.show()
    }

    private fun onNewButtonClick() {
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
                AlertDialog.Builder(this)
                    .setTitle(R.string.win_msg)
                    .setMessage(getString(R.string.win_popup))
                    .setPositiveButton(R.string.restart_game) { _, _ ->
                        board.resetBoard()
                        board.populateGame(table, applicationContext)
                    }.show()
            }
            GameState.LOST -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.lost_msg)
                    .setMessage(getString(R.string.lose_popup))
                    .setPositiveButton(R.string.restart_game) { _, _ ->
                        board.resetBoard()
                        board.populateGame(table, applicationContext)
                    }.show()
            }
        }
    }

    private fun onClearButtonClick() {
        board.clearCurrentRow()
        board.populateGame(table, applicationContext)

    }

    private fun setupBannerView(){
        bannerView.setmAdDownloader(ImageAdsDownloadHelper.getInstance(getApplicationContext()))
        bannerView.setUrlResourceHelper(URLResourceHelper())
        bannerView.setmBannerListener(this);
        lifecycle.addObserver(bannerView)
        bannerView.downloadAds(this.applicationContext)
        bannerView.setSizeAdImages(300,100);
        bannerView.prepareAdImages()
        
    }

    // Lion
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