package dms.dto;

import lombok.Data;


@Data
public class AuthRequestDto {
    private String login;
    private String password;
}
