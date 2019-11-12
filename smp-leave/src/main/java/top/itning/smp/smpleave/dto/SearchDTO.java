package top.itning.smp.smpleave.dto;

import lombok.Data;
import top.itning.smp.smpleave.entity.LeaveType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author itning
 */
@Data
public class SearchDTO implements Serializable {
    /**
     * 关键字
     */
    private String key;
    /**
     * 开始日期
     */
    private Date startTime;
    /**
     * 结束日期
     */
    private Date endTime;
    /**
     * 请假类型
     */
    private LeaveType leaveType;
    /**
     * 假期中
     */
    private Boolean end;
}
