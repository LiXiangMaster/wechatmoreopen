package com.shenqi.duokai.interf;

import java.io.File;

public interface OnDownloadListener {

	/**
	 * 下载成功的回调方法
	 * @param destFile
	 */
	void onDownloadSuccess(File destFile);
	
	/**
	 * 下载失败的回调方法
	 */
	void onDownlaodFailed();
}
