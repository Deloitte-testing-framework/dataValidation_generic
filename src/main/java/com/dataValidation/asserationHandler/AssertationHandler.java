package com.dataValidation.asserationHandler;

import org.testng.Reporter;
import org.testng.asserts.SoftAssert;

public class AssertationHandler {

	public SoftAssert softAssert;

	public AssertationHandler() {
		softAssert = new SoftAssert();
	}

	public void assertAll() {
		softAssert.assertAll();
	}

	public void assertEquals(String actual, String expected, String failMessage, String passMessage) {
		
		softAssert.assertEquals(actual, expected, passMessage);
		if (!passMessage.equalsIgnoreCase("")) {
			Reporter.log("Assertion Passed " + passMessage, true);
		} else {
			Reporter.log("Assertion Failed " + failMessage, true);
		}
	}
}
