package com.leave.permit.domain.vo;

//import com.leave.common.annotation.Excel;
import com.leave.common.core.domain.BaseEntity;

public class PermitLeaveDept extends BaseEntity {
    private Long deptId;

    // 特殊条件开启
    private Boolean specialConditions;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Boolean getSpecialConditions() {
        return specialConditions;
    }

    public void setSpecialConditions(Boolean specialConditions) {
        this.specialConditions = specialConditions;
    }
}
