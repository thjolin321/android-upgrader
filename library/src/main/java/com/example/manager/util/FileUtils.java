package com.example.manager.util;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.manager.database.DownloadProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by tanghao on 2021/5/31
 */
public class FileUtils {


    private static String defaultSaveRootPath;

    public static String getDefaultSaveRootPath() {
        if (!TextUtils.isEmpty(defaultSaveRootPath)) {
            return defaultSaveRootPath;
        }
        if (DownloadProvider.context.getFilesDir() == null) {
            defaultSaveRootPath = Environment.getDownloadCacheDirectory().getAbsolutePath() + File.separator + "UUDownload";
        } else {
            defaultSaveRootPath = DownloadProvider.context.getFilesDir().getAbsolutePath() + File.separator + "UUDownload";
        }
        File file = new File(defaultSaveRootPath);
        if (file.exists()) {
            if (!file.isDirectory()) {
                defaultSaveRootPath += "2";
            }
        } else {
            file.mkdir();
        }
        Logl.e("defaultSaveRootPath: " + defaultSaveRootPath);
        return defaultSaveRootPath;
    }

    public static String strMd5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append(0);
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 获取文件MD5值
     *
     * @param filePath
     * @return
     */
    public static String fileMd5(String filePath) {
        String md5 = "";
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            MessageDigest messageDigest;
            try {
                messageDigest = MessageDigest.getInstance("MD5");
                InputStream in = new FileInputStream(file);
                byte[] cache = new byte[1024];
                int nRead = 0;
                while ((nRead = in.read(cache)) != -1) {
                    messageDigest.update(cache, 0, nRead);
                }
                in.close();
                byte data[] = messageDigest.digest();
                md5 = byteToHex(data);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                md5 = "";
            } catch (IOException e) {
                e.printStackTrace();
                md5 = "";
            }
        }
        return md5;
    }

    protected static String byteToHex(byte[] bytes) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        StringBuffer stringbuffer = new StringBuffer(2 * bytes.length);
        for (byte b : bytes) {
            char c0 = hexDigits[b >>> 4 & 0xf];
            char c1 = hexDigits[b & 0xf];
            stringbuffer.append(c0);
            stringbuffer.append(c1);
        }
        return stringbuffer.toString();
    }


    public static String getTargetFilePath(String directory, String filename) {
        if (directory == null) {
            directory = getDefaultSaveRootPath();
        }
        return FileUtils.generateFilePath(directory, filename);
    }

    private static String generateFilePath(String directory, @NonNull String filename) {
        return String.format("%s%s%s", directory, File.separator, filename);
    }

    public static boolean isFileExist(String filePath) {
        boolean result = false;
        File file = new File(filePath);
        if (file != null && file.exists() && file.isFile()) {
            result = true;
        }
        return result;
    }

    public static boolean isFileExist(File file) {
        boolean result = false;
        if (file != null && file.exists() && file.isFile()) {
            result = true;
        }
        return result;
    }


    public static boolean delete(String filePath) {
        boolean result = false;
        File file = new File(filePath);
        if (file.exists()) {
            result = file.delete();
        } else {
            result = true;
        }
        return result;
    }

    public static boolean copyFile(String oldPath, String newPath) {
        boolean flag = false;
        try {
            Log.e("Huan", "old path ==" + oldPath);
            Log.e("Huan", "new path ==" + newPath);
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                Log.e("Huan", "old path ==cunzai");
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];

                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                flag = true;
            }
        } catch (Exception e) {
            Log.e("ad", "copy error");
            e.printStackTrace();
        }

        return flag;
    }

    private static long getFileSize(File file) {
        long size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
            } else {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static void createNewFile(String file) {
        createNewFile(file, null);
    }

    public static void createNewFile(String file, String content) {
        try {
            if (!isFileExist(file)) {
                File newFile = new File(file);
                File fileParent = newFile.getParentFile();
                if (!fileParent.exists()) {
                    fileParent.mkdirs();
                }
                newFile.createNewFile();
            }
            if (!TextUtils.isEmpty(content)) {
                write(file, content, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void write(String filename, String content,
                                boolean isAppend) {
        try {
            FileWriter fw = new FileWriter(filename, isAppend);
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            for (File chileFile : file.listFiles()) {
                deleteFile(chileFile);
            }

            file.delete();
        }
    }


}