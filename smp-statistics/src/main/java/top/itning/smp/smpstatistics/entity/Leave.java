package top.itning.smp.smpstatistics.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author itning
 */
@Data
public class Leave implements Serializable {
    /**
     * 总人数
     */
    private long sum;
    /**
     * 请假数
     */
    private long leave;
}
