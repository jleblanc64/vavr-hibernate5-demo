package com.demo.dto;

import com.demo.model.Customer;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDtoReq {
    private Option<String> name;
    private Option<Integer> number;
    private Option<String> city;
    private List<OrderDto> orders;
    private Option<MembershipDto> membership;
    private Set<String> tags = HashSet.of();
//    private List<String> tags = List.of();

    public Customer toEntity() {
        var c = new Customer();
        c.setName(name);
        c.setNumber(number);
        c.setCity(city);
        c.setOrders(orders.map(x -> x.toEntity(c)));
        c.setMembership(membership.map(MembershipDto::toEntity));
        c.setTags(tags);

        return c;
    }
}
