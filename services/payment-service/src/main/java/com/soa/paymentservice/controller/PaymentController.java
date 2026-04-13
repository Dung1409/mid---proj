package com.soa.paymentservice.controller;

import com.soa.paymentservice.dto.ProcessPaymentRequest;
import com.soa.paymentservice.dto.ProcessPaymentResponse;
import com.soa.paymentservice.entity.PaymentEntity;
import com.soa.paymentservice.repository.PaymentRepository;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @PostMapping
    public ResponseEntity<ProcessPaymentResponse> processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        String paymentStatus = ThreadLocalRandom.current().nextBoolean() ? "SUCCESS" : "FAILED";
        paymentRepository.save(new PaymentEntity(
                request.getOrderId(),
                request.getAmount(),
                paymentStatus,
                Instant.now()));
        return ResponseEntity.ok(new ProcessPaymentResponse(paymentStatus));
    }
}