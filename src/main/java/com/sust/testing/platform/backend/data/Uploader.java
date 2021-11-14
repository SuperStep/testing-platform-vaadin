package com.sust.testing.platform.backend.data;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Uploader {

    public Uploader(){
    }

    /**
     *
     * @param buffer Excel file to parse
     * @return QuestionSet - ready to persist set of questions and influence map
     */
    public List<QuestionSet> parseFile(MemoryBuffer buffer) {
        if (buffer.getFileName() != null){
            try {
                Workbook wb = new XSSFWorkbook(buffer.getInputStream());
                Sheet sheet = wb.getSheetAt(0);
                Row headerRow = sheet.getRow(0);

                // Getting headers
                List<String> columns = new ArrayList<>();
                for (Cell cell : headerRow) {
                    if (cell.getCellTypeEnum() == CellType.STRING){
                        columns.add(cell.getStringCellValue());
                    }
                }

                // Getting data
                List<QuestionSet> questionSets = new ArrayList<>();
                for (Row row : sheet) {
                    QuestionSet questionSet = new QuestionSet();
                    Map<String, Integer> influenceMap = new HashMap<>();
                    if (row.getRowNum() == 0){
                        continue;
                    }
                    for (int y=0; y<columns.size(); y++){
                        Cell cell = row.getCell(y);
                        if (cell == null) {
                            continue;
                        }
                        String colName = columns.get(y);
                        switch (colName) {
                            case "test": questionSet.setTestName(cell.getStringCellValue()); break;
                            case "question_text": questionSet.setQuestionText(cell.getStringCellValue()); break;
                            case "position": questionSet.setPosition((int) cell.getNumericCellValue()); break;
                            case "answer": questionSet.setAnswer(cell.getStringCellValue()); break;
                            default: influenceMap.put(colName, (int)cell.getNumericCellValue());
                        }
                    }
                    questionSet.setInfluenceMap(influenceMap);
                    questionSets.add(questionSet);
                }

                return questionSets;

            } catch (Exception e) {
                Notification.show(e.getMessage());
                return null;
            }

        } else {
            return null;
        }
    }

}
