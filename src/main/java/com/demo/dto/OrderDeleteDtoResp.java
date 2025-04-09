package com.demo.dto;

import io.vavr.collection.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDeleteDtoResp {
    private Set<String> descriptions;
}
