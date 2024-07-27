package com.demo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDtoReq {
    private Optional<String> name;

    public Customer toCustomer() {
        var c = new Customer();
        c.setName(name);
        return c;
    }
}
