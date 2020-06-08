package dataValidation.TestClass;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.testng.Reporter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.dataValidation.asserationHandler.AssertationHandler;
import com.dataValidation.configHandler.IConstants;
import com.dataValidation.functionLibrary.BusinessRulesClass;
import com.dataValidation.functionLibrary.UtilityClass;
import com.dataValidation.inputHanlder.CSVHandlerClass;
import com.dataValidation.inputHanlder.DBHandlerClass;
import com.dataValidation.inputHanlder.JSONHandlerClass;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ValidationTest extends AssertationHandler implements IConstants {
	@Test
	@Parameters({ "comparison", "sourceType", "targetType", "separatorTarget", "separatorSource" })
	public void democomparingSrcTarget(@Optional("ROW") String comparison, String sourceType, String targetType,
			@Optional(",") String separatorTarget, @Optional(",") String separatorSource)
			throws JsonParseException, JsonMappingException, IOException {
		Map<String, List<String>> srcColNamesComparisionList = null;
		Map<String, List<String>> targetColNamesComparisionList = null;
		Map<String, List<String>> targetData = null;
		Map<String, List<String>> rawSrcData = null;
		Map<String, List<String>> processedSrcData = null;
		switch (sourceType) {
		case "CSV":
			srcColNamesComparisionList = CSVHandlerClass.getSourceKeyComparisonListFromExcel();
			rawSrcData = CSVHandlerClass.loadCsvData(srcColNamesComparisionList, srcCSVFolderName, separatorSource);
			CSVHandlerClass.printCountOfColumnsinFile(srcCSVFolderName);
			break;

		case "JSON":
			srcColNamesComparisionList = JSONHandlerClass.getSourceKeyComparisonListFromExcel();
			rawSrcData = JSONHandlerClass.loadJsonData(srcColNamesComparisionList, srcJSONFolderName);
			JSONHandlerClass.printCountOfColumnsinFile(srcJSONFolderName);
			break;
			
		case "DB":
			srcColNamesComparisionList = DBHandlerClass.getSourceKeyComparisonListFromExcel();
			try {
				rawSrcData = DBHandlerClass.loadDbData(srcColNamesComparisionList);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DBHandlerClass.printColumnNamesandSize(rawSrcData);
			break;
			
		default:
			Reporter.log("Source type was not found", true);
		}
		switch (targetType) {
		case "CSV":
			targetColNamesComparisionList = CSVHandlerClass.getTargetKeyComparisonListFromExcel();
			targetData = CSVHandlerClass.loadCsvData(targetColNamesComparisionList, targetCSVFolderName,
					separatorTarget);
			CSVHandlerClass.printCountOfColumnsinFile(targetCSVFolderName);
			break;
		case "JSON":
			targetColNamesComparisionList = JSONHandlerClass.getTargetKeyComparisonListFromExcel();
			targetData = JSONHandlerClass.loadJsonData(targetColNamesComparisionList, targetJSONFolderName);
			JSONHandlerClass.printCountOfColumnsinFile(targetJSONFolderName);
			break;
		case "DB":
			targetColNamesComparisionList = DBHandlerClass.getTargetKeyComparisonListFromExcel();
			try {
				targetData = DBHandlerClass.loadDbData(targetColNamesComparisionList);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DBHandlerClass.printColumnNamesandSize(targetData);
			break;
		default:
			Reporter.log("Target type was not found", true);
		}

		processedSrcData = BusinessRulesClass.applyBusinessRule(rawSrcData, srcColNamesComparisionList,
				targetColNamesComparisionList);

		List<String> srcKeyList = Arrays.asList(UtilityClass.getAllKeyOfMap(srcColNamesComparisionList).split(","));
		List<String> targetKeyList = Arrays
				.asList(UtilityClass.getAllKeyOfMap(targetColNamesComparisionList).split(","));
		List<String> srcValuesListRowWise = new ArrayList<String>();
		List<String> targetValuesListRowWise = new ArrayList<String>();

		if (comparison.equalsIgnoreCase("ROW")) {
			String singleValue = "";
			String separtor = "";
			for (int i = 0; i < processedSrcData.get(targetKeyList.get(0)).size(); i++) {
				for (String singleSrckey : targetKeyList) {
					List<String> srcmapValues = processedSrcData.get(singleSrckey);
					if (!singleValue.equalsIgnoreCase("")) {
						separtor = ",";
					} else {
						separtor = "";
					}
					singleValue = singleValue + separtor + srcmapValues.get(i);

				}
				srcValuesListRowWise.add(singleValue);
				singleValue = "";
			}
			for (int i = 0; i < targetData.get(targetKeyList.get(0)).size(); i++) {

				for (String singleTargetKey : targetKeyList) {
					List<String> targetMapValues = targetData.get(singleTargetKey);
					if (!singleValue.equalsIgnoreCase("")) {
						separtor = ",";
					} else {
						separtor = "";
					}
					singleValue = singleValue + separtor + targetMapValues.get(i);

				}
				targetValuesListRowWise.add(singleValue);
				singleValue = "";
			}

			for (String singleTargetValue : targetValuesListRowWise) {
				if (srcValuesListRowWise.contains(singleTargetValue)) {
					Integer indexValue = srcValuesListRowWise.indexOf(singleTargetValue);
					assertEquals(singleTargetValue, srcValuesListRowWise.get(indexValue), "",
							"The data [" + singleTargetValue + "] was found in the source");
				} else {

					assertEquals(singleTargetValue, "",
							"The data [" + singleTargetValue + "] was not found in the source", "");
				}
			}
			assertAll();

		} else {

			for (int i = 0; i < targetKeyList.size(); i++) {

				String targetKey = targetKeyList.get(i);
				List<String> srcValueList = processedSrcData.get(targetKey);
				List<String> targetValueList = targetData.get(targetKey);

				for (String singleTargetValue : targetValueList) {
					if (srcValueList.contains(singleTargetValue)) {
						Integer indexValue = srcValueList.indexOf(singleTargetValue);
						assertEquals(singleTargetValue, srcValueList.get(indexValue), "",
								"Column name is [" + targetKey + "]  value of the [" + targetKey + "] is ["
										+ singleTargetValue + "]  and it was found in the source");
					} else {
						assertEquals(singleTargetValue, "", "Column name is [" + targetKey + "]  value of the ["
								+ targetKey + "] is [" + singleTargetValue + "]  and it was found in the source", "");
					}
				}

			}
			assertAll();

		}

	}
}
