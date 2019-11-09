package top.itning.smp.smpinfo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.itning.smp.smpinfo.entity.RestModel;
import top.itning.smp.smpinfo.server.ApartmentService;

/**
 * @author itning
 */
@RestController
public class ApartmentController {
    private final ApartmentService apartmentService;

    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping("/apartments")
    public RestModel<?> getAllApartments() {
        return RestModel.ok(apartmentService.getAllApartments());
    }
}
