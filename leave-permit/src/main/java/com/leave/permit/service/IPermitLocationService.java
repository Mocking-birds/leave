package com.leave.permit.service;

import java.util.List;
import com.leave.permit.domain.PermitLocation;

/**
 * 位置信息Service接口
 * 
 * @author admin
 * @date 2024-10-17
 */
public interface IPermitLocationService 
{
    /**
     * 查询位置信息
     * 
     * @param locationId 位置信息主键
     * @return 位置信息
     */
    public PermitLocation selectPermitLocationByLocationId(String locationId);

    /**
     * 查询位置信息列表
     * 
     * @param permitLocation 位置信息
     * @return 位置信息集合
     */
    public List<PermitLocation> selectPermitLocationList(PermitLocation permitLocation);

    /**
     * 新增位置信息
     * 
     * @param permitLocation 位置信息
     * @return 结果
     */
    public int insertPermitLocation(PermitLocation permitLocation);

    /**
     * 修改位置信息
     * 
     * @param permitLocation 位置信息
     * @return 结果
     */
    public int updatePermitLocation(PermitLocation permitLocation);

    /**
     * 批量删除位置信息
     * 
     * @param locationIds 需要删除的位置信息主键集合
     * @return 结果
     */
    public int deletePermitLocationByLocationIds(String[] locationIds);

    /**
     * 删除位置信息信息
     * 
     * @param locationId 位置信息主键
     * @return 结果
     */
    public int deletePermitLocationByLocationId(String locationId);
}
