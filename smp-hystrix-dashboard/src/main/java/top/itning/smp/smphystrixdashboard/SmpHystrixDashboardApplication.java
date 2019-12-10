package top.itning.smp.smphystrixdashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

/**
 * @author itning
 */
@SpringBootApplication
@EnableHystrixDashboard
@EnableTurbine
public class SmpHystrixDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmpHystrixDashboardApplication.class, args);
    }

}
