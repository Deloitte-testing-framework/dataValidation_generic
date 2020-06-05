package com.dataValidation.inputHanlder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.Reporter;

import com.dataValidation.configHandler.IConstants;

public class ExcelHandlerClass implements IConstants {

	public static String getCellValue(String sheetName, int row, int col) {

		Cell value = null;
		String strValue = "";

		try {
			Workbook wb = WorkbookFactory.create(new FileInputStream(System.getProperty("user.dir") + configExcelPath));
			value = wb.getSheet(sheetName).getRow(row).getCell(col);
			value.setCellType(Cell.CELL_TYPE_STRING);
			strValue = value.toString();
		} catch (InvalidFormatException e) {
			Reporter.log(e.toString(), true);
		} catch (IllegalStateException e) {
			Reporter.log(e.toString(), true);
		}
		catch (FileNotFoundException e) {
			Reporter.log(e.toString() + "File path " + configExcelPath, true);
		} catch (IOException e) {
			Reporter.log(e.toString(), true);
		} catch (NullPointerException e) {
			return null;
		}

		return strValue.replaceAll("\\s", "").toUpperCase();

	}

}
