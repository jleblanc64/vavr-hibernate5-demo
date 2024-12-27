package com.demo.dto;

import com.demo.model.Customer;
import com.demo.model.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderDto {
    private String description;

    public OrderDto(Order o) {
        description = o.getDescription();
    }

    public Order toEntity(Customer c) {
        var o = new Order();
        o.setCustomer(c);
        o.setDescription(description);
        return o;
    }
}
