package com.nguyendat.linkedin.dto.request;
import lombok.*;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
