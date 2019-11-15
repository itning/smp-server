package top.itning.smp.smpclass.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author itning
 */
@Data
public class StudentGroupPrimaryKey implements Serializable {
    /**
     * StudentGroup ID
     */
    private String user;
    /**
     * studentClass ID
     */
    private String studentClass;
}
