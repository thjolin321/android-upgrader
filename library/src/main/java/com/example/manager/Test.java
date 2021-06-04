package com.example.manager;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.manager.database.DownloadProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by tanghao on 2021/5/31
 */
class Test {
    public void dealListWithMutiThread(){
        List<Object> list = new ArrayList<Object>(10000);
        int index = 0;
        ExecutorService ex = Executors.newFixedThreadPool(5);
        int dealSize = 2000;
        List<Future<List<Object>>> futures = new ArrayList<>(5);
        //分配
        for(int i=0;i<= 5;i++,index+=dealSize){
            int start = index;
            if(start>=list.size()) break;
            int end = start + dealSize;
            end = end>list.size() ? list.size() : end;
            futures.add(ex.submit(new Task(list,start,end)));
        }
        try {
            //处理
            List<Object> result = new ArrayList<>();
            for(Future<List<Object>> future : futures){
                //合并操作
                result.addAll(future.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void queryImageFromAlbum() {
        Cursor cursor = DownloadProvider.context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                //获取唯一的id
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                //通过id构造Uri
                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                //解析uri
//                decodeUriForBitmap(uri);
            }
        }
    }


    private class Task implements Callable<List<Object>> {

        private List<Object> list;
        private int start;
        private int end;

        public Task(List<Object> list,int start,int end){
            this.list = list;
            this.start = start;
            this.end = end;
        }

        @Override
        public List<Object> call() throws Exception {
            Object obj = null;
            List<Object> retList = new ArrayList<Object>();
            for(int i=start;i<end;i++){
                obj = list.get(i);
                //你的处理逻辑
            }
            //返回处理结果
            return retList;
        }
    }

}