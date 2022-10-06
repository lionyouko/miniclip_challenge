package com.thelion.advertads.typedefenums;

import androidx.annotation.StringDef;

/**
 * The type def enum state of the banner ad to mimic a lifecycle of ad.
 *
 * Lifecycles are related to Fragments, Activities and Views, and we want to inform
 * the activities or fragments that the banner, a view, has changed its state.
 *
 * This is an abstraction on top of the events sourced from Android System. For example,
 * when onDraw() is called, READY state should be set and a listener should be notified of it.
 *
 * Why we want that? Because we want to define particular lifecycle-like events to be observed
 * for the View that is not always related stricly to system events (like a touch-event that triggers onTouch()).
 *
 * We also want to manage different concerns in different stages of the life of the view.
 *
 * The STARTING state: We want to download non-cached content in this state
 * The READY state: We want to show the content of ads in this state
 * The STOPPED state: We want to stop the view on this state
 *
 * It is said that type def enums are better than plain java enums, as they consume much less memory.
 * Even nowadays that devices have 6,8, 12GB of RAM, we try to make the library footprint small as possible.
 */
public class BannerState {
    public static final String STARTING = "STARTING";
    public static final String READY = "READY";
    public static final String STOPPED = "STOPPED";

    @StringDef({STARTING,READY, STOPPED})
    public @interface BannerStateGiven{}

    private final String bannerState;

    public BannerState (@BannerStateGiven String bannerState) {
        this.bannerState = bannerState;
    }

    public String getBannerState() {
        return this.bannerState;
    }


}
