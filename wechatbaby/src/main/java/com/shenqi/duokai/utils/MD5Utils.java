package com.shenqi.duokai.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	
	
	public static void main(String[] args) throws FileNotFoundException {
		String encode = encode("hello,world");
		System.out.println(encode);
	}
	
	/**
	 * 对字符串进行md5加密
	 * @param text
	 * @return
	 */
	public static String encode(String text) {
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] buffer = digest.digest(text.getBytes());
			StringBuffer sb = new StringBuffer();
			for (byte b : buffer) {
				int a = b & 0xff;
				String hex = Integer.toHexString(a);
				if (hex.length() == 1) {
					hex = 0 + hex;
				}
				sb.append(hex);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * 对一个文件进行md5处理
	 * @param in
	 * @return
	 */
	public static String encode(InputStream in) {
		try {
			MessageDigest digester = MessageDigest.getInstance("MD5");
			byte[] bytes = new byte[8192];
			int byteCount;
			while ((byteCount = in.read(bytes)) > 0) {
				digester.update(bytes, 0, byteCount);
			}
			byte[] digest = digester.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				int a = b & 0xff;
				String hex = Integer.toHexString(a);
				if (hex.length() == 1) {
					hex = 0 + hex;
				}
				sb.append(hex);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}
		}
		return null;
	}
}
