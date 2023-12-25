package com.hmh;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RuleTableReader {
    private static XSSFSheet sheet;
    private static final Map<String,Integer> columnNums = new HashMap<>();
    private static String alphabet;
    private static FileInputStream fis;
    private static XSSFWorkbook workbook;
    static {
        try{
            File file = new File("src/main/resources/ruleTable.xlsx");
            fis = new FileInputStream(file);
            workbook = new XSSFWorkbook(fis);

            // 获取第一个工作表
            sheet = workbook.getSheetAt(0);
        }catch (Exception e){
            System.out.println(e.toString());
        }
        alphabet = "a\tb\tc\ti\tf\t(\t)\t{\t}\t;\t0\t1\t2\t3\t4\t5\t6\t7\t8\t9\t*\t+\t=\t>\t#\tS\tI\tA\tC\tN\tV\tE\tV'\tN'\tT\tF";
        String[] tokens = alphabet.split("\t");

        int indexOfColumn = 0;
        for (int i = 0; i < tokens.length;i++) {
            if (!"23456789".contains(tokens[i])){
                indexOfColumn++;
            }
            else {
                columnNums.put(tokens[i], indexOfColumn);
                continue;
            }
            columnNums.put(tokens[i], indexOfColumn);
        }
    }
    public static String getActionStr(String currentState, String column){
        Integer rowNum = Integer.parseInt(currentState)+2;
        Integer columnNum = columnNums.get(column);
        if (columnNum==null) return null;
        Cell cell = sheet.getRow(rowNum).getCell(columnNum);
        if (cell==null) return null;
        if (cell.getCellType().equals(CellType.NUMERIC)){
            DataFormatter dataFormatter = new DataFormatter();
            return dataFormatter.formatCellValue(cell);
        }
        return cell.getStringCellValue();
    }
    public static void closeReader(){
        try {
            workbook.close();
            fis.close();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
