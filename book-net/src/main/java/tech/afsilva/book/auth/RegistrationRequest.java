package tech.afsilva.book.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {

    @Email(message = "Invalid email")
    @NotEmpty(message = "Email is required")
    @NotBlank(message = "Email is required")
    private String email;
    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    @NotEmpty(message = "First name is required")
    @NotBlank(message = "First is required")
    @Size(min = 3, max = 255, message = "First name must be between 3 and 255 characters long")
    private String firstname;
    @NotEmpty(message = "Last name is required")
    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 255, message = "First name must be between 3 and 255 characters long")
    private String lastname;
}
