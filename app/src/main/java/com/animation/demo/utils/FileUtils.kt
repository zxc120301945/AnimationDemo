package com.animation.demo.utils

import android.content.Context
import android.os.Environment

/**
 * Created by my on 2018/06/28 0028.
 */
object FileUtils{
    /**
     * 获取app缓存路径
     * @param context
     * @return
     */
    fun getCachePath(context: Context): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            context.externalCacheDir?.path ?: context.cacheDir.path
        } else {
            //外部存储不可用
            context.cacheDir.path
        }
    }
}