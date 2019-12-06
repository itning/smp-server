package top.itning.smp.smpstatistics.entity;

import lombok.Data;
import top.itning.smp.smpstatistics.client.entity.User;

import java.io.Serializable;

/**
 * @author itning
 */
@Data
public class AllCounselor implements Serializable {
    private User user;
    private ClassComing classComing;
    private HomeComing homeComing;
    private Leave leave;
}
