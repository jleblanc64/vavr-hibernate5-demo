package com.demo;

import com.demo.dto.OrderDeleteDtoReq;
import com.demo.dto.OrderDeleteDtoResp;
import com.demo.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderRepository orderRepository;

    @PostMapping
    public OrderDeleteDtoResp deleteByDescriptions(@RequestBody OrderDeleteDtoReq req) {
        req.getDescriptions().forEach(desc -> {
            var orders = orderRepository.findAllByDescription(desc);
            orderRepository.deleteAll(orders);
        });

        return new OrderDeleteDtoResp(req.getDescriptions());
    }
}