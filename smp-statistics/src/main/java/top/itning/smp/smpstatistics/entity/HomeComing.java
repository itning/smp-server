package top.itning.smp.smpstatistics.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author itning
 */
@Data
public class HomeComing implements Serializable {
    /**
     * 总人数
     */
    private long sum;
    /**
     * 归寝数
     */
    private long coming;
}
