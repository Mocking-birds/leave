package com.leave.permit.domain.vo;

//import com.leave.common.annotation.Excel;
import com.leave.common.annotation.Excel;
import com.leave.common.core.domain.BaseEntity;

public class PermitLeaveDept extends BaseEntity {
    private Long deptId;

    // 特殊条件开启
    private String specialConditions;

    // 请假状态
    @Excel(name = "请假状态", readConverterExp = "0=：未审批，1：已同意，2：已驳回")
    private String leaveStatus;

    /** 是否销假（0:未销假， 1：已销假） */
    @Excel(name = "是否销假", readConverterExp = "0=:未销假，,1=：已销假")
    private String isBack;

    /** 请假类型（0：病假，1：事假） */
    @Excel(name = "请假类型", readConverterExp = "0=：病假，1：事假")
    private String permitType;

    /** user_id */
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPermitType() {
        return permitType;
    }

    public void setPermitType(String permitType) {
        this.permitType = permitType;
    }

    public String getLeaveStatus() {
        return leaveStatus;
    }

    public void setLeaveStatus(String leaveStatus) {
        this.leaveStatus = leaveStatus;
    }

    public String getIsBack() {
        return isBack;
    }

    public void setIsBack(String isBack) {
        this.isBack = isBack;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getSpecialConditions() {
        return specialConditions;
    }

    public void setSpecialConditions(String specialConditions) {
        this.specialConditions = specialConditions;
    }
}
