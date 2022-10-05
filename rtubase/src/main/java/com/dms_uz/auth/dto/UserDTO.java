package com.dms_uz.auth.dto;

import com.dms_uz.auth.validate.update.UpdateUserForm;
import com.dms_uz.auth.validate.update.UpdateUserInfo;
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
