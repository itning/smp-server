package top.itning.smp.smpinfo.server.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itning.smp.smpinfo.dao.ApartmentDao;
import top.itning.smp.smpinfo.entity.Apartment;
import top.itning.smp.smpinfo.server.ApartmentService;

import java.util.List;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ApartmentServiceImpl implements ApartmentService {
    private final ApartmentDao apartmentDao;

    public ApartmentServiceImpl(ApartmentDao apartmentDao) {
        this.apartmentDao = apartmentDao;
    }

    @Override
    public List<Apartment> getAllApartments() {
        return apartmentDao.findAll();
    }
}
