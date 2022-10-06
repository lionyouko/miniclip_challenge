package com.thelion.advertads.interfaces;

/**
 * Interface for Activities or Fragments to be notified when Banner View actions happens
 * Resource (to remember): https://guides.codepath.com/android/Creating-Custom-Listeners
 */
public interface BannerListener {
    void onBannerAdClicked();   //triggered when banner view is clicked
    void onAdsLoaded();         //triggered when downloadAds() is called
    void onBannerAdTriggered(); //triggered when BannerView API only call is called for trigger
    void onBannerAdDismissed(); //triggered when BannerView API only call is called for dismissal

}
