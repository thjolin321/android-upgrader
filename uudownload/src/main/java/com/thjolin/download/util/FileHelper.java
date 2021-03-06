package com.thjolin.download.util;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.thjolin.download.database.DownloadProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by th on 2021/5/31
 */
public class FileHelper {

    private static String defaultSaveRootPath;

    public static String getDefaultSaveRootPath() {
        if (!TextUtils.isEmpty(defaultSaveRootPath)) {
            return defaultSaveRootPath;
        }
        defaultSaveRootPath = DownloadProvider.context.getFilesDir().getAbsolutePath();
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
        return FileHelper.generateFilePath(directory, filename);
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

    public static boolean createNewFile(String file) {
        return createNewFile(file, null);
    }

    public static boolean createNewFile(String file, String content) {
        try {
            boolean createFile = false;
            if (!isFileExist(file)) {
                File newFile = new File(file);
                File fileParent = newFile.getParentFile();
                if (!fileParent.exists()) {
                    fileParent.mkdirs();
                }
                createFile = newFile.createNewFile();
            }
            if (createFile && !TextUtils.isEmpty(content)) {
                write(file, content, false);
            }
            return createFile;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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

    public static void creatDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
    }

    public static long getFileSize(File f){
        FileChannel fc= null;
        long fileSize = 0;
        try {
            if (f.exists() && f.isFile()){
                FileInputStream fis= new FileInputStream(f);
                fc= fis.getChannel();
                fileSize = fc.size();
            }else{
                Log.e("getFileSize","file doesn't exist or is not a file");
            }
        } catch (FileNotFoundException e) {
            Log.e("getFileSize",e.getMessage());
        } catch (IOException e) {
            Log.e("getFileSize",e.getMessage());
        } finally {
            if (null!=fc){
                try{
                    fc.close();
                }catch(IOException e){
                    Log.e("getFileSize",e.getMessage());
                }
            }
        }
        return fileSize;
    }
}