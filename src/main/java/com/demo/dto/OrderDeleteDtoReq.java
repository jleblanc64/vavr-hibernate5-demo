package com.demo.dto;

import io.vavr.collection.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDeleteDtoReq {
    private Set<String> descriptions;
}
