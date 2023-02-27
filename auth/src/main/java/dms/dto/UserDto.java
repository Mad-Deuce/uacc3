package dms.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dms.entity.UserEntity;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

    private Long id;
    private String login;
    private String subdivision;
    private String permitCode;

    public UserEntity toUser(){
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setUsername(login);
        userEntity.setSubdivision(subdivision);
        userEntity.setPermitCode(permitCode);

        return userEntity;
    }

    public static UserDto fromUser(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setId(userEntity.getId());
        userDto.setLogin(userEntity.getUsername());
        userDto.setSubdivision(userEntity.getSubdivision());
        userDto.setPermitCode(userEntity.getPermitCode());

        return userDto;
    }
}
