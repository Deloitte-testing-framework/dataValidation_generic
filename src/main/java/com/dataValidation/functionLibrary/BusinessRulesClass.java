package com.dataValidation.functionLibrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.testng.Reporter;
import org.testng.SkipException;

import com.dataValidation.configHandler.IConstants;
import com.dataValidation.inputHanlder.ExcelHandlerClass;

public class BusinessRulesClass implements IConstants {
	static Map<String, List<String>> modifiedData = new HashMap<String, List<String>>();
	static List<String> srcKeyList;
	static List<String> targetKeyList;

	public static Map<String, List<String>> applyBusinessRule(Map<String, List<String>> srcData,
			Map<String, List<String>> srcColMaping, Map<String, List<String>> targetColMaping) {

		srcKeyList = Arrays.asList(UtilityClass.getAllKeyOfMap(srcColMaping).split(","));
		targetKeyList = Arrays.asList(UtilityClass.getAllKeyOfMap(targetColMaping).split(","));
		for (Entry<String, List<String>> entry : targetColMaping.entrySet()) {
			String colMapingTransformationRuleValue = entry.getValue().get(2);

			if (colMapingTransformationRuleValue.equalsIgnoreCase("DirectMapping")) {
				String colMapingKey = entry.getKey();
				Integer indexValue = targetKeyList.indexOf(colMapingKey);
				String srcKey = srcKeyList.get(indexValue);
				List<String > str=srcData.get(srcKey);
				System.out.println(str);
				modifiedData.put(colMapingKey, str);
			} else {
				switch (colMapingTransformationRuleValue) {
				case "RULE1":
					modifiedData.putAll(ruleOne(srcData));
					break;

				case "RULE2":
					modifiedData.putAll(ruleTwo(srcData));
					break;

				default:
					Reporter.log("Business Rule was not found", true);
				}

			}
		}

		return modifiedData;

	}

	public static Map<String, List<String>> ruleOne(Map<String, List<String>> srcData) {

		String forecastedCases = ExcelHandlerClass.getCellValue(businessRulesSheetName, 1, 1);
		String overriddenCases = ExcelHandlerClass.getCellValue(businessRulesSheetName, 1, 2);
		String targetColName = ExcelHandlerClass.getCellValue(businessRulesSheetName, 1,
				businessRuleTargetColumnNumber);
		List<String> modifiedCases = new ArrayList<String>();
		Map<String, List<String>> tmpModifiedSrcData = new HashMap<String, List<String>>();

		if (srcData.containsKey(forecastedCases) && srcData.containsKey(overriddenCases)) {
			List<String> foreCastedCasesList = srcData.get(forecastedCases);
			List<String> overriddenCasesList = srcData.get(overriddenCases);
			if (foreCastedCasesList.size() == overriddenCasesList.size()) {
				for (int i = 0; i < foreCastedCasesList.size(); i++) {
					String singleForecastedCases = foreCastedCasesList.get(i);
					String singleOverriddenCases = overriddenCasesList.get(i);
					if (singleOverriddenCases.equalsIgnoreCase("0")) {
						modifiedCases.add(singleForecastedCases);
					} else {
						modifiedCases.add(singleOverriddenCases);
					}
				}
				tmpModifiedSrcData.put(targetColName, modifiedCases);

			}

		} else {

			throw new SkipException("Business rule can't be applied since column name  [" + forecastedCases + "] and ["
					+ overriddenCases + "] is not found in source");

		}
		return tmpModifiedSrcData;

	}

	public static Map<String, List<String>> ruleTwo(Map<String, List<String>> srcData) {

		Map<String, List<String>> tmpCases = ruleOne(srcData);
		String targetColName = ExcelHandlerClass.getCellValue(businessRulesSheetName, 2,
				businessRuleTargetColumnNumber);
		String vendorPack = ExcelHandlerClass.getCellValue(businessRulesSheetName, 2, 3);
		Map<String, List<String>> tmpModifiedSrcData = new HashMap<String, List<String>>();
		List<String> modifiedVendorPackCostAmt = new ArrayList<String>();

		if (srcData.containsKey(vendorPack)) {
			List<String> vendorPackList = srcData.get(vendorPack);
			for (Entry<String, List<String>> entry : tmpCases.entrySet()) {

				if (entry.getValue().size() == vendorPackList.size()) {

					for (int i = 0; i < vendorPackList.size(); i++) {
						String cases = entry.getValue().get(i);
						String singleVendorPack = vendorPackList.get(i);
						modifiedVendorPackCostAmt
								.add(Double.toString(Double.parseDouble(cases) * Double.parseDouble(singleVendorPack)));
					}

				}
				tmpModifiedSrcData.put(targetColName, modifiedVendorPackCostAmt);
			}
		} else {
			throw new SkipException(
					"Business rule can't be applied since column name  [" + vendorPack + "]  is not found in source");
		}
		return tmpModifiedSrcData;

	}

}