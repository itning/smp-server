package top.itning.smp.smpinfo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author itning
 */
@Data
public class UpFileDTO implements Serializable {
    private Integer now;
    private Integer total;
}
