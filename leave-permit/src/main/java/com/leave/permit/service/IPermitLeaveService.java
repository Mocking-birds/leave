package com.leave.permit.service;

import java.util.List;
import com.leave.permit.domain.PermitLeave;
import com.leave.permit.domain.vo.PermitLeaveBack;
import com.leave.permit.domain.vo.PermitLeaveDept;
import com.leave.permit.domain.vo.PermitLeaveDeptIds;

/**
 * 假条信息Service接口
 * 
 * @author admin
 * @date 2024-10-17
 */
public interface IPermitLeaveService 
{
    /**
     * 查询假条信息
     * 
     * @param leaveId 假条信息主键
     * @return 假条信息
     */
    public PermitLeave selectPermitLeaveByLeaveId(Long leaveId);

    /**
     * 查询假条信息列表
     * 
     * @param permitLeave 假条信息
     * @return 假条信息集合
     */
    public List<PermitLeave> selectPermitLeaveList(PermitLeave permitLeave);

    /**
     * 查询假条信息列表（部门ids组）
     *
     * @param permitLeaveDeptIds 假条信息
     * @return 假条信息集合
     */
    public List<PermitLeave> selectPermitLeaveByDeptIds(PermitLeaveDeptIds permitLeaveDeptIds);

    /**
     * 查询待办假条信息列表
     * @param permitLeaveDept
     * @return 待办假条信息集合
     */
    public List<PermitLeave> selectPermitLeaveListBackLog(PermitLeaveDept permitLeaveDept);

    /**
     * 查询假条信息列表
     *
     * @param userId 用户id
     * @return 假条信息集合
     */
    public List<PermitLeave> selectPermitLeaveListByUserId(Long userId);

    /**
     * 查询假条信息列表
     *
     * @param permitLeaveBack (是否销假,用户id)
     * @return 假条信息集合
     */
    public List<PermitLeave> selectPermitLeaveListByIsBack(PermitLeaveBack permitLeaveBack);

    /**
     * 查询对应系部或专业对应假条信息（通过部门id）
     *
     * @param permitLeaveDept (部门id)
     * @return 假条信息集合
     */
    public List<PermitLeave> selectPermitLeaveListByDeptId(PermitLeaveDept permitLeaveDept);

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
     * 批量删除假条信息
     * 
     * @param leaveIds 需要删除的假条信息主键集合
     * @return 结果
     */
    public int deletePermitLeaveByLeaveIds(Long[] leaveIds);

    /**
     * 删除假条信息信息
     * 
     * @param leaveId 假条信息主键
     * @return 结果
     */
    public int deletePermitLeaveByLeaveId(Long leaveId);
}
