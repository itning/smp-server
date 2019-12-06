package top.itning.smp.smpinfo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.itning.smp.smpinfo.dto.ApartmentDTO;
import top.itning.smp.smpinfo.entity.Apartment;
import top.itning.smp.smpinfo.entity.RestModel;
import top.itning.smp.smpinfo.security.LoginUser;
import top.itning.smp.smpinfo.security.MustCounselorLogin;
import top.itning.smp.smpinfo.service.ApartmentService;

import java.util.List;

/**
 * @author itning
 */
@RestController
public class ApartmentController {
    private final ApartmentService apartmentService;

    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    /**
     * 获取公寓信息
     *
     * @return 公寓信息
     */
    @GetMapping("/apartments")
    public ResponseEntity<?> getAllApartments(@MustCounselorLogin LoginUser loginUser) {
        return RestModel.ok(apartmentService.getAllApartments());
    }

    /**
     * 更新公寓信息
     *
     * @param apartment 公寓信息
     */
    @PatchMapping("/apartment")
    public ResponseEntity<?> updateApartment(@MustCounselorLogin LoginUser loginUser, @RequestBody Apartment apartment) {
        apartmentService.updateApartment(apartment);
        return RestModel.noContent();
    }

    /**
     * 删除公寓
     *
     * @param id 公寓ID
     * @return ResponseEntity
     */
    @DeleteMapping("/apartment/{id}")
    public ResponseEntity<?> delApartment(@MustCounselorLogin LoginUser loginUser, @PathVariable String id) {
        apartmentService.delApartment(id);
        return RestModel.noContent();
    }

    /**
     * 添加公寓
     *
     * @param name 公寓信息
     * @return 新创建的公寓
     */
    @PostMapping("/apartment")
    public ResponseEntity<?> addApartment(@MustCounselorLogin LoginUser loginUser, @RequestParam String name) {
        return RestModel.created(apartmentService.saveApartment(name));
    }

    /**
     * 获取所有公寓信息
     *
     * @return 公寓信息集合
     */
    @GetMapping("/internal/apartments")
    public List<ApartmentDTO> getAllApartments() {
        return apartmentService.getAllApartmentsWithPeople();
    }
}
