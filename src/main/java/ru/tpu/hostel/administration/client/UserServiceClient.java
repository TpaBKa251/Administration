package ru.tpu.hostel.administration.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@Component
@FeignClient(name = "userServiceClient", url = "http://localhost:8081/users")
public interface UserServiceClient {

    @GetMapping("/get_by_id}")
    ResponseEntity<?> getUserById(UUID id);
}