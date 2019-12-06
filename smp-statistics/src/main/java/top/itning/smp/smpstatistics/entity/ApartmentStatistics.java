package top.itning.smp.smpstatistics.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author itning
 */
@Data
public class ApartmentStatistics implements Serializable {
    /**
     * 公寓名
     */
    private String name;
    /**
     * 人数
     */
    private long people;
}
