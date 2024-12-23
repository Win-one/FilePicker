package com.oneskyer.library.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.oneskyer.library.model.FileListItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Utility {
    /**
     * Post Lollipop Devices require permissions on Runtime (Risky Ones), even though it has been
     * specified in the uses-permission tag of manifest. checkStorageAccessPermissions
     * method checks whether the READ EXTERNAL STORAGE permission has been granted to
     * the Application.
     *
     * @return a boolean value notifying whether the permission is granted or not.
     */
    public static boolean checkStorageAccessPermissions(Context context) {   //Only for Android M and above.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            String permission = "android.permission.READ_EXTERNAL_STORAGE";
            int res = context.checkCallingOrSelfPermission(permission);
            return (res == PackageManager.PERMISSION_GRANTED);
        } else {   //Pre Marshmallow can rely on Manifest defined permissions.
            return true;
        }
    }

    @android.annotation.TargetApi(Build.VERSION_CODES.TIRAMISU)
    public static boolean checkMediaAccessPermissions(Context context) {
        String audioPermission = Manifest.permission.READ_MEDIA_AUDIO;
        String imagesPermission = Manifest.permission.READ_MEDIA_IMAGES;
        String videoPermission = Manifest.permission.READ_MEDIA_VIDEO;
        // Check for permissions and if permissions are granted then it will return true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // You have the permissions, you can proceed with your media file operations.
            return context.checkSelfPermission(audioPermission) == PackageManager.PERMISSION_GRANTED ||
                    context.checkSelfPermission(imagesPermission) == PackageManager.PERMISSION_GRANTED ||
                    context.checkSelfPermission(videoPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    /**
     * Prepares the list of Files and Folders inside 'inter' Directory.
     * The list can be filtered through extensions. 'filter' reference
     * is the FileFilter. A reference of ArrayList is passed, in case it
     * may contain the ListItem for parent directory. Returns the List of
     * Directories/files in the form of ArrayList.
     *
     * @param internalList ArrayList containing parent directory.
     * @param inter        The present directory to look into.
     * @param filter       Extension filter class reference, for filtering files.
     * @return ArrayList of FileListItem containing file info of current directory.
     */
    public static ArrayList<FileListItem> prepareFileListEntries(ArrayList<FileListItem> internalList, File inter, ExtensionFilter filter, boolean show_hidden_files) {
        try {
            //Check for each and every directory/file in 'inter' directory.
            //Filter by extension using 'filter' reference.

            for (File name : Objects.requireNonNull(inter.listFiles(filter))) {
                //If file/directory can be read by the Application
                if (name.canRead()) {
                    if (name.getName().startsWith(".") && !show_hidden_files) continue;
                    //Create a row item for the directory list and define properties.
                    FileListItem item = new FileListItem();
                    item.setFilename(name.getName());
                    item.setDirectory(name.isDirectory());
                    item.setLocation(name.getAbsolutePath());
                    item.setTime(name.lastModified());
                    //Add row to the List of directories/files
                    internalList.add(item);
                }
            }
            //Sort the files and directories in alphabetical order.
            //See compareTo method in FileListItem class.
            Collections.sort(internalList);
        } catch (NullPointerException e) {   //Just dont worry, it rarely occurs.
            e.printStackTrace();
            internalList = new ArrayList<>();
        }
        return internalList;
    }

    /**
     * Method checks whether the Support Library has been imported by application
     * or not.
     *
     * @return A boolean notifying value wheter support library is imported as a
     * dependency or not.
     */
    @SuppressLint("PrivateApi")
    private boolean hasSupportLibraryInClasspath() {
        try {
            Class.forName("androidx.appcompat:appcompat:1.7.0");
            return true;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
