package com.track.toy.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class FileHelper {
	public static void fileCopy(String resource, String target) {
		File folder = new File(target.substring(0, target.lastIndexOf(File.separator)));
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File file = new File(target);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try (FileInputStream fis = new FileInputStream(resource);
				FileOutputStream fos = new FileOutputStream(target);) {

			byte[] buf = new byte[1024];
			int by = 0;
			while ((by = fis.read(buf)) != -1) {
				fos.write(buf, 0, by);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String getAppRoot() {
		try {
			return new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void createDirAndFileIfNotExists(File file) {
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteFile(File f, boolean flg) {
		listDirectory(f).forEach(item -> {
			deleteFile(item, true);
		});

		listFiles(f).forEach(item -> {
			item.delete();
		});

		if (flg) {
			f.delete();
		}
	}

	public static List<File> listDirectory(File parent) {
		File[] listFiles = parent.listFiles(file -> {
			return file.isDirectory();
		});
		if (listFiles != null) {

			return Arrays.asList(listFiles);
		}
		return new ArrayList<>();
	}

	public static List<File> listFiles(File parent) {
		File[] listFiles = parent.listFiles(file -> {
			return file.isFile();
		});
		if (listFiles != null) {

			return Arrays.asList(listFiles);
		}
		return new ArrayList<>();
	}

	public static String getAppPath(Class<?> cls) {
		if (cls == null) {
			throw new java.lang.IllegalArgumentException("cls is null");
		}
		ClassLoader loader = cls.getClassLoader();
		String clsName = cls.getName() + ".class";
		Package pack = cls.getPackage();
		String path = "";
		if (pack != null) {
			String packName = pack.getName();
			if (packName.startsWith("java.") || packName.startsWith("javax."))
				throw new java.lang.IllegalArgumentException("cls is system class");
			clsName = clsName.substring(packName.length() + 1);
			if (packName.indexOf(".") < 0)
				path = packName + "/";
			else {
				int start = 0;
				int end = 0;
				end = packName.indexOf(".");
				while (end != -1) {
					path = path + packName.substring(start, end) + "/";
					start = end + 1;
					end = packName.indexOf(".", start);
				}
				path = path + packName.substring(start) + "/";
			}
		}
		java.net.URL url = loader.getResource(path + clsName);
		String realPath = url.getPath();
		int pos = realPath.indexOf("file:");
		if (pos > -1)
			realPath = realPath.substring(pos + 5);
		pos = realPath.indexOf(path + clsName);
		realPath = realPath.substring(0, pos - 1);
		if (realPath.endsWith("!"))
			realPath = realPath.substring(0, realPath.lastIndexOf("/"));
		try {
			realPath = java.net.URLDecoder.decode(realPath, "utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return realPath;
	}

	public static String FileToBase64(File file) {
		try (FileInputStream inputFile = new FileInputStream(file);) {
			byte[] buffer = new byte[(int) file.length()];
			inputFile.read(buffer);
			inputFile.close();
			return new String(Base64.getEncoder().encode(buffer));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
