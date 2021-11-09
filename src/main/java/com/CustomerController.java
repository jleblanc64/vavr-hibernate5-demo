package com;

import javax.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CustomerController {

    @Autowired
    CustomerRepository customerRepository;

    @GetMapping("/customers")
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @PostMapping("/customers")
    public Customer createCustomer(@Valid @RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    @GetMapping("/customers/{id}")
    public Customer getCustomerById(@PathVariable(value = "id") Long noteId) {
        return customerRepository.findById(noteId).orElseThrow(NotFoundException::new);
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable(value = "id") Long noteId) {
        Customer note = customerRepository.findById(noteId).orElseThrow(NotFoundException::new);
        customerRepository.delete(note);

        return ResponseEntity.ok().build();
    }
}
