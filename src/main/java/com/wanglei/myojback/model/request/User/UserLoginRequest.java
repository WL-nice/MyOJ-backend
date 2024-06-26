package com.wanglei.myojback.model.request.User;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -2401848398677577265L;
    private String userAccount;
    private String userPassword;
}
