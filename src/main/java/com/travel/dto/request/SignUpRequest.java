package com.travel.dto.request;

import com.travel.constant.Authority;
import com.travel.constant.Gender;
import com.travel.constant.UserRole;
import com.travel.entity.User;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "ID is required")
    @Size(min = 4, max = 20, message = "ID length must be between 4 - 20")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "ID must include letters, numbers, and underscores only")
    private final String userId;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password length must be between 8 - 100")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "Password must be at least 8 characters long and include letters, numbers, and special characters")
    private final String password;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be 50 characters or fewer")
    private final String firstName;

    @Size(max = 50, message = "Middle name must be 50 characters or fewer")
    private final String middleName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be 50 characters or fewer")
    private final String lastName;

    @NotNull(message = "Gender is required")
    private final Gender gender;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private final LocalDateTime birthDate;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must be 100 characters or fewer")
    private final String email;

    @Pattern(regexp = "^\\d{10,11}$", message = "Phone number must be between 10 and 11 digits")
    private final String phone;

    private final UserRole userRole;
    private final Authority authority;

    public User toUserEntity(PasswordEncoder passwordEncoder) {
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
                .userRole(UserRole.USER)
                .authority(Authority.USER)
                .build();
    }
}
