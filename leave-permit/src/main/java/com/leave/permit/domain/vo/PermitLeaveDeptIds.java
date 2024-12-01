package com.leave.permit.domain.vo;

import com.leave.common.core.domain.BaseEntity;
import com.leave.permit.domain.PermitLeave;

public class PermitLeaveDeptIds extends BaseEntity {
    private PermitLeave permitLeave;
    private Long[] deptIds;
    private String roleName;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public PermitLeaveDeptIds() {
    }

    public PermitLeaveDeptIds(PermitLeave permitLeave, Long[] deptIds) {
        this.permitLeave = permitLeave;
        this.deptIds = deptIds;
    }

    public PermitLeave getPermitLeave() {
        return permitLeave;
    }

    public void setPermitLeave(PermitLeave permitLeave) {
        this.permitLeave = permitLeave;
    }

    public Long[] getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(Long[] deptIds) {
        this.deptIds = deptIds;
    }
}
