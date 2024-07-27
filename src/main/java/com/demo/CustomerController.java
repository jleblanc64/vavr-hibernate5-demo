package com.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.demo.functional.ListF.f;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    CustomerRepository customerRepository;

    @GetMapping
    public List<CustomerDtoResp> getCustomers() {
        return f(customerRepository.findAll()).map(CustomerDtoResp::new);
    }

    @PostMapping
    public CustomerDtoResp createCustomer(@Valid @RequestBody CustomerDtoReq customer) {
        var c = customerRepository.save(customer.toCustomer());
        return new CustomerDtoResp(c);
    }

    @GetMapping("/{id}")
    public CustomerDtoResp getCustomerById(@PathVariable(value = "id") Long customerId) {
        var c = customerRepository.findById(customerId).orElseThrow(NotFoundException::new);
        return new CustomerDtoResp(c);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable(value = "id") Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(NotFoundException::new);
        customerRepository.delete(customer);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    private static class NotFoundException extends RuntimeException {
    }
}