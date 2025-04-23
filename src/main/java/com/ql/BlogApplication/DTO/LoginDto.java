package com.ql.BlogApplication.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    @NotEmpty
    @Valid
    private String email;
    @NotEmpty
    private String password;
}
