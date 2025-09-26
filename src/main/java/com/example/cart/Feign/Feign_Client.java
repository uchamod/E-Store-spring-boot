package com.example.cart.Feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.UUID;

@FeignClient("PRODUCT-SERVICE")
public interface  Feign_Client {
    @PostMapping("/api/products/getTotal/{productIds}")
    public ResponseEntity<Double> getTotalAmount(@PathVariable UUID productIds);
}
