package com.thelion.advertads.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.thelion.advertads.R;
import com.thelion.advertads.interfaces.AdDownloader;
import com.thelion.advertads.interfaces.BannerListener;
import com.thelion.advertads.workers.ImageAdsDownloadHelper;
import com.thelion.advertads.workers.URLResourceHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Banner View is a custom view class that can rotate through advertisements image during a certain time period
 * It can be triggered or dismissed by only one API call.
 * It is lifecycle-aware of its parent, and also is clickable.
 * The width and height can be programmatically configured via parent methods.
 * Position can be configured via parent method
 * This class needs to know at least about a resource origin so it can provide it to the class that will
 * download the ads in setup stage before displaying them.
 * This class still is clickable beyond the area occupied by current ad image displayed.
 *
 * See setupBannerView() to see the methods and their order in MainActivity.kt.
 */
public class BannerView extends AppCompatImageView implements View.OnClickListener, DefaultLifecycleObserver {


    private final long TO_MILLIS = 1000;
    private final long DEFAULT_MILLIS = 1000;

    private BannerListener mBannerListener;
    private AdDownloader mAdDownloader;
    private URLResourceHelper urlResourceHelper;
    private List<Bitmap> adImagesToDisplay = new ArrayList<>();

    // Custom size for bitmap (current ad displayed)
    private int customAdImageSizeWidth = getWidth();
    private int customAdImageSizeHeight = getHeight();


    private long mRefreshTime = 5; // defaults is 5 seconds
    private CountDownTimer refreshTimer;

    //I would use the current image bitmap to try to find its position inside the area of the BannerView
    //because the ad is clickable on all its area, not only currentAdImage is.
    private Bitmap currentAdImage;


    /*
------------------------- Custom View Constructors   -------------------------
    */

    public BannerView(Context context) {
        super(context);


    }

    public BannerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(attrs, 0);
    }

    public BannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs, defStyleAttr);
    }

    /*
------------------------- custom view related functions   -------------------------
    */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }


    /**
     * Make all custom setups for the attr params specific to Banner View custom class
     *
     * @param attrs    xml custom attributes
     * @param defStyle xml custom style
     */
    private void setup(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.BannerView, defStyle, 0);

        if (a.hasValue(R.styleable.BannerView_refreshTime)) {
            mRefreshTime = (long)(a.getFloat(
                    R.styleable.BannerView_refreshTime, mRefreshTime) * TO_MILLIS);
        }

    }


    /*
------------------------- setters related functions   -------------------------
    */
    public void setmBannerListener(BannerListener bannerListener) {
        this.mBannerListener = bannerListener;
    }

    public void setmAdDownloader(AdDownloader adDownloader) {
        this.mAdDownloader = adDownloader;


    }

    public void setUrlResourceHelper(URLResourceHelper urlResourceHelper) {
        this.urlResourceHelper = urlResourceHelper;

        if (this.mAdDownloader != null && (this.mAdDownloader instanceof ImageAdsDownloadHelper)) {
            ((ImageAdsDownloadHelper) this.mAdDownloader).setResourceHelper(urlResourceHelper);
        }
    }

    /**
     * This function allows to reset the size of image that will be present in the ad.
     * The default values are the original size of the image.
     * Comment: if the image is to long or tall it may cause problem (I just want to acknowledge about resizing images)
     * @param sizeX - value of new size of width
     * @param sizeY - value of new size of height
     */
    public void setSizeAdImages (int sizeX, int sizeY) {
        if (sizeX > 0)
            this.customAdImageSizeWidth = sizeX;
        if (sizeY > 0)
            this.customAdImageSizeHeight = sizeY;
    }


    /*
------------------------- underlying AdDownloader related functions   -------------------------
    */

    /**
     * Helper Function to load ad images
     * If this was a bigger project, for instance, from various types of ads,
     * this class would need to have a Strategy pattern instead, for each type of resource
     * (See how setmAdDownloader has "if" and instanceof. That is indicative to abstract into a Strategy),
     */
    private void loadAdImages() {
        adImagesToDisplay = ((ImageAdsDownloadHelper) this.mAdDownloader).getCachedOnDiskImageAds();
    }

    /**
     * Trigger the ads downloading that will be displayed by this view.
     * I made the choice to relate the AdDownloader to the custom view instead of Fragment or Activity,
     * because it is an interface for this library, not in general (i.e., As I am not building an AdDownloader library).
     * I am letting this public because challenge evaluators may want to call it from outside.
     * I could setup everything (meaning all library) very closed,
     * in the sense that banner view would just call this automatically and privately instead:
     * I believe it would be bad practice, as I don't know the complete context of the usage of the library.
     * @param c Context of the application
     */
    public void downloadAds(Context c) {

        this.mAdDownloader.downloadAds(c);
        mBannerListener.onAdsLoaded();
    }

    /**
     * This function was made just to evaluate clear cache from ImageAdsDownloadHelper
     */
    private void clearCache() {
        if (this.mAdDownloader != null && (this.mAdDownloader instanceof ImageAdsDownloadHelper)) {
            ((ImageAdsDownloadHelper) this.mAdDownloader).clearDiskCache();
        }
    }

    /*
------------------------- methods concerned to BannerView itself   -------------------------
    */


    /**
     * The API CALL to trigger the custom view (to show and show ads)
     * It relies on onVisibilityChanged() method to stop or to start displaying the ad images
     */
    public void triggerBannerView() {
        if (this.getVisibility() == View.VISIBLE) {
            this.setVisibility(View.GONE);
            this.mBannerListener.onBannerAdDismissed();

        } else {
            this.setVisibility(View.VISIBLE);
            this.mBannerListener.onBannerAdTriggered();
        }
    }

    /**
     * Makes the setup of variable members not related to the XML files of this custom view
     * I had a problem because the run of a Timer would not be able to modify the view, so I had to get the UIThread,
     * but it had multiple calls, so I had to use CountDownTimer. I know a millisInFuture must be set, so it can be set to:
     * very long time in future, or to the average time a player plays teh game (for that, to use Lytics library, for example)
     * Resource: https://stackoverflow.com/questions/47041396/only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-views
     * Resource 2: https://stackoverflow.com/questions/59225192/timertask-timer-running-multiple-times
     * Resource 3: https://developer.android.com/reference/android/os/CountDownTimer
     */
    private void setupRefreshTimer(long millisInFuture, long countDownInterval) {
        refreshTimer = new CountDownTimer(millisInFuture,this.mRefreshTime) {
            @Override
            public void onTick(long l) {
                displayAdImages();
            }

            @Override
            public void onFinish() {

            }
        };

        if (this.getVisibility() == VISIBLE) {
            refreshTimer.start();
        }

    }

    /**
     * Stop the refresh timer (used for lifecycle)
     */
    private void stopRefreshTimer(){
        if (refreshTimer != null)
            refreshTimer.cancel();

    }

    /**
     * Use this function to load image ads and also scale up them based on scale set
     */
    public void prepareAdImages() {
        this.loadAdImages();
        this.scaleAdImages(this.customAdImageSizeWidth, this.customAdImageSizeHeight);
    }

    /**
     * This function sets up a random image from images available to show as ads
     * I know it is possible to create animations: https://developer.android.com/develop/ui/views/animations/reveal-or-hide-view .
     *
     */
    private void displayAdImages() {
        int imageIndex = new Random().nextInt(adImagesToDisplay.size());
        //Log.d("BannerView","adImagesToDisplay.Size() " + adImagesToDisplay.size());
        currentAdImage = adImagesToDisplay.get(imageIndex);
        this.setImageBitmap(currentAdImage);
        //this.invalidate();

    }

    /**
     * This function is automatically called to change size all images based on the height and width given of the BannerView:
     * This function allows to reset the size of image that will be present in the ad.
     * The default values are the original size of the image.
     * Comment: if the image is to long or tall it may cause problem (I just want to acknowledge about resizing images). 
     * Comment: when layout has wrap_content, width and height will be defaulted to 0, so ternary operator is used.
     * Comment: I am aware of this way of scaling images https://www.informit.com/articles/article.aspx?p=2423187 .
     * @param scaledWidth the width that ad image should scale up
     * @param scaledHeight the height that ad image should scale up
     */
    private void scaleAdImages(int scaledWidth, int scaledHeight) {
        List<Bitmap> scaledAdImages = new ArrayList<>();
        for (Bitmap b : adImagesToDisplay) {
            int ws = scaledWidth > 0 ? scaledWidth : b.getWidth();
            int hs = scaledHeight > 0 ? scaledHeight : b.getHeight();
            b = Bitmap.createScaledBitmap(b,ws,hs,false);
            scaledAdImages.add(b);

        }
        this.adImagesToDisplay = scaledAdImages;
    }

    /*
------------------------- View.onVisibilityChange interface related functions   -------------------------
    */

    @Override
    public void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            setupRefreshTimer(DEFAULT_MILLIS*TO_MILLIS, mRefreshTime * TO_MILLIS);
        } else if (visibility == GONE || visibility == INVISIBLE) {
            stopRefreshTimer();
        }

    }

    /*
------------------------- View.onClick and listeners interface related functions   -------------------------
    */
    @Override
    public void onClick(View view) {
        mBannerListener.onBannerAdClicked();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);

    }


    /*
    ------------------------- lifecycle functions to observe parent lifecycle  -------------------------
    */

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
        Log.d("BannerView", "onResume");
        //Just as example
        setupRefreshTimer(100000,3000);
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onPause(owner);
        stopRefreshTimer();
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onCreate(owner);
        Log.d("BannerView", "onCreate");
        //Just as example
        setupRefreshTimer(100000,3000);
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        Log.d("BannerView", "onStart");

    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStop(owner);
        Log.d("BannerView", "onStop");
        //stopRefreshTimer(); // -> to call stopRefreshTimer here was crashing the app on closing. I could not solve.
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        Log.d("BannerView", "onDestroy");
        stopRefreshTimer();

    }


}
