package top.itning.smp.smpinfo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.itning.smp.smpinfo.dao.ApartmentDao;
import top.itning.smp.smpinfo.entity.Apartment;
import top.itning.smp.smpinfo.exception.NullFiledException;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest
class ApartmentServiceTest {
    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private ApartmentDao apartmentDao;

    @Test
    void getAllApartments() {
        Assertions.assertNotNull(apartmentService.getAllApartments());
    }

    @Test
    void updateApartment() {
        Assertions.assertThrows(NullFiledException.class, () -> apartmentService.updateApartment(null));
        Assertions.assertThrows(NullFiledException.class, () -> apartmentService.updateApartment(new Apartment()));
        Apartment apartment = new Apartment();
        apartment.setName("测试用公寓");
        Apartment saved = apartmentDao.save(apartment);
        saved.setName("测试更新公寓名");
        apartmentService.updateApartment(saved);
        Apartment a = apartmentDao.findById(saved.getId()).orElseThrow(() -> new IllegalStateException("保存失败"));
        Assertions.assertEquals("测试更新公寓名", a.getName());
        apartmentDao.delete(a);
    }

    @Test
    void delApartment() {
        Assertions.assertThrows(NullFiledException.class, () -> apartmentService.delApartment(null));
        Assertions.assertThrows(NullFiledException.class, () -> apartmentService.delApartment(""));

        Apartment apartment = new Apartment();
        apartment.setName("测试用公寓");
        Apartment saved = apartmentDao.save(apartment);
        apartmentService.delApartment(saved.getId());
        Assertions.assertNull(apartmentDao.findByName("测试用公寓"));
    }

    @Test
    void saveApartment() {
        Assertions.assertThrows(NullFiledException.class, () -> apartmentService.saveApartment(""));
        Assertions.assertThrows(NullFiledException.class, () -> apartmentService.saveApartment(null));

        Apartment saved = apartmentService.saveApartment("测试用公寓");
        apartmentDao.deleteById(saved.getId());
        Assertions.assertNull(apartmentDao.findByName("测试用公寓"));
    }
}