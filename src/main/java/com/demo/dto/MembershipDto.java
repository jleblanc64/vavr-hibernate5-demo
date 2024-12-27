package com.demo.dto;

import com.demo.model.Membership;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MembershipDto {
    private String description;

    public MembershipDto(Membership m) {
        description = m.getDescription();
    }

    public Membership toEntity() {
        var m = new Membership();
        m.setDescription(description);
        return m;
    }
}
