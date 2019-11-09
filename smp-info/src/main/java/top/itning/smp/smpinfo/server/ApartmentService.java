package top.itning.smp.smpinfo.server;

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
}
