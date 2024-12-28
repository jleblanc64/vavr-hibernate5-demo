package com.demo.repo;

import com.demo.model.Customer;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findAllByCity(String city);

    Option<Customer> findByName(String name);

    default List<Customer> findAllF() {
        return List.ofAll(findAll());
    }

    default Option<Customer> findByIdF(Long id) {
        return Option.ofOptional(findById(id));
    }
}
