//package com.ql.BlogApplication.DTO;
//
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotEmpty;
//import jakarta.validation.constraints.Pattern;
//import jakarta.validation.constraints.Size;
//
//// Inner class just for testing validation
//public class UserRequestDto {
//    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only alphabets")
//    @NotEmpty(message = "Name must not be empty")
//    @Size(min = 3, max = 100, message = "Name must be between 3 to 10 characters")
//    private String name;
//
//    @Email(message = "Email should be valid")
//    @NotEmpty(message = "Email must not be empty")
//    private String email;
//
//    //TODO: ADD REGEX for strong passwrod
//    @NotEmpty(message = "Password must not be empty")
//    @Size(min = 6, message = "Password must be at least 6 characters long")
//    private String password;
//
//    private String roleName;
//
//    // getter & setter
//
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getRoleName() {
//        return roleName;
//    }
//
//    public void setRoleName(String roleName) {
//        this.roleName = roleName;
//    }
//
//
//}

package com.ql.BlogApplication.DTO;

import com.ql.BlogApplication.entities.Role;
import com.ql.BlogApplication.entities.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserRequestDto {

    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only alphabets")
    @NotEmpty(message = "Name must not be empty")
    @Size(min = 3, max = 100, message = "Name must be between 3 to 100 characters")
    private String name;

    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email must not be empty")
    private String email;

    @NotEmpty(message = "Password must not be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    private String roleName;
}
