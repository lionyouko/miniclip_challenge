package com.thelion.advertads.workers;

import com.thelion.advertads.interfaces.ResourceGetter;

import java.util.ArrayList;
import java.util.List;

/**
 * This is just a generic class ot provide URLs
 * The idea is to remember that a generic helper ( in thee role of a provider) could provide different types of resources
 */
public class URLResourceHelper implements ResourceGetter {
    private List<String> resources;

    private static List<String> staticResources = new ArrayList<>();

    static {
        staticResources.add("https://sdk.eng.miniclip.com/code_challenge/banner/MNF_Banners_V1_300x50.jpg");
        staticResources.add("https://sdk.eng.miniclip.com/code_challenge/banner/BPM_Banner_V2_300x50px.jpg");
        staticResources.add("https://sdk.eng.miniclip.com/code_challenge/banner/UMG_Banner_V2_300x50px.jpg");
    }

    public URLResourceHelper(){

    }

    /**
     * Suppose the resources come from different fonts that are implemented in different ways
     * For thee demonstration, we maintain a list of the given strings of the challenge
     */
    @Override
    public void getResourcesFromExterior() {
        // Would fill resources
    }

    public List<String> getResourcesGathered(){
        return this.staticResources;
    }
}
