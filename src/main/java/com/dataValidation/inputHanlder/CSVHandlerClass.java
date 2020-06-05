package com.dataValidation.inputHanlder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.testng.Reporter;
import org.testng.SkipException;

import com.dataValidation.configHandler.IConstants;
import com.dataValidation.functionLibrary.UtilityClass;

public class CSVHandlerClass implements IConstants {
	public static Map<String, List<String>> loadCsvData(Map<String, List<String>> colNames, String folderName,
			String separator) {

		boolean sizeResult = false;

		if (folderName.equalsIgnoreCase(srcCSVFolderName)) {
			sizeResult = validateSourceCSVCount();
		} else {
			sizeResult = validateTargetCSVCount();
		}

		ArrayList<String> filePaths = UtilityClass.getListOfFilePath(folderName);

		FileInputStream fis = null;
		Scanner sc = null;
		Map<String, List<String>> dataOnFile = new HashMap<String, List<String>>();
		Map<Integer, String> colMaping = new HashMap<Integer, String>();
		if (sizeResult) {

			for (Entry<String, List<String>> entrySingleColNames : colNames.entrySet()) {
				String colNameValue = entrySingleColNames.getValue().get(0);
				String colNameKey = entrySingleColNames.getKey();
				Integer indexValueSingleFilePath = null;

				for (String singleFilePath : filePaths) {
					if (singleFilePath.contains(colNameValue)) {
						indexValueSingleFilePath = filePaths.indexOf(singleFilePath);
						break;
					}
				}
				try {
					fis = new FileInputStream(filePaths.get(indexValueSingleFilePath));
					sc = new Scanner(fis, "UTF-8");
					Path path = Paths.get(filePaths.get(indexValueSingleFilePath));
					String line0 = null;
					line0 = Files.readAllLines(path).get(0).replaceAll("\\s", "");
					String[] lineSplit = line0.split(",");
					for (int j = 0; j < lineSplit.length; j++) {
						String str = lineSplit[j].toUpperCase();
						if (colNameKey.contains(str)) {
							colMaping.put(j, str);
							break;
						}
					}
					List<String> valueList = new ArrayList<String>();
					String value = null;
					while (sc.hasNextLine()) {
						String line = sc.nextLine().replaceAll("\\s", "");
						if (!line.equalsIgnoreCase(line0)) {
							String fileLine[] = line.split(separator);
							for (Entry<Integer, String> entry : colMaping.entrySet()) {
								Integer key = entry.getKey();
								value = entry.getValue();
								Integer filedLength = getColoumnMapingFieldLength(folderName, value, colNames);
								String lineValue = fileLine[key];
								String lastCharacter = lineValue;
								if (lineValue.substring(lineValue.lastIndexOf(',') + 1).length() <= filedLength) {
									Reporter.log(
											"Column name [" + value + "]  the length is " + filedLength + "  data  ["
													+ lastCharacter + "] is lesser than or equal to defiend length",
											true);
								} else {
									Reporter.log("Column name [" + value + "]  the length is " + filedLength
											+ "  data  [" + lastCharacter + "] is greater to defiend length", true);
								}
								lineValue = lineValue.replaceAll("\\s", "").trim();
								valueList.add(lineValue);
							}
						}

					}
					dataOnFile.put(value, valueList);
					colMaping.clear();
				} catch (FileNotFoundException e) {
					Reporter.log("File was not found in the specified path " + System.getProperty("user.dir")
							+ filePaths.get(indexValueSingleFilePath), true);
					Reporter.log(e.toString(), true);
				} catch (IOException e) {
					Reporter.log(e.toString(), true);
				} finally {
					if (fis != null && sc != null) {
						try {
							fis.close();
						} catch (IOException e) {
							Reporter.log(e.toString(), true);
						}
						sc.close();
					}
				}
			}
		}
		return dataOnFile;

	}

	public static Map<String, List<String>> getSourceKeyComparisonListFromExcel() {

		String colNameComparison = null;
		String colFileNameComparison = null;
		String colFieldLength = null;
//		String colTransformationRule = null;
		Map<String, List<String>> targetKeyComparisonList = new LinkedHashMap<String, List<String>>();
		int rowCount = 1;
		int colCount = 0;
		do {
			colNameComparison = ExcelHandlerClass.getCellValue(csvConfigSheetName, rowCount, colCount);
			colFileNameComparison = ExcelHandlerClass.getCellValue(csvConfigSheetName, rowCount, colCount + 1);
			colFieldLength = ExcelHandlerClass.getCellValue(csvConfigSheetName, rowCount, colCount + 2);
//			colTransformationRule = ExcelHandlerClass.getCellValue(csvConfigSheetName, rowCount, colCount + 3);
			rowCount++;
			if (colNameComparison == null && colFileNameComparison == null) {
				break;
			} else {
				List<String> tempArryList = new ArrayList<String>();
				tempArryList.add(colFileNameComparison);
				tempArryList.add(colFieldLength);
//				tempArryList.add(colTransformationRule);
				targetKeyComparisonList.put(colNameComparison, tempArryList);
			}
		} while (colNameComparison != null && colFileNameComparison != null);

		return targetKeyComparisonList;

	}

	public static Map<String, List<String>> getTargetKeyComparisonListFromExcel() {
		String colNameComparison = null;
		String colFileNameComparison = null;
		String colFieldLength = null;
		String colTransformationRule = null;
		Map<String, List<String>> targetKeyComparisonList = new LinkedHashMap<String, List<String>>();
		int rowCount = 1;
		int colCount = 4;
		do {
			colNameComparison = ExcelHandlerClass.getCellValue(csvConfigSheetName, rowCount, colCount);
			colFileNameComparison = ExcelHandlerClass.getCellValue(csvConfigSheetName, rowCount, colCount + 1);
			colFieldLength = ExcelHandlerClass.getCellValue(csvConfigSheetName, rowCount, colCount + 2);
			colTransformationRule = ExcelHandlerClass.getCellValue(csvConfigSheetName, rowCount, colCount -1);
			rowCount++;
			if (colNameComparison == null && colFileNameComparison == null) {
				break;
			} else {
				List<String> tempArryList = new ArrayList<String>();
				tempArryList.add(colFileNameComparison);
				tempArryList.add(colFieldLength);
				tempArryList.add(colTransformationRule);
				targetKeyComparisonList.put(colNameComparison, tempArryList);
			}
		} while (colNameComparison != null && colFileNameComparison != null);
		return targetKeyComparisonList;

	}

	public static boolean validateSourceCSVCount() {

		File[] listFile = UtilityClass.getListOfFiles(srcCSVFolderName);

		Map<String, Integer> countList = new HashMap<String, Integer>();
		int size = 0;
		for (File singleFile : listFile) {
			try {
				String filePath = singleFile.getAbsolutePath();
				Path path = Paths.get(filePath);
				size = Files.readAllLines(path).size();
			} catch (FileNotFoundException e) {
				Reporter.log(e.toString());
			} catch (IOException e) {
				Reporter.log(e.toString(), true);
			}
			countList.put(singleFile.getAbsolutePath(), size);
			size = 0;
		}

		Set<Integer> values = new HashSet<Integer>(countList.values());
		boolean isUnique = values.size() == 1;
		if (isUnique) {
			for (Entry<String, Integer> entry : countList.entrySet()) {
				String fileName = entry.getKey();
				Integer fileRowSize = entry.getValue();
				Reporter.log("File name [" + fileName + "]  and ROW size is [" + fileRowSize + "] \n", true);
			}
			return isUnique;
		} else {
			for (Entry<String, Integer> entry : countList.entrySet()) {
				String fileName = entry.getKey();
				Integer fileRowSize = entry.getValue();
				Reporter.log("File name [" + fileName + "]  and ROW size is [" + fileRowSize + "] \n", true);
			}
			throw new SkipException("Size is not matching in source files" + countList);
		}
	}

	public static boolean validateTargetCSVCount() {

		File[] listFile = UtilityClass.getListOfFiles(targetCSVFolderName);

		Map<String, Integer> countList = new HashMap<String, Integer>();
		int size = 0;
		for (File singleFile : listFile) {
			try {
				String filePath = singleFile.getAbsolutePath();
				Path path = Paths.get(filePath);
				size = Files.readAllLines(path).size();
			} catch (FileNotFoundException e) {
				Reporter.log(e.toString());
			} catch (IOException e) {
				Reporter.log(e.toString(), true);
			}
			countList.put(singleFile.getAbsolutePath(), size);
			size = 0;
		}

		Set<Integer> values = new HashSet<Integer>(countList.values());
		boolean isUnique = values.size() == 1;
		if (isUnique) {
			for (Entry<String, Integer> entry : countList.entrySet()) {
				String fileName = entry.getKey();
				Integer fileRowSize = entry.getValue();
				Reporter.log("File name [" + fileName + "]  and ROW size is [" + fileRowSize + "] \n", true);
			}
			return isUnique;
		} else {
			for (Entry<String, Integer> entry : countList.entrySet()) {
				String fileName = entry.getKey();
				Integer fileRowSize = entry.getValue();
				Reporter.log("File name [" + fileName + "]  and ROW size is [" + fileRowSize + "] \n", true);
			}
			throw new SkipException("Size is not matching in source files" + countList);
		}
	}

	public static void printCountOfColumnsinFile(String folderName) {

		File[] listFile = UtilityClass.getListOfFiles(folderName);

		Map<String, Integer> countList = new HashMap<String, Integer>();
		int colSize = 0;
		for (File singleFile : listFile) {
			try {
				String filePath = singleFile.getAbsolutePath();
				Path path = Paths.get(filePath);
				colSize = Files.readAllLines(path).get(0).split(",").length;
			} catch (FileNotFoundException e) {
				Reporter.log(e.toString());
			} catch (IOException e) {
				Reporter.log(e.toString(), true);
			}
			countList.put(singleFile.getAbsolutePath(), colSize);
			colSize = 0;
		}
		for (Entry<String, Integer> entry : countList.entrySet()) {
			String fileName = entry.getKey();
			Integer fileColSize = entry.getValue();

			Reporter.log("File name [" + fileName + "]  and column size is [" + fileColSize + "] \n", true);
		}

	}

	public static Integer getColoumnMapingFieldLength(String folderName, String coloumnName,Map<String,List<String>> colMaping) {

		String fieldLength = null;
		Integer fileLenthInt;
		
		for(Entry <String,List<String>> entry:colMaping.entrySet()){
			String key=entry.getKey();
			if(key.contains(coloumnName)){
				 fieldLength=entry.getValue().get(1);
				 break;
			}
		}
		fileLenthInt = Integer.parseInt(fieldLength);
		return fileLenthInt;
	}

}
