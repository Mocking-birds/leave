package com.leave.permit.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.leave.common.annotation.Excel;
import com.leave.common.core.domain.BaseEntity;

/**
 * 位置信息对象 permit_location
 * 
 * @author admin
 * @date 2024-10-17
 */
public class PermitLocation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 位置信息id */
    private String locationId;

    /** 假条id */
    @Excel(name = "假条id")
    private Long leaveId;

    /** 纬度 */
    @Excel(name = "纬度")
    private String latitude;

    /** 经度 */
    @Excel(name = "经度")
    private String longitude;

    /** 位置名称 */
    @Excel(name = "位置名称")
    private String locationName;

    /** 位置类型（0：请假位置，1：销假位置） */
    @Excel(name = "位置类型", readConverterExp = "0=：请假位置，1：销假位置")
    private String locationType;

    public void setLocationId(String locationId) 
    {
        this.locationId = locationId;
    }

    public String getLocationId() 
    {
        return locationId;
    }
    public void setLeaveId(Long leaveId) 
    {
        this.leaveId = leaveId;
    }

    public Long getLeaveId() 
    {
        return leaveId;
    }
    public void setLatitude(String latitude) 
    {
        this.latitude = latitude;
    }

    public String getLatitude() 
    {
        return latitude;
    }
    public void setLongitude(String longitude) 
    {
        this.longitude = longitude;
    }

    public String getLongitude() 
    {
        return longitude;
    }
    public void setLocationName(String locationName) 
    {
        this.locationName = locationName;
    }

    public String getLocationName() 
    {
        return locationName;
    }
    public void setLocationType(String locationType) 
    {
        this.locationType = locationType;
    }

    public String getLocationType() 
    {
        return locationType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("locationId", getLocationId())
            .append("leaveId", getLeaveId())
            .append("latitude", getLatitude())
            .append("longitude", getLongitude())
            .append("locationName", getLocationName())
            .append("locationType", getLocationType())
            .toString();
    }
}
