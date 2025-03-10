package com.leave.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.leave.common.annotation.Excel;
import com.leave.common.core.domain.BaseEntity;

/**
 * 轮播图管理对象 sys_swiper
 * 
 * @author admin
 * @date 2025-03-09
 */
public class SysSwiper extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long swiperId;

    /** 轮播图描述信息 */
    @Excel(name = "轮播图描述信息")
    private String description;

    /** 轮播图路径 */
    @Excel(name = "轮播图路径")
    private String imageUrl;

    /** 轮播图状态（0 正常； 1 停用） */
    @Excel(name = "轮播图状态", readConverterExp = "0=,正=常；,1=,停=用")
    private String isActive;

    public void setSwiperId(Long swiperId) 
    {
        this.swiperId = swiperId;
    }

    public Long getSwiperId() 
    {
        return swiperId;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setImageUrl(String imageUrl) 
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() 
    {
        return imageUrl;
    }
    public void setIsActive(String isActive) 
    {
        this.isActive = isActive;
    }

    public String getIsActive() 
    {
        return isActive;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("swiperId", getSwiperId())
            .append("description", getDescription())
            .append("imageUrl", getImageUrl())
            .append("isActive", getIsActive())
            .toString();
    }
}
