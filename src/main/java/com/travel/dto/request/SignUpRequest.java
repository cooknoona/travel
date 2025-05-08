package com.travel.dto.request;

import com.travel.constant.Authority;
import com.travel.constant.Gender;
import com.travel.constant.UserRole;
import com.travel.entity.User;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Setter
@NoArgsConstructor
public class SignUpRequest {
    private String userId;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private Gender gender;
    private LocalDateTime birthDate;
    private String email;
    private String phone;
    private UserRole userRole;
    private Authority authority;

    public User toRegister(PasswordEncoder passwordEncoder) {
        return User.builder()
                .userId(userId)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName)
                .gender(gender)
                .email(email)
                .phone(phone)
                .birthDate(birthDate)
                .userRole(UserRole.valueOf("USER"))
                .build();
    }
}
