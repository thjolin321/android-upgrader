# android-upgrader
一个App的更新框架，包含增量更新、完整链接更新、跳转应用市场更新。你可以一句代码，传入更新的参数，交由框架自动帮你处理所有操作，也可以实现框架提供的接口，自由定制你需要的UI和业务逻辑。
如果你仅仅需要一个下载器，可以单独集成下载框架，为你提供一个功能全备的Android多线程下载器，包含断点下载、多任务同时下载、进度速度回调等。
## 简单效果图，建议下载demo体验。
![](https://obs-mips3-test.obs.cn-north-1.myhuaweicloud.com/bk_run_log/20200306/xa/upgrader_github_jietu.png)
## 使用
```
// gradle集成，此依赖已包含下载框架
implementation 'com.github.thjolin321.android-upgrader:library:v1.0.0'
// 单独下载框架，如果你仅仅想使用下载功能的话，请使用此依赖
implementation 'com.github.thjolin321.android-upgrader:uudownload:v1.0.0'
```
## 使用示例
``` 
// App更新及增量更新使用示例
Upgrader.with().start(new ApkUpdateBean.Builder()
                .newApkUrl("完整apk下载链接")
                .newApkVersionCode(2)
                // 可选，可增多个，增量更新时使用
                .addApkPatchBean(new ApkPatchBean(1, "增量文件链接"))
                .build());
                
// 下载框架使用示例                
UuDownloader.with().start(new DownloadTask.Builder().url(url).build(), new DownloadListener() {
            @Override
            public void success(String path) {
            }
            @Override
            public void progress(int progress) {
            }
            @Override
            public void failed(String meg) {
            }
        });
``` 
## Api详解
```
*********更新框架Api详解*********
Upgrader.with().setConfiger(new UpgraderConfiger.Builder()
                // 指定更新方式 增量更新、完整apk更新、跳转应用市场，不设置将自动匹配
                .updateMethod()
                // 是否显示通知，仅在使用默认UI时有用
                .needNotifycation()
                // 是否显示下载进度，仅在使用默认UI时有用
                .showDownladProgress()
                // 是否静默安装，静默安装将先默认下载，然后再弹框让用户直接安装。
                .silent()
                // 是否强制更新
                .forceUpdate(false)
                // 强制更新时，用户点出关闭按钮的回调
                .forceExitListener()
                // 自定义UI接口，建议参考框架默认UI进行实现
                .uiListener()
                // 更新流程生命周期回调，可在此观察整个更新进度，并进行处理
                .lifeCycleListener()
                .build())
                .start(new ApkUpdateBean.Builder()
                        .newApkUrl("完整apk下载链接")
                        .newApkVersionCode(2)
                        // 可选，可增多个，增量更新时使用
                        .addApkPatchBean(new ApkPatchBean(1, "增量文件链接"))
                        .build());
*********下载框架Api详解*********
// 单任务详细配置
UuDownloader.with().start(new DownloadTask.Builder()
                // 下载链接，唯一一个必传参数，框架会判重处理
                .url()
                // 下载目录，已适配android文件权限，不指定将默认使用app文件目录
                .fileParent()
                // 文件名称，不指定将从链接获取
                .fileName()
                // 下载文件的md5值
                .newFileMd5()
                // 下载线程数据，最大值为5，为不指定将依据文件策略自动匹配数量
                .blockSize()
                // 是否强制下载
                .forceRepeat()
                // 是否需要下载进度，默认false
                .needProgress()
                // 进度回调等分数量。默认100份，如果设置此值为5，那么次回调100/5次
                .progressDivide()
                // 是否需要速度回调，须配合DownloadListenerWithSpeed使用
                .needSpeed()
                // 所有回调是否回调至主线程，默认false
                .needMoveToMainThread()
                .build(), new DownloadListener());
                
// 多任务配置，最大同时下载数量为3
List<String> list = new ArrayList<>();
        list.add(url);
        list.add(url1);
        list.add(url2);
        list.add(url3);
        UuDownloader.with().start(list, new MultiDownloadListener() {
            @Override
            public void onFinish() {
            }
            @Override
            public void onSuccess(String url, String path) {
            }
            @Override
            public void onFailed(String url) {
            }
        });         
```
## 增量更新配置及原理说明
### 第一步：生成差分包，上传至服务器，需要用户手动实现
有old1.apk、old2.apk版本号versionCode分别为1和2，现在有new3.apk版本号为3，我们需要对old1和old2分别进行生成差分包patch.apk（命名随意）。然后将差分包分别上传至服务器，通过请求链接返回给客户端。
### 第二步：下载对应差分包，合成new.apk，进行安装，框架已实现
差分包下载地址：[差分包工具](https://obs-mips3-test.obs.cn-north-1.myhuaweicloud.com/bk_run_log/20200306/xa/bsdiff.zip) ，请查看README.txt，获取使用方法。具体差分算法原理，请自行搜索。






