package com.group_three.food_ordering.dtos.update;
import jakarta.validation.Valid;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPatchDto {

    private String name;
    private String lastName;
    private String phone;
    private LocalDate birthDate;

    @Valid
    private AddressUpdateDto address;
}