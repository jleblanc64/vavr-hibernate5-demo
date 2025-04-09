package com.demo.repo;

import com.demo.model.Order;
import io.vavr.collection.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByDescription(String d);
}
