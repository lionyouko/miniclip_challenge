package com.thelion.advertads.workers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.thelion.advertads.interfaces.AdDownloader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.msebera.android.httpclient.Header;

/**
 * This class implements the tasks to download image ads from web and to cache them on disk
 * so if we have them already cached, we will not try to spend network resources.
 * I made this class Singleton for the case of multiple banner views
 * The following resources were consulted to build this class:
 * Resource 1 (adapted, for File API and caching in disk): https://github.com/loopj/android-smart-image-view
 * Resource 2 (as library, for downloading ad images): https://loopj.com/android-async-http/
 */
public class ImageAdsDownloadHelper implements AdDownloader {

    private static final String DISK_CACHE_PATH = "/advertads_cached_ads/";
    private List<String> adsDiskUrls;
    private boolean areAdsCachable;
    private boolean isDiskCacheEmpty;
    private String diskCachePath;
    private ExecutorService writeToCacheThread;
    private AsyncHttpClient clientForDownloadAdImages;
    private URLResourceHelper urlResourceHelper;

    private static ImageAdsDownloadHelper INSTANCE;

    /*
------------------------- Constructors and Singleton Pattern related functions   -------------------------
    I know Singleton Pattern is debatable, but I use it here trying to balance what would be better:
    One instance for multiple Banner Views, or various Instances for various Banner Views.
    I made this choice for this time.
    */

    public static ImageAdsDownloadHelper getInstance(Context c) {
        if (INSTANCE == null)
            INSTANCE = new ImageAdsDownloadHelper(c);
        return INSTANCE;
    }

    private ImageAdsDownloadHelper(Context c) {



        urlResourceHelper = new URLResourceHelper();

        // Set up disk cache path
        Context appContext = c.getApplicationContext();
        diskCachePath = appContext.getCacheDir().getAbsolutePath() + DISK_CACHE_PATH;
        Log.d("ImageAdsDownloadHelper"," diskCachePath " + diskCachePath);

        // There is some decisions that should be taken regarding this class.
        // I am choosing to look for disk cache in the beginning, so it will not run createDiskCache all time
        if (!diskCacheExists())
            createDiskCache();


        // Get from cache first if any
        adsDiskUrls = fromFileToResourceUrl(loadFromCache());

        // Parallel execution for downloading and writing ads
        writeToCacheThread = Executors.newSingleThreadExecutor();

        clientForDownloadAdImages = new AsyncHttpClient();

    }

    /*
------------------------- Setter Functions   -------------------------
    */

    /**
     * Set the resource helper in order to download helper to know from where it will download resources for ads
     * (it could be an interface)
     * @param urlResource - A class that has resources to be used to download ads
     */
    public void setResourceHelper(URLResourceHelper urlResource){
        this.urlResourceHelper = urlResource;
    }


    /*
------------------------- Download Ads Functions   -------------------------
    */

    /**
     * This implementation downloads an image from the web and stores in cache altogether
     * @param adURL  web resource
     * @param c Context of the Application
     */
    private void downloadAd(String adURL, Context c) {
        clientForDownloadAdImages.get(adURL, new FileAsyncHttpResponseHandler(c) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                // does nothing for now
                Log.d("ImageAdsDownloadHelper", " downloadAd() Failed Download");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                // Do something with the file `response`
                if (response != null) {
                    String downloadedFilePath = response.getAbsolutePath();
                    Log.d("ImageAdsDownloadHelper", " downloadAd() : " + downloadedFilePath);
                    Bitmap imageAd = BitmapFactory.decodeFile(downloadedFilePath);
                    boolean cached = cacheImageAdToDisk(adURL,imageAd);
                }

            }
        });
    }

    /**
     * Download all ads that resource set has to offer if they aren't already loaded
     * @param c - The context of the application
     */
    @Override
    public void downloadAds(Context c) {
        if (this.urlResourceHelper != null) {
            for (String url : urlResourceHelper.getResourcesGathered()) {
                if(!this.adsDiskUrls.contains(formatCachedImageKey(url))) {
                    Log.d("ImageAdsDownloadHelper", ":: " + url);
                    downloadAd(url, c);
                }
            }
        }
    }


    /*
------------------------- Cache Image Ads Functions   -------------------------
    */
    /**
     *  Helper function to cache images to the disk
     *  Resource: https://github.com/loopj/android-smart-image-view/blob/master/src/com/loopj/android/image/WebImageCache.java (line 102)
     * @param imageURL - web url of the string to be cached
     * @param imageToCacheToDisk - The Bitmap representing that web url
     * @return True if image was successfully saved in cache
     */
    private boolean cacheImageAdToDisk(String imageURL, Bitmap imageToCacheToDisk) {
        boolean[] successfullyCached = {false};

        // Imagine we give the option to clear disk cache publicly. So it needs to create it
        // again if it wants to store images
        if (!areAdsCachable) {
            // It may make the inner if of the run function to be innocuous
            createDiskCache();
        }

        writeToCacheThread.execute(() -> {
            if(areAdsCachable) {
                BufferedOutputStream ostream = null;
                try {

                    ostream = new BufferedOutputStream(new FileOutputStream(new File(diskCachePath, formatCachedImageKey(imageURL))), 2*1024);
                    imageToCacheToDisk.compress(Bitmap.CompressFormat.PNG, 100, ostream);

                    String addedToDiskImageUrl = formatCachedImageKey(imageURL);

                    // Just to prevent concurrency problems
                    synchronized (adsDiskUrls) {
                        adsDiskUrls.add(addedToDiskImageUrl);
                    }

                    successfullyCached[0] = true;
                    Log.d("ImageAdsDownloadHelper", " cached image: " + addedToDiskImageUrl);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(ostream != null) {
                            ostream.flush();
                            ostream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // From the very first cache onwards, disk cache is not empty
        if (successfullyCached[0]) {
            isDiskCacheEmpty = false;
        }

        return successfullyCached[0];
    }


    /**
     *  Helper function to save the image ads url into friendly format
     *
     * @param url - the url to be formatted without problematic (special) chars
     * @return The formatted version of url
     */
    private String formatCachedImageKey(String url) {
        if(url == null){
            throw new RuntimeException("Null url passed in");
        } else {
            return url.replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
        }
    }

    /**
     * Helper function for checking if disk cache has at least an image
     * @return whether or not cache has ad images
     */
    public boolean isDiskCacheEmpty() {
        return this.isDiskCacheEmpty;
    }

    /**
     * Method for getting a list of images that may be present in disk cache
     * @return list of Bitmaps representing the cached images on disk (it can be empty)
     */
    public List<Bitmap> getCachedOnDiskImageAds(){
        return fromFileToImages(loadFromCache());

    }

    /**
     * Helper function to build an Bitmap (image) from the string resource given.
     * NOT USED
     * @param url - resource for an image on the disk
     * @return return an bitmap instance of a image file cached on disk
     */
    private Bitmap getBitmapFromDisk(String url) {
        Bitmap bitmap = null;
        Bitmap rbitmap = null;
        if(areAdsCachable){
            String filePath = getFilePath(url);
            Log.d("ImageAdsDownloadHelper","getBitmapFromDisk " + "url: " + url + "filePath: " + filePath);
            Log.d("ImageAdsDownloadHelper","rbitmap exists");
            File file = new File(filePath);
            if(file.exists()) {
                bitmap = BitmapFactory.decodeFile(filePath);
                rbitmap = Bitmap.createScaledBitmap(bitmap,100,50,false);
                Log.d("ImageAdsDownloadHelper","rbitmap exists");
            }
        }
        return rbitmap;
    }


    /**
     *  Helper function for finding the cached files
     *
     * @param url - url of the file saved on the disk
     * @return complete path of the file cached
     */
    private String getFilePath(String url) {
        return diskCachePath + formatCachedImageKey(url);
    }



    /**
     * Helper function to avoid inserting duplicates in disk cache
     * @param url url of the maybe-cached resource
     * @return if there is a cache of a file from that url
     */
    private boolean isFileCached(String url){
        File maybeCachedFile = new File(diskCachePath, formatCachedImageKey(url));
        return maybeCachedFile.isFile();
    }

    /**
     *
     * @param url resource string to verify if it was loaded from cache files
     * @return whether or not the file was found in cache files
     */
    private boolean isFileSoftCached(String url) {
        return this.adsDiskUrls.contains(formatCachedImageKey(url));
    }

    /**
     * Loads files from disk cache in initialization of this Class
     * It may need permission o check the disk
     * @return List of Stored files in cache
     */
    private File[] loadFromCache() {
        File dir = new File(this.diskCachePath);
        Log.d("ImageAdsDownloadHelper", " cache exists? " + dir.exists());
        Log.d("ImageAdsDownloadHelper", " lengthFiles[] " + dir.listFiles().length);
        return dir.listFiles();
    }

    /**
     * Helper function to make Files turn into Bitmaps (images)
     * @param files Array of Files that can be turned into Bitmap
     * @return
     */
    private List<Bitmap> fromFileToImages(File[] files) {
        List<Bitmap> cachedImagesOnDisk = new ArrayList<>();
        if (files != null) {
            for (File f : files) {
                Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                Bitmap rbitmap = Bitmap.createScaledBitmap(bitmap,300,50,false);
                cachedImagesOnDisk.add(rbitmap);
            }
        }
        return cachedImagesOnDisk;
    }


    /**
     *
     * @param files list of files in a cache dir
     * @return list of URLs (names) of those files (must be used with a given DISK CACHE PATH)
     */
    private List<String> fromFileToResourceUrl(File[] files) {
        List<String> cachedUrlsOnDisk = new ArrayList<>();
        if (files != null) {
            for (File f : files) {
                String s = f.getName();
                Log.d("ImageAdsDownloadHelper", " stored with name " + s);
                cachedUrlsOnDisk.add(s);
            }
        }
        return cachedUrlsOnDisk;
    }

    /**
     * Helper function to clear the disk cache by eliminating all files in cache dir
     * There is a similar function in resource link, but by working with File API, I got used to what I would have to do.
     */
    public void clearDiskCache() {
        File cachedFileDir = new File(this.diskCachePath);
        if(cachedFileDir.exists() && cachedFileDir.isDirectory()) {
            File[] cachedFiles = cachedFileDir.listFiles();
            for(File f : cachedFiles) {
                if(f.exists() && f.isFile()) {
                    f.delete();
                }
            }
        }
        cachedFileDir.delete();
        this.areAdsCachable = false;
    }

    /**
     * Helper Function - The setup of the disk cache made in a function
     */
    private void createDiskCache() {
        // Set up the actual filesystem dir for the cache
        File outFile = new File(diskCachePath);
        outFile.mkdirs();
        areAdsCachable = outFile.exists(); // ads are cachable of a cache has been made
        isDiskCacheEmpty = true;
    }

    /**
     * Helper Function - looks if filesystem has cache setup for image ads
     * @return true if there is already a cache in file system
     */
    private boolean diskCacheExists(){
        File outFile = new File(diskCachePath);
        return outFile.exists();
    }

}
