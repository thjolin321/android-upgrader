package com.example.updatefrompatch;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class StorageActivity extends AppCompatActivity {

    private String path;

    public static void start(Context context) {
        Intent intent = new Intent(context, StorageActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        findViewById(R.id.btn_read_img).setOnClickListener((v) -> {
            requestStoragePermission(StorageActivity.this);
            new Thread((new Runnable() {
                @Override
                public void run() {
                    readImageFromMediaStore(v.getContext());
                }
            })).start();
        });

        findViewById(R.id.btn_write_img).setOnClickListener((v) -> {
            requestStoragePermission(StorageActivity.this);
            new Thread((new Runnable() {
                @Override
                public void run() {
                    writeImageToMediaStore(path);
                }
            })).start();
        });
    }

    private void readImageFromMediaStore(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {

            try {
                //取出路径
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                Bitmap bitmap = BitmapFactory.decodeFile(path);
            } catch (Exception e) {
                Log.d("test", e.getLocalizedMessage());
            }
            break;
        }

        return;
    }

    private void writeImageToMediaStore(String path) {
        if (TextUtils.isEmpty(path))
            return;
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap != null) {
//            Bitmap bitmap1 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bitmap1);
//            Paint paint = new Paint();
//            paint.setAntiAlias(true);
//            paint.setColor(Color.BLUE);
//            paint.setStrokeWidth(10);
//            canvas.drawCircle(0, 0, 20, paint);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            } catch (Exception e) {
                Log.d("test", e.getLocalizedMessage());
            }
        }
    }

    private void requestStoragePermission(Activity activity) {
        String[] checkList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        List<String> needRequestList = checkPermission(activity, checkList);
        if (needRequestList.isEmpty()) {
            Toast.makeText(StorageActivity.this, "无需申请权限", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission(activity, needRequestList.toArray(new String[needRequestList.size()]));
        }
    }

    //检查权限，并返回需要申请的权限列表
    private List<String> checkPermission(Context context, String[] checkList) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < checkList.length; i++) {
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(context, checkList[i])) {
                list.add(checkList[i]);
            }
        }
        return list;
    }

    //申请权限
    private void requestPermission(Activity activity, String requestPermissionList[]) {
        ActivityCompat.requestPermissions(activity, requestPermissionList, 100);
    }

    //用户作出选择后，返回申请的结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(StorageActivity.this, "存储权限申请成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(StorageActivity.this, "存储权限申请失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
