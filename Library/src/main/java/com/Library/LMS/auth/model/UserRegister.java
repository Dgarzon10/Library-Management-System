package com.Library.LMS.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegister {

    private String name;
    private String email;
    private String password;
    private String libraryId;
}
