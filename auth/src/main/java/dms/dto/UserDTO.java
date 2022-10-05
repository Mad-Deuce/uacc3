package dms.dto;

import dms.validate.update.UpdateUserForm;
import dms.validate.update.UpdateUserInfo;
import lombok.Data;

@Data
@UpdateUserForm(groups = UpdateUserInfo.class)
public class UserDTO {

    private Long id;
    private String username;
    private String roles;
    private String password;
    private String passwordConfirm;

}
