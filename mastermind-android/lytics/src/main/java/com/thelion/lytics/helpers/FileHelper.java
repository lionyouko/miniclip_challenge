package com.thelion.lytics.helpers;


import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import java.io.File;
import java.time.Instant;

public class FileHelper {
    /*FILE FORMAT IS:
        Stored files must be named with the user_id concatenated with the storage timestamp;
        u2338475_2384723, for example
    */

    private final static String SEPARATOR = "_";

    /**
     * Produces the name for a file in format [user_id]_[timestamp]
     * @param userId the current user id being used in a game session
     * @return the file name that can be used when storing a new file with events
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String createStandardFileName(String userId) {
        long fileTimeStamp = Instant.now().getEpochSecond();
        StringBuilder sb = new StringBuilder();
        sb.append(userId);
        sb.append(SEPARATOR);
        sb.append(fileTimeStamp);
        return sb.toString();
    }


    /**
     * Get timestamp from a default filename
     * @param filename string with default file name format for the library
     * @return long that is the time when file was created
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long recoverTStampFromStandardFileName(String filename){
        StringBuilder sb = new StringBuilder(filename);
        String fileTimeStamp =  sb.substring(sb.indexOf(SEPARATOR) + 1).toString();
        //Log.d("FileHelper","fileTimeStamp: " + fileTimeStamp);
        return Long.parseLong(fileTimeStamp);
    }


    /**
     * Get the timestamp from a file instead of a name
     * @param f file to extract timestamp from the name
     * @return long that is the time when file was created
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long recoverTStampFromFile(File f){
        StringBuilder sb = new StringBuilder(f.getName());
        return FileHelper.recoverTStampFromStandardFileName(sb.toString());
    }

    /**
     * Loads files from disk cache
     * It may need permission to check the disk
     * @return List of Stored files in cache
     */
    public static File[] loadFromCache(String diskCachePath) {
        File dir = new File(diskCachePath);
        Log.d("FileHelper", " cache exists? " + dir.exists());
        Log.d("FileHelper", " lengthFiles[] " + dir.listFiles().length);
        return dir.listFiles();
    }

    /**
     * Clear the files saved with events
     * @param diskCachePath
     */
    public static void clearFiles(String diskCachePath) {
        File cachedFileDir = new File(diskCachePath);
        if(cachedFileDir.exists() && cachedFileDir.isDirectory()) {
            File[] cachedFiles = cachedFileDir.listFiles();
            for(File f : cachedFiles) {
                if(f.exists() && f.isFile()) {
                    f.delete();
                }
            }
        }
        cachedFileDir.delete();
    }


}
