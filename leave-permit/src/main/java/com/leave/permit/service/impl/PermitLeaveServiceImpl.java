package com.leave.permit.service.impl;

import java.util.Arrays;
import java.util.List;

import com.leave.permit.domain.vo.PermitLeaveDept;
import com.leave.permit.domain.vo.PermitLeaveDeptIds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.leave.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.leave.permit.domain.PermitLocation;
import com.leave.permit.mapper.PermitLeaveMapper;
import com.leave.permit.domain.PermitLeave;
import com.leave.permit.domain.vo.PermitLeaveBack;
import com.leave.permit.service.IPermitLeaveService;

/**
 * 假条信息Service业务层处理
 * 
 * @author admin
 * @date 2024-10-17
 */
@Service
public class PermitLeaveServiceImpl implements IPermitLeaveService 
{
    @Autowired
    private PermitLeaveMapper permitLeaveMapper;

    /**
     * 查询假条信息
     * 
     * @param leaveId 假条信息主键
     * @return 假条信息
     */
    @Override
    public PermitLeave selectPermitLeaveByLeaveId(Long leaveId)
    {
        return permitLeaveMapper.selectPermitLeaveByLeaveId(leaveId);
    }

    /**
     * 查询假条信息列表
     * 
     * @param permitLeave 假条信息
     * @return 假条信息
     */
    @Override
    public List<PermitLeave> selectPermitLeaveList(PermitLeave permitLeave)
    {
        return permitLeaveMapper.selectPermitLeaveList(permitLeave);
    }

    /**
     * 查询假条信息列表（部门ids组）
     *
     * @param permitLeaveDeptIds 假条信息
     * @return 假条信息
     */

    @Override
    public List<PermitLeave> selectPermitLeaveByDeptIds(PermitLeaveDeptIds permitLeaveDeptIds)
    {
        PermitLeave permitLeave = permitLeaveDeptIds.getPermitLeave();
        Long[] deptIds = permitLeaveDeptIds.getDeptIds();
        String roleName = permitLeaveDeptIds.getRoleName();
        System.out.println("roleName:" + roleName);
        if(roleName.equals("admin")){
            return permitLeaveMapper.selectPermitLeaveByDeptIdsAdmin(permitLeave,deptIds);
        } else if (roleName.equals("director")) {
            return permitLeaveMapper.selectPermitLeaveByDeptIdsDirector(permitLeave,deptIds);
        }else {
            return null;
        }

    }

    /**
     * 查询假条信息列表
     *
     * @param userId 用户id
     * @return 假条信息
     */
    @Override
    public List<PermitLeave> selectPermitLeaveListByUserId(Long userId) {
        return permitLeaveMapper.selectPermitLeaveListByUserId(userId);
    }

    /**
     * 查询对应系部或专业对应假条信息（通过部门id）
     *
     * @param permitLeaveDept
     * @return 假条信息
     */
    @Override
    public List<PermitLeave> selectPermitLeaveListByDeptId(PermitLeaveDept permitLeaveDept) {
        return permitLeaveMapper.selectPermitLeaveListByDeptId(permitLeaveDept);
    }

    /**
     * 查询假条信息列表
     *
     * @param permitLeaveBack 是否销假
     * @return 假条信息
     */
    @Override
    public List<PermitLeave> selectPermitLeaveListByIsBack(PermitLeaveBack permitLeaveBack) {
        return permitLeaveMapper.selectPermitLeaveListByIsBack(permitLeaveBack);
    }

    /**
     * 新增假条信息
     * 
     * @param permitLeave 假条信息
     * @return 结果
     */
    @Transactional
    @Override
    public int insertPermitLeave(PermitLeave permitLeave)
    {
        int rows = permitLeaveMapper.insertPermitLeave(permitLeave);
        insertPermitLocation(permitLeave);
        return rows;
    }

    /**
     * 修改假条信息
     * 
     * @param permitLeave 假条信息
     * @return 结果
     */
    @Transactional
    @Override
    public int updatePermitLeave(PermitLeave permitLeave)
    {
        permitLeaveMapper.deletePermitLocationByLeaveId(permitLeave.getLeaveId());
        insertPermitLocation(permitLeave);
        return permitLeaveMapper.updatePermitLeave(permitLeave);
    }

    /**
     * 批量删除假条信息
     * 
     * @param leaveIds 需要删除的假条信息主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePermitLeaveByLeaveIds(Long[] leaveIds)
    {
        permitLeaveMapper.deletePermitLocationByLeaveIds(leaveIds);
        return permitLeaveMapper.deletePermitLeaveByLeaveIds(leaveIds);
    }

    /**
     * 删除假条信息信息
     * 
     * @param leaveId 假条信息主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePermitLeaveByLeaveId(Long leaveId)
    {
        permitLeaveMapper.deletePermitLocationByLeaveId(leaveId);
        return permitLeaveMapper.deletePermitLeaveByLeaveId(leaveId);
    }

    /**
     * 新增位置信息信息
     * 
     * @param permitLeave 假条信息对象
     */
    public void insertPermitLocation(PermitLeave permitLeave)
    {
        List<PermitLocation> permitLocationList = permitLeave.getPermitLocationList();
        Long leaveId = permitLeave.getLeaveId();
        if (StringUtils.isNotNull(permitLocationList))
        {
            List<PermitLocation> list = new ArrayList<PermitLocation>();
            for (PermitLocation permitLocation : permitLocationList)
            {
                permitLocation.setLeaveId(leaveId);
                list.add(permitLocation);
            }
            if (list.size() > 0)
            {
                permitLeaveMapper.batchPermitLocation(list);
            }
        }
    }
}
