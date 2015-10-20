/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synergy.cs530.ccsu.mobileauthentication.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author ewint
 */
public class FileTransfer {

    private final String TAG = this.getClass().getName();

    /**
     * Transfers a specific file to a destination directory
     *
     * @param sourceFile           the file to be transferred
     * @param destinationDirectory the intended directory to transfer files
     * @return boolean flag indicating the file has successfully transfered
     */
    public boolean transferFile(File sourceFile, File destinationDirectory) {
        boolean result = false;

        if (sourceFile != null && sourceFile.exists() && sourceFile.isFile()
                && destinationDirectory != null
                && destinationDirectory.isDirectory()) try {

            if (!destinationDirectory.exists()) {
                try {
                    destinationDirectory.mkdirs();
                } catch (SecurityException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
            //Create the destination file
            File destinationFile = new File(destinationDirectory,
                    sourceFile.getName());
            //Create the destination file channel
            FileChannel destinationFileChannel
                    = new FileOutputStream(destinationFile).getChannel();
            //transfer data
            //Get the source file channel
            FileChannel sourceFileChannel = new FileInputStream(
                    sourceFile).getChannel();
            long fileSize = sourceFileChannel.size();
            long transferred = destinationFileChannel.transferFrom(
                    sourceFileChannel, 0,
                    fileSize);
            result = ((fileSize - transferred) == 0);
            sourceFileChannel.close();
            destinationFileChannel.close();
        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }

    /**
     * Transfers a collection of files in a selected directory to a destination
     * directory
     *
     * @param sourceDirectory      a directory containing files to be transfered
     * @param destinationDirectory the intended directory to transfer files
     * @return
     */
    public FileStatus[] transferDirectory(File sourceDirectory,
                                          File destinationDirectory) {
        FileStatus[] result = new FileStatus[0];
        try {
            if (null != sourceDirectory && sourceDirectory.isDirectory()
                    && null != destinationDirectory
                    && destinationDirectory.isDirectory()) {
                File[] files = sourceDirectory.listFiles();
                int size = files.length;
                result = new FileStatus[size];
                for (int i = 0; i < size; i++) {
                    File file = files[i];
                    result[i] = new FileStatus(file.getName(),
                            transferFile(file, destinationDirectory));
                }
            }
        } catch (SecurityException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return result;
    }

}
