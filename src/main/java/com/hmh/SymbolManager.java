package com.hmh;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolManager {
    //String const
    static final String SYMBOL_TABLE = "symbolTable";
    static final String CONST_TABLE = "constTable";
    static final String KEYWORD_TABLE = "keywordTable";
    static final String VARIABLE_TABLE = "variableTable";
    //FileInputStream and workbook
    private static FileInputStream symbolTableFis;
    private static XSSFWorkbook symbolTableWorkbook;
    private static final String filePath = "src/main/resources/symbolTable.xlsx";
    //sheets
    private static Map<String, XSSFSheet> sheets = new HashMap<>();
    //symbol information
    private static String symbolName="";//只有variable用symbolName
    private static SymbolType symbolType;
    private static Integer symbolPointer=0;
    private static String symbolValue="";

    static {
        try{
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            XSSFWorkbook workbook = new XSSFWorkbook();
            // 写入表头
            XSSFSheet sheet1 = workbook.createSheet("Sheet1");
            Row row1 = sheet1.createRow(0);
            row1.createCell(0).setCellValue("class");
            row1.createCell(1).setCellValue("ptr");

            XSSFSheet sheet2 = workbook.createSheet("Sheet2");
            Row row2 = sheet2.createRow(0);
            row2.createCell(0).setCellValue("const_value");

            XSSFSheet sheet3 = workbook.createSheet("Sheet3");
            Row row3 = sheet3.createRow(0);
            row3.createCell(0).setCellValue("keyword");

            XSSFSheet sheet4 = workbook.createSheet("Sheet4");
            Row row4 = sheet4.createRow(0);
            row4.createCell(0).setCellValue("variable_name");
            row4.createCell(1).setCellValue("type");

            // 创建 FileOutputStream 对象
            FileOutputStream fos = new FileOutputStream(filePath);
            // 将 Workbook 写入到文件中
            workbook.write(fos);
            fos.close();
            workbook.close();
            symbolTableFis = new FileInputStream(filePath);
            symbolTableWorkbook = new XSSFWorkbook(symbolTableFis);
            sheets.put(SYMBOL_TABLE,symbolTableWorkbook.getSheetAt(0));
            sheets.put(CONST_TABLE,symbolTableWorkbook.getSheetAt(1));
            sheets.put(KEYWORD_TABLE,symbolTableWorkbook.getSheetAt(2));
            sheets.put(VARIABLE_TABLE,symbolTableWorkbook.getSheetAt(3));
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public static void setSymbolType(SymbolType symbolType) {
        SymbolManager.symbolType = symbolType;
    }
    public static void addBehindSymbolName(String toBeAppend) {
        symbolName = symbolName+toBeAppend;
    }
    public static void cleanSymbolName() {
        SymbolManager.symbolName = "";
    }
    public static void cleanSymbolValue() {
        SymbolManager.symbolValue = "";
    }
    public static void addBehindSymbolValue(String toBeAppend) {
        symbolValue = symbolValue+toBeAppend;
    }
    public static Integer getSymbolValue() {
        return Integer.parseInt(symbolValue);
    }

    public static void addToSymbolTable(){
        XSSFSheet sheet = sheets.get(SYMBOL_TABLE);
        int lastRowNum = sheet.getLastRowNum(); // 获取最后一行编号
        Row newRow = sheet.createRow(lastRowNum + 1); // 创建新行

        Cell cell1 = newRow.createCell(0); // 第一列 class
        cell1.setCellValue(symbolType.name());

        Cell cell2 = newRow.createCell(1); // 第二列 ptr
        List<String> datas = new ArrayList<>();
        String sheetName="";
        switch (symbolType){
            case CONST -> {
                datas.add(symbolValue);
                sheetName = CONST_TABLE;
            }
            case KEYWORD -> {
                datas.add(symbolValue);
                sheetName = KEYWORD_TABLE;
            }
            case VARIABLE -> {
                datas.add(symbolName);
                datas.add("int");
                sheetName = VARIABLE_TABLE;
            }
        }
        Integer pointer = addToCorrespondTable(sheetName,datas);
        cell2.setCellValue(String.valueOf(pointer));
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            symbolTableWorkbook.write(fos);
        }catch (IOException e) {
            throw new RuntimeException();
        }
        finally {
            cleanSymbolName();
            cleanSymbolValue();
        }
    }

    private static Integer addToCorrespondTable(String sheetName,List<String> datas){
        if (sheetName.equals("")) return 0;
        XSSFSheet sheet = sheets.get(sheetName);
        String str = datas.get(0);
        Integer pointer = isExist(str,sheetName);
        if (!pointer.equals(0)) return pointer;//对应子表中已存在
        //以下是子表中不存在
        int lastRowNum = sheet.getLastRowNum(); // 获取最后一行编号
        Row newRow = sheet.createRow(++lastRowNum); // 创建新行
        for (int i = 0; i < datas.size(); i++) {
            newRow.createCell(i).setCellValue(datas.get(i));
        }
        return lastRowNum;
    }
    /*
    If exists then return the ptr.If not return 0.
    str could be the name of the variable or the value of the const.
    */
    public static Integer isExist(String str,String sheetName){
        Integer constValue = 0;
        if (sheetName.equals("num")) constValue = Integer.parseInt(str);
        XSSFSheet sheet = sheets.get(sheetName);
        if (sheet==null) throw new RuntimeException();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null && row.getCell(0) != null) { // 检查单元格是否为空
                Cell cell = row.getCell(0);
                String cellContent="";
                if (cell.getCellType().equals(CellType.NUMERIC)){
                    DataFormatter dataFormatter = new DataFormatter();
                    cellContent =  dataFormatter.formatCellValue(cell);
                }else {
                    cellContent = cell.getStringCellValue();

                }
                if (str.equals(cellContent)) return i;
            }
        }
        return 0;
    }
    public static void close(){
        try {
            symbolTableFis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
enum SymbolType{
    CONST,KEYWORD,VARIABLE
}