package com.ql.BlogApplication.documents;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String otp;

    private Date otpGeneratedTime;

    private Boolean emailVerified = false;

    @DBRef
    private Role role;

    // We'll handle relationships differently in MongoDB
    // One approach is just to store the role ID here
    // private String roleId;
}