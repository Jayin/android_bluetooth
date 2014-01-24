智器SDK帮助包中包含:
1.ZWatch手机助手安装apk 
2.手表通信程序apk 
3.手机与手表通信模块SDK jar包  
4.智器智能手表应用开发指南SDK_v1.0  
5.sdk_changelog.txt
6.sdk demo工程代码 
7.sdk demo apk程序 8. readme txt

智器手表SDK程序安装指南
1.在手机端安装智器Zwatch手机助手，在一同提供的包内有该程序，也可上智器的官网进行下载安装，下载地址：http://www.smartdevices.com.cn/support/download/Z1/
ex:adb install ***.apk
2.手表端安装手表同步通信程序
安装步骤 
(1).adb push 手表端同步程序.apk /sdcard/  
(2).adb shell;su;mount -o remount,rw /system;busybox cp /sdcard/手表端同步助手.apk  /system/app/IndroidSyncWatch.apk
3.打开手机与手表端蓝牙，通过Zwatch手机助手进行配对同步，点击Zwatch手机助手中发送提醒功能，测试通信是否连接。
4.运行demo程序
5.建立自己的android工程，导入smartdevices_sdk.jar
