package com.demo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

import static java.util.Optional.empty;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDtoReq {
    private Optional<String> name = empty();

    public Customer toCustomer() {
        var c = new Customer();
        c.setName(name);
        return c;
    }
}
