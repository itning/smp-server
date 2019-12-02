package top.itning.smp.smpinfo.service;

import top.itning.smp.smpinfo.entity.Apartment;

import java.util.List;

/**
 * @author itning
 */
public interface ApartmentService {
    /**
     * 获取所有公寓信息
     *
     * @return 公寓集合
     */
    List<Apartment> getAllApartments();

    /**
     * 更新公寓信息
     *
     * @param apartment 公寓信息
     */
    void updateApartment(Apartment apartment);

    /**
     * 删除公寓
     *
     * @param id 公寓ID
     */
    void delApartment(String id);

    /**
     * 新增公寓
     *
     * @param name 公寓
     * @return 新增的公寓
     */
    Apartment saveApartment(String name);
}
