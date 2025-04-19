package com.demo;

import com.demo.dto.CustomerDtoReqSub;
import com.demo.dto.CustomerDtoResp;
import com.demo.repo.CustomerRepository;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    @Value("${array.prop}")
    java.util.List<String> props;
    @Autowired
    CustomerRepository customerRepository;


    @GetMapping
    public List<CustomerDtoResp> getCustomers(@RequestParam(required = false) Option<String> city) {
        var customers = city.fold(customerRepository::findAllF, customerRepository::findAllByCity);
        return customers.map(CustomerDtoResp::new);
    }

    @GetMapping("/props")
    public List<String> getProps() {
        return List.ofAll(props);
    }

    @PostMapping
    public CustomerDtoResp createCustomer(@RequestBody CustomerDtoReqSub customer) {
        var cust = customerRepository.save(customer.toEntity());
        return new CustomerDtoResp(cust);
    }

    @PostMapping("/batch")
    public List<CustomerDtoResp> createCustomers(@RequestBody List<CustomerDtoReqSub> customers) {
        return customers.map(this::createCustomer);
    }

    @PostMapping("/batchJ")
    public List<CustomerDtoResp> createCustomers(@RequestBody java.util.List<CustomerDtoReqSub> customers) {
        return List.ofAll(customers).map(this::createCustomer);
    }

    @GetMapping("/{id}")
    public CustomerDtoResp getCustomerById(@PathVariable(value = "id") Long customerId) {
        var cust = customerRepository.findByIdF(customerId).getOrElseThrow(NotFoundException::new);
        return new CustomerDtoResp(cust);
    }

    @GetMapping("/by-name")
    CustomerDtoResp getCustomerByName(@RequestParam String name) {
        var cust = customerRepository.findByName(name).getOrElseThrow(NotFoundException::new);
        return new CustomerDtoResp(cust);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable(value = "id") Long customerId) {
        var cust = customerRepository.findByIdF(customerId).getOrElseThrow(NotFoundException::new);
        customerRepository.delete(cust);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    private static class NotFoundException extends RuntimeException {
    }
}