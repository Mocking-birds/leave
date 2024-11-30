package com.leave.permit.mapper;

import java.util.List;
import com.leave.permit.domain.PermitLeave;
import com.leave.permit.domain.vo.PermitLeaveBack;
import com.leave.permit.domain.PermitLocation;
import com.leave.permit.domain.vo.PermitLeaveDept;
import com.leave.permit.domain.vo.PermitLeaveDeptIds;
import org.apache.ibatis.annotations.Param;

/**
 * 假条信息Mapper接口
 * 
 * @author admin
 * @date 2024-10-17
 */
public interface PermitLeaveMapper 
{
    /**
     * 查询假条信息
     * 
     * @param leaveId 假条信息主键
     * @return 假条信息
     */
    public PermitLeave selectPermitLeaveByLeaveId(Long leaveId);

    /**
     * 查询假条信息列表（用户id）
     *
     * @param userId 假条信息外键
     * @return 假条信息
     */
    public List<PermitLeave> selectPermitLeaveListByUserId(Long userId);

    /**
     * 查询对应系部或专业对应假条信息（通过部门id）
     *
     * @param permitLeaveDept
     * @return 假条信息
     */
    public List<PermitLeave> selectPermitLeaveListByDeptId(PermitLeaveDept permitLeaveDept);

    /**
     * 查询假条信息列表（用户id,是否销假）
     *
     * @param permitLeaveBack 假条信息外键
     * @return 假条信息
     */
    public List<PermitLeave> selectPermitLeaveListByIsBack(PermitLeaveBack permitLeaveBack);

    /**
     * 查询假条信息列表
     * 
     * @param permitLeave 假条信息
     * @return 假条信息集合
     */
    public List<PermitLeave> selectPermitLeaveList(PermitLeave permitLeave);

    /**
     * 查询假条信息列表(ids组)
     *
     * @param permitLeave,deptIds
     * @return 假条信息集合
     */
    public List<PermitLeave> selectPermitLeaveByDeptIds(@Param("permitLeave") PermitLeave permitLeave, @Param("deptIds") Long[] deptIds);

//    public List<PermitLeave> selectPermitLeaveByDeptIds(PermitLeave permitLeave);
    /**
     * 新增假条信息
     * 
     * @param permitLeave 假条信息
     * @return 结果
     */
    public int insertPermitLeave(PermitLeave permitLeave);

    /**
     * 修改假条信息
     * 
     * @param permitLeave 假条信息
     * @return 结果
     */
    public int updatePermitLeave(PermitLeave permitLeave);

    /**
     * 删除假条信息
     * 
     * @param leaveId 假条信息主键
     * @return 结果
     */
    public int deletePermitLeaveByLeaveId(Long leaveId);

    /**
     * 批量删除假条信息
     * 
     * @param leaveIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePermitLeaveByLeaveIds(Long[] leaveIds);

    /**
     * 批量删除位置信息
     * 
     * @param leaveIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePermitLocationByLeaveIds(Long[] leaveIds);
    
    /**
     * 批量新增位置信息
     * 
     * @param permitLocationList 位置信息列表
     * @return 结果
     */
    public int batchPermitLocation(List<PermitLocation> permitLocationList);
    

    /**
     * 通过假条信息主键删除位置信息信息
     * 
     * @param leaveId 假条信息ID
     * @return 结果
     */
    public int deletePermitLocationByLeaveId(Long leaveId);
}
