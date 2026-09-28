package com.example.rentacar;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BookingFlowTest {

    @Autowired MockMvc mvc;
    @Autowired CarRepository cars;
    @Autowired CustomerRepository customers;

    private String body(Car car, Customer cust, int fromDay, int toDay) {
        return "{\"carId\":%d,\"customerId\":%d,\"startDate\":\"%s\",\"endDate\":\"%s\"}".formatted(
                car.getId(), cust.getId(), LocalDate.now().plusDays(fromDay), LocalDate.now().plusDays(toDay));
    }

    @Test
    void preventsDoubleBooking() throws Exception {
        long n = System.nanoTime();
        Car car = new Car();
        car.setMake("Test"); car.setModel("Car"); car.setYear(2024); car.setPlate("T-" + n);
        car.setCategory(CarCategory.SEDAN); car.setDailyRate(new BigDecimal("40"));
        car = cars.save(car);

        Customer cust = new Customer();
        cust.setName("Tester"); cust.setEmail(n + "@test.com"); cust.setLicenseNo("L" + n);
        cust = customers.save(cust);

        mvc.perform(post("/api/bookings").with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON).content(body(car, cust, 10, 12)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.totalPrice").value(120.0));

        mvc.perform(post("/api/bookings").with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON).content(body(car, cust, 11, 14)))
           .andExpect(status().isConflict());
    }

    @Test
    void bookingsRequireLogin() throws Exception {
        mvc.perform(get("/api/bookings")).andExpect(status().isUnauthorized());
    }

    @Test
    void fleetIsPublic() throws Exception {
        mvc.perform(get("/api/cars")).andExpect(status().isOk());
    }
}
