package com.dataValidation.inputHanlder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.testng.Reporter;
import org.testng.SkipException;

import com.dataValidation.configHandler.IConstants;
import com.dataValidation.functionLibrary.UtilityClass;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ValueNode;

public class JSONHandlerClass implements IConstants {

	static Map<String, List<String>> dataInFile;

	public static Map<String, List<String>> loadJsonData(Map<String, List<String>> colNames, String folderName)
			throws JsonParseException, JsonMappingException, IOException {

		boolean sizeResult = false;
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

		if (folderName.equalsIgnoreCase(srcJSONFolderName)) {
			sizeResult = validateSourceJSONCount();
		} else {
			sizeResult = validateTargetJSONCount();
		}

		ArrayList<String> filePaths = UtilityClass.getListOfFilePath(folderName);

		File file = new File(filePaths.get(0));

		dataInFile = new HashMap<String, List<String>>();

		final JsonNode rootNode = mapper.readTree(file);

		addKeys("", rootNode, dataInFile);
		return dataInFile;

	}

	private static void addKeys(String currentPath, JsonNode jsonNode, Map<String, List<String>> map) {
		if (jsonNode.isObject()) {
			Iterator<Map.Entry<String, JsonNode>> iter = jsonNode.fields();
			String pathPrefix = currentPath.isEmpty() ? "" : currentPath + "-";

			while (iter.hasNext()) {
				Map.Entry<String, JsonNode> entry = iter.next();
				addKeys(pathPrefix + entry.getKey(), entry.getValue(), map);
			}
		} else if (jsonNode.isArray()) {
			ArrayNode arrayNode = (ArrayNode) jsonNode;

			for (int i = 0; i < arrayNode.size(); i++) {
				// suffix.add(i + 1);
				addKeys(currentPath, arrayNode.get(i), map);

				if (i + 1 < arrayNode.size()) {
					// suffix.remove(0);
				}
			}

		} else if (jsonNode.isValueNode()) {
			/*
			 * if (currentPath.contains("-")) { for (int i = 0; i < suffix.size(); i++) {
			 * currentPath += "-" + suffix.get(i); } suffix = new ArrayList<>(); }
			 */

			ValueNode valueNode = (ValueNode) jsonNode;
			currentPath = currentPath.toUpperCase();

			if (!dataInFile.containsKey(currentPath)) {
				dataInFile.put(currentPath, new ArrayList<String>());
			}
			dataInFile.get(currentPath).add(valueNode.asText());
			System.out.println(currentPath);
			System.out.println(valueNode.asText());
		}
	}

	public static Map<String, List<String>> getSourceKeyComparisonListFromExcel() {

		String colNameComparison = null;
		String colFileNameComparison = null;
		String colFieldLength = null;
		// String colTransformationRule = null;
		Map<String, List<String>> sourceKeyComparisonList = new LinkedHashMap<String, List<String>>();
		int rowCount = 1;
		int colCount = 0;
		do {
			colNameComparison = ExcelHandlerClass.getCellValue(jsonConfigSheetName, rowCount, colCount);
			colFileNameComparison = ExcelHandlerClass.getCellValue(jsonConfigSheetName, rowCount, colCount + 1);
			colFieldLength = ExcelHandlerClass.getCellValue(jsonConfigSheetName, rowCount, colCount + 2);
			// colTransformationRule = ExcelHandlerClass.getCellValue(csvConfigSheetName,
			// rowCount, colCount + 3);
			rowCount++;
			if (colNameComparison == null && colFileNameComparison == null) {
				break;
			} else {
				List<String> tempArryList = new ArrayList<String>();
				tempArryList.add(colFileNameComparison);
				tempArryList.add(colFieldLength);
				// tempArryList.add(colTransformationRule);
				sourceKeyComparisonList.put(colNameComparison, tempArryList);
			}
		} while (colNameComparison != null && colFileNameComparison != null);

		return sourceKeyComparisonList;

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
			colNameComparison = ExcelHandlerClass.getCellValue(jsonConfigSheetName, rowCount, colCount);
			colFileNameComparison = ExcelHandlerClass.getCellValue(jsonConfigSheetName, rowCount, colCount + 1);
			colFieldLength = ExcelHandlerClass.getCellValue(jsonConfigSheetName, rowCount, colCount + 2);
			colTransformationRule = ExcelHandlerClass.getCellValue(jsonConfigSheetName, rowCount, colCount - 1);
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

	public static boolean validateSourceJSONCount() {

		File[] listFile = UtilityClass.getListOfFiles(srcJSONFolderName);

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

	public static boolean validateTargetJSONCount() {

		File[] listFile = UtilityClass.getListOfFiles(targetJSONFolderName);

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
}