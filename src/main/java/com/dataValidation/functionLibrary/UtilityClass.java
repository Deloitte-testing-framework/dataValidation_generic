package com.dataValidation.functionLibrary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UtilityClass {

	public static String getAllKeyOfMap(Map<String, List<String>> map) {
		String keys = map.keySet().stream().collect(Collectors.toList()).toString();
		keys = keys.substring(1, keys.length() - 1);
		String[] splitValue = keys.split(",");
		keys = "";
		for (String singleSplitValue : splitValue) {
			keys = keys + singleSplitValue.trim() + ",";
		}
		return keys;

	}

	public static File[] getListOfFiles(String folderName) {
		File files = new File(System.getProperty("user.dir") + folderName);
		File[] totalFiles = files.listFiles();

		return totalFiles;
	}

	public static ArrayList<String> getListOfFilePath(String folderName) {
		ArrayList<String> filePath = new ArrayList<String>();
		File[] listOfFiles = getListOfFiles(folderName);

		for (File singleFile : listOfFiles) {
			filePath.add(singleFile.getAbsolutePath().toUpperCase());
		}

		return filePath;

	}
}
