package com.syb.ypic.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum UserRoleEnum {
    ADMIN("管理员","admin"),
    USER("用户","user");

    private final String text;
    private final String value;

    UserRoleEnum(String text,String value){
        this.text = text;
        this.value = value;
    }

    public static UserRoleEnum getEnumByValue(String value){
        if(ObjUtil.isEmpty(value)){
            return null;
        }
        for(UserRoleEnum userRoleEnum : UserRoleEnum.values()){
            if(userRoleEnum.getValue().equals(value)){
                return userRoleEnum;
            }
        }
        return null;
    }
}
