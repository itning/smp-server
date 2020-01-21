package top.itning.smp.smpinfo.service.impl.excel;

import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Excel某行数据
 *
 * @author itning
 * @date 2020/1/21 19:31
 */
@Getter
public class ExcelData {
    private static final Logger logger = LoggerFactory.getLogger(ExcelData.class);

    private String name;
    private String tel;
    private String email;
    private String studentId;
    private String idCard;
    private String politicalStatus;
    private String ethnic;
    private String apartmentName;
    private String roomNum;
    private String bedNum;
    private String address;

    private ExcelData(Builder builder) {
        this.name = builder.name;
        this.tel = builder.tel;
        this.email = builder.email;
        this.studentId = builder.studentId;
        this.idCard = builder.idCard;
        this.politicalStatus = builder.politicalStatus;
        this.ethnic = builder.ethnic;
        this.apartmentName = builder.apartmentName;
        this.roomNum = builder.roomNum;
        this.bedNum = builder.bedNum;
        this.address = builder.address;
    }

    public static class Builder {
        private Row row;

        private String name;
        private String tel;
        private String email;
        private String studentId;
        private String idCard;
        private String politicalStatus;
        private String ethnic;
        private String apartmentName;
        private String roomNum;
        private String bedNum;
        private String address;

        /**
         * 设置行信息
         *
         * @param row 行
         * @return Builder
         */
        public Builder setRow(Row row) {
            this.row = row;
            return this;
        }

        public ExcelData build() {
            this.name = getCellValue(row, 0);
            this.tel = getCellValue(row, 1);
            this.email = getCellValue(row, 2);
            this.studentId = getCellValue(row, 3);
            this.idCard = getCellValue(row, 4);
            this.politicalStatus = getCellValue(row, 5);
            this.ethnic = getCellValue(row, 6);
            this.apartmentName = getCellValue(row, 7);
            this.roomNum = getCellValue(row, 8);
            this.bedNum = getCellValue(row, 9);
            this.address = getCellValue(row, 10);
            return new ExcelData(this);
        }

        /**
         * 获取单元格中的数据
         *
         * @param row     行
         * @param cellNum 单元格号
         * @return 该单元格的数据
         */
        private String getCellValue(Row row, int cellNum) {
            String stringCellValue = null;
            Cell cell;
            if ((cell = row.getCell(cellNum)) != null) {
                try {
                    stringCellValue = cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    logger.debug("getCellValue::CellNum->" + cellNum + "<-尝试获取String类型数据失败");
                    try {
                        logger.debug("getCellValue::CellNum->" + cellNum + "<-尝试获取Double类型数据");
                        stringCellValue = String.valueOf(cell.getNumericCellValue());
                    } catch (IllegalStateException e1) {
                        try {
                            logger.debug("getCellValue::CellNum->" + cellNum + "<-尝试获取Boolean类型数据");
                            stringCellValue = String.valueOf(cell.getBooleanCellValue());
                        } catch (IllegalStateException e2) {
                            try {
                                logger.debug("getCellValue::CellNum->" + cellNum + "<-尝试获取Date类型数据");
                                stringCellValue = String.valueOf(cell.getDateCellValue());
                            } catch (IllegalStateException e3) {
                                logger.warn("getCellValue::CellNum->" + cellNum + "<-未知类型数据->" + e3.getMessage());
                            }
                        }
                    }
                } finally {
                    logger.debug("getCellValue::第" + cellNum + "格获取到的数据为->" + stringCellValue);
                }
            } else {
                logger.debug("getCellValue::第" + cellNum + "格为空");
            }
            return stringCellValue;
        }
    }
}
