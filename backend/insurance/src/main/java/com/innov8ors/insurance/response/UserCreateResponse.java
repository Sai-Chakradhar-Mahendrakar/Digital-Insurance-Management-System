package com.innov8ors.insurance.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateResponse {
    private String id;

    private String name;

    private String email;

    private String phone;

    private String address;

    private String role;
}
