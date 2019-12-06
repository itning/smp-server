package top.itning.smp.smpinfo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author itning
 */
@Data
public class ApartmentDTO implements Serializable {
    /**
     * 公寓名
     */
    private String name;
    /**
     * 人数
     */
    private Long people;
}
