package top.itning.smp.smproom.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author itning
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentRoomCheckMetaDataPrimaryKey implements Serializable {
    private String key;
    private String belongCounselorId;
}
