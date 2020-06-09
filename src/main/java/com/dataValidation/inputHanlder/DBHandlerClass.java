package com.dataValidation.inputHanlder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.testng.Reporter;

import com.dataValidation.configHandler.IConstants;

public class DBHandlerClass implements IConstants {

	public static Map<String, List<String>> loadDbData(Map<String, List<String>> colNames) throws SQLException {
		Map<String, List<String>> tempMap = new HashMap<String, List<String>>();
		Map<String, List<String>> data = new HashMap<String, List<String>>();

		for (Entry<String, List<String>> entrySingleColNames : colNames.entrySet()) {
			String connectionString = entrySingleColNames.getValue().get(0);
			String userName = entrySingleColNames.getValue().get(1);
			String password = entrySingleColNames.getValue().get(2);
			String dbName = entrySingleColNames.getValue().get(3);
			String tableName = entrySingleColNames.getValue().get(4);
			String filtercondition = entrySingleColNames.getValue().get(6);
			String colNameKey = entrySingleColNames.getKey();
			tempMap = connecttoDB(connectionString, userName, password, dbName, colNameKey, tableName, filtercondition);
			data.putAll(tempMap);
		}

		return data;
	}

	public static void printColumnNamesandSize(Map<String, List<String>> map) {
		// Printing all Columns & list of values
		for (Entry<String, List<String>> entrySingleColNames : map.entrySet()) {
			Reporter.log("Column name [" + entrySingleColNames.getKey() + "]  and column size is ["
					+ entrySingleColNames.getValue().size() + "] \n", true);
		}

	}

	public static Map<String, List<String>> connecttoDB(String connectionString, String userName, String password,
			String dbName, String colName, String tableName, String filtercondition) throws SQLException {
		// String URL = "jdbc:sqlserver://USBLRHLAKSHMAN4;" +
		// "user=testlogin;password=Admin123456;database=demo1";
		Map<String, List<String>> tempMap = new HashMap<String, List<String>>();
		String URL = connectionString + ";user=" + userName + ";password=" + password + ";database=" + dbName;
		Connection con = DriverManager.getConnection(URL);
		Reporter.log("Connection Successful!", true);

		// Test Query 1
		String testQuery = "";
		if (filtercondition != null) {
			testQuery = "SELECT " + colName + " FROM " + tableName + " " + filtercondition;
		} else {
			testQuery = "SELECT " + colName + " FROM " + tableName;
		}
		Reporter.log(testQuery);
		Statement statement = null;
		try {
			statement = con.createStatement();
			ResultSet rs = statement.executeQuery(testQuery);
			List<String> colValueList = new ArrayList<String>();
			while (rs.next()) {
				String colValue = rs.getString(colName).toUpperCase().replaceAll("\\s", "");
				colValueList.add(colValue);
			}
			tempMap.put(colName, colValueList);
		} catch (SQLException e) {
			Reporter.log("Exception" + e, true);
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
		return tempMap;
	}

	public static Map<String, List<String>> getSourceKeyComparisonListFromExcel() {

		String colConnectionString = null;
		String colUserName = null;
		String colPassword = null;
		String colDBName = null;
		String colTableName = null;
		String colColumnName = null;
		String colFieldLength = null;
		String colFilterCondition = null;
		// String colTransformationRule = null;
		Map<String, List<String>> sourceKeyComparisonList = new LinkedHashMap<String, List<String>>();
		int rowCount = 1;
		int colCount = 0;
		do {
			colConnectionString = ExcelHandlerClass.getCellValue(dbConfigSheetName, rowCount, colCount,
					"connectionString");
			colUserName = ExcelHandlerClass.getCellValue(dbConfigSheetName, rowCount, colCount + 1, "username");
			colPassword = ExcelHandlerClass.getCellValue(dbConfigSheetName, rowCount, colCount + 2, "password");
			colDBName = ExcelHandlerClass.getCellValue(dbConfigSheetName, rowCount, colCount + 3, "dbname");
			colTableName = ExcelHandlerClass.getCellValue(dbConfigSheetName, rowCount, colCount + 4, "null");
			colColumnName = ExcelHandlerClass.getCellValue(dbConfigSheetName, rowCount, colCount + 5, "null");
			colFilterCondition = ExcelHandlerClass.getCellValue(dbConfigSheetName, rowCount, colCount + 6, "filtercondition");
			colFieldLength = ExcelHandlerClass.getCellValue(dbConfigSheetName, rowCount, colCount + 7, "null");
			// colTransformationRule = ExcelHandlerClass.getCellValue(csvConfigSheetName,
			// rowCount, colCount + 3);
			rowCount++;
			if (colConnectionString!=null) {
				List<String> tempArryList = new ArrayList<String>();
				tempArryList.add(colConnectionString);
				tempArryList.add(colUserName);
				tempArryList.add(colPassword);
				tempArryList.add(colDBName);
				tempArryList.add(colTableName);
				tempArryList.add(colColumnName);
				tempArryList.add(colFilterCondition);
				tempArryList.add(colFieldLength);
				// tempArryList.add(colTransformationRule);
				sourceKeyComparisonList.put(colColumnName, tempArryList);
				
			} else {
				break;
			}
		} while (colConnectionString!=null);

		return sourceKeyComparisonList;

	}

	public static Map<String, List<String>> getTargetKeyComparisonListFromExcel() {
		String colConnectionString = null;
		String colUserName = null;
		String colPassword = null;
		String colDBName = null;
		String colTableName = null;
		String colColumnName = null;
		String colFieldLength = null;
		String colFilterCondition = null;
		String colTransformationRule = null;
		Map<String, List<String>> targetKeyComparisonList = new LinkedHashMap<String, List<String>>();
		int rowCount = 1;
		int colCount = 9;
		do {
			colConnectionString = ExcelHandlerClass.getCellValue(dbConfigSheetName, rowCount, colCount,
					"connectionString");
			colUserName = ExcelHandlerClass.getCellValue(dbConfigSheetName, rowCount, colCount + 1, "username");
			colPassword = ExcelHandlerClass.getCellValue(dbConfigSheetName, rowCount, colCount + 2, "password");
			colDBName = ExcelHandlerClass.getCellValue(dbConfigSheetName, rowCount, colCount + 3, "dbname");
			colTableName = ExcelHandlerClass.getCellValue(dbConfigSheetName, rowCount, colCount + 4, "null");
			colColumnName = ExcelHandlerClass.getCellValue(dbConfigSheetName, rowCount, colCount + 5, "null");
			colFilterCondition = ExcelHandlerClass.getCellValue(dbConfigSheetName, rowCount, colCount + 6, "filtercondition");
			colFieldLength = ExcelHandlerClass.getCellValue(dbConfigSheetName, rowCount, colCount + 7, "null");
			colTransformationRule = ExcelHandlerClass.getCellValue(dbConfigSheetName, rowCount, colCount - 1, "null");
			rowCount++;
			if (colConnectionString!=null) {
				List<String> tempArryList = new ArrayList<String>();
				tempArryList.add(colConnectionString);
				tempArryList.add(colUserName);
				tempArryList.add(colPassword);
				tempArryList.add(colDBName);
				tempArryList.add(colTableName);
				tempArryList.add(colColumnName);
				tempArryList.add(colFilterCondition);
				tempArryList.add(colFieldLength);
				tempArryList.add(colTransformationRule);
				targetKeyComparisonList.put(colColumnName, tempArryList);
				
			} else {
				break;
			}
		} while (colConnectionString!=null);

		return targetKeyComparisonList;

	}

}
