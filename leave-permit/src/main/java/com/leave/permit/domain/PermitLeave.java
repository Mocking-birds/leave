package com.leave.permit.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.leave.common.annotation.Excels;
import com.leave.common.core.domain.entity.SysDept;
import com.leave.common.core.domain.entity.SysUser;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.leave.common.annotation.Excel;
import com.leave.common.core.domain.BaseEntity;

/**
 * 假条信息对象 permit_leave
 * 
 * @author admin
 * @date 2024-10-17
 */
public class PermitLeave extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 假条id */
    private Long leaveId;

    /** 用户id */
    @Excel(name = "用户id", type = Excel.Type.IMPORT)
    private Long userId;

    /** 请假类型（0：病假，1：事假） */
    @Excel(name = "请假类型", readConverterExp = "0=：病假，1：事假")
    private String permitType;

    /** 请假理由 */
    @Excel(name = "请假理由")
    private String reason;

    /** 起始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "起始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 是否销假（0:未销假， 1：已销假） */
    @Excel(name = "是否销假", readConverterExp = "0=:未销假，,1=：已销假")
    private String isBack;

    /** 请假状态（0：未审批，1：已同意，2：已驳回） */
    @Excel(name = "请假状态", readConverterExp = "0=：未审批，1：已同意，2：已驳回")
    private String leaveStatus;

    /** 请假时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "请假时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date permitTime;

    /** 销假时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "销假时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date backTime;

    /** 用户对象 */
    @Excels({
            @Excel(name = "用户名称", targetAttr = "user_name", type = Excel.Type.EXPORT),
            @Excel(name = "用户昵称", targetAttr = "nick_name", type = Excel.Type.EXPORT)
    })
    private SysUser user;

    /** 位置信息信息 */
    private List<PermitLocation> permitLocationList;

    public void setLeaveId(Long leaveId) 
    {
        this.leaveId = leaveId;
    }

    public Long getLeaveId() 
    {
        return leaveId;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setPermitType(String permitType) 
    {
        this.permitType = permitType;
    }

    public String getPermitType() 
    {
        return permitType;
    }
    public void setReason(String reason) 
    {
        this.reason = reason;
    }

    public String getReason() 
    {
        return reason;
    }
    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }
    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }
    public void setIsBack(String isBack) 
    {
        this.isBack = isBack;
    }

    public String getIsBack() 
    {
        return isBack;
    }
    public void setLeaveStatus(String leaveStatus) 
    {
        this.leaveStatus = leaveStatus;
    }

    public String getLeaveStatus() 
    {
        return leaveStatus;
    }
    public void setPermitTime(Date permitTime) 
    {
        this.permitTime = permitTime;
    }

    public Date getPermitTime() 
    {
        return permitTime;
    }
    public void setBackTime(Date backTime) 
    {
        this.backTime = backTime;
    }

    public Date getBackTime() 
    {
        return backTime;
    }

    public List<PermitLocation> getPermitLocationList()
    {
        return permitLocationList;
    }

    public SysUser getUser()
    {
        return user;
    }

    public void setUser(SysUser user)
    {
        this.user = user;
    }

    public void setPermitLocationList(List<PermitLocation> permitLocationList)
    {
        this.permitLocationList = permitLocationList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("leaveId", getLeaveId())
            .append("userId", getUserId())
            .append("permitType", getPermitType())
            .append("reason", getReason())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("isBack", getIsBack())
            .append("leaveStatus", getLeaveStatus())
            .append("permitTime", getPermitTime())
            .append("backTime", getBackTime())
            .append("permitLocationList", getPermitLocationList())
            .append("user", getUser())
            .append("createTime", getCreateTime())
            .toString();
    }
}
