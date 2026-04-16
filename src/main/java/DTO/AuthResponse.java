package DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResponse {
    private String token;
    private String tokenType;
    private User user;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private Long id;
        private String fullName;
        private String email;
        private String telephoneNumber;
        private String[] roles;
    }
}