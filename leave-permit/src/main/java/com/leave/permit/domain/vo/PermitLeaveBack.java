package com.leave.permit.domain.vo;

import com.leave.common.annotation.Excel;
import com.leave.common.core.domain.BaseEntity;

public class PermitLeaveBack extends BaseEntity {
    private Long userId;

    /** 是否销假（0:未销假， 1：已销假） */
    @Excel(name = "是否销假", readConverterExp = "0=:未销假，,1=：已销假")
    private String isBack;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIsBack() {
        return isBack;
    }

    public void setIsBack(String isBack) {
        this.isBack = isBack;
    }
}
