package top.itning.smp.smproom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.itning.smp.smproom.dao.StudentRoomCheckMetaDataDao;
import top.itning.smp.smproom.entity.StudentRoomCheckMetaData;

@SpringBootTest
class SmpRoomApplicationTests {
    @Autowired
    private StudentRoomCheckMetaDataDao studentRoomCheckMetaDataDao;

    @Test
    void contextLoads() {
        String counselorId = "1";
        /*if (!studentRoomCheckMetaDataDao.existsById(new StudentRoomCheckMetaDataPrimaryKey(StudentRoomCheckMetaData.KEY_ROOM_CHECK_TIME, counselorId))) {
            StudentRoomCheckMetaData studentRoomCheckMetaData1 = new StudentRoomCheckMetaData();
            studentRoomCheckMetaData1.setKey(StudentRoomCheckMetaData.KEY_ROOM_CHECK_TIME);
            studentRoomCheckMetaData1.setValue("20:30");
            studentRoomCheckMetaData1.setBelongCounselorId(counselorId);
            studentRoomCheckMetaDataDao.save(studentRoomCheckMetaData1);
        }*/
        StudentRoomCheckMetaData metaData = studentRoomCheckMetaDataDao.findByKeyAndBelongCounselorId(StudentRoomCheckMetaData.KEY_ROOM_CHECK_GPS_RANGE, counselorId);
        System.out.println(metaData);
    }

}
