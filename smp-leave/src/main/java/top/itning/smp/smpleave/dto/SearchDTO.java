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
    private String key;
    private Boolean effective;
    private Date startTime;
    private Date endTime;
    private LeaveType leaveType;
}
