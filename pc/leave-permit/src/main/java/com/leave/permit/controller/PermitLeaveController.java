package com.leave.permit.controller;

import java.util.*;
import javax.servlet.http.HttpServletResponse;

import com.leave.permit.domain.vo.PermitLeaveDept;
import com.leave.permit.domain.vo.PermitLeaveDeptIds;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.leave.common.annotation.Log;
import com.leave.common.core.controller.BaseController;
import com.leave.common.core.domain.AjaxResult;
import com.leave.common.enums.BusinessType;
import com.leave.permit.domain.PermitLeave;
import com.leave.permit.domain.vo.PermitLeaveBack;
import com.leave.permit.service.IPermitLeaveService;
import com.leave.common.utils.poi.ExcelUtil;
import com.leave.common.core.page.TableDataInfo;

/**
 * 假条信息Controller
 * 
 * @author admin
 * @date 2024-10-17
 */
@RestController
@RequestMapping("/permit/permit")
public class PermitLeaveController extends BaseController
{
    @Autowired
    private IPermitLeaveService permitLeaveService;

    /**
     * 查询假条信息列表
     */
    @PreAuthorize("@ss.hasPermi('permit:permit:list')")
    @GetMapping("/list")
    public TableDataInfo list(PermitLeave permitLeave)
    {
        startPage();
        List<PermitLeave> list = permitLeaveService.selectPermitLeaveList(permitLeave);
        return getDataTable(list);
    }

    /**
     * 查询假条信息列表（部门ids组）
     */
    @PreAuthorize("@ss.hasPermi('permit:permit:list')")
    @PostMapping("/list/deptIds")
    public TableDataInfo listByIds(@RequestBody PermitLeaveDeptIds permitLeaveDeptIds){
        // 非空判断
        if(permitLeaveDeptIds.getPermitLeave() == null && permitLeaveDeptIds.getPermitLeave().getLeaveStatus() == null && permitLeaveDeptIds.getPermitLeave().getIsBack() == null){
            return  null;
        }
        List<PermitLeave> list = permitLeaveService.selectPermitLeaveByDeptIds(permitLeaveDeptIds);
        Map<Long,List<PermitLeave>> map = new HashMap<>();
        for(Long deptId : permitLeaveDeptIds.getDeptIds()){
            map.put(deptId, new ArrayList<PermitLeave>());
        }
//        System.out.println("list:"+list);
        
        String roleName = permitLeaveDeptIds.getRoleName();

        System.out.println("roleName:"+roleName);
        System.out.println("系主任："+roleName.equals("director"));
        
        if(roleName.equals("admin")){
            for(PermitLeave permitLeave : list){
                if (map.containsKey(permitLeave.getUser().getDept().getParentId())){
                    map.get(permitLeave.getUser().getDept().getParentId()).add(permitLeave);
                }
            }
        } else if (roleName.equals("director")) {
            for(PermitLeave permitLeave : list){
                if (map.containsKey(permitLeave.getUser().getDeptId())){
                    map.get(permitLeave.getUser().getDeptId()).add(permitLeave);
                }
            }
        }


        List<List<PermitLeave>> res = new ArrayList<>(map.values());


       return getDataTable(res);
    }
//    public TableDataInfo list(@RequestParam Long[] deptIds){
//        List<PermitLeave> list = permitLeaveService.selectPermitLeaveByDeptIds(deptIds);
//        Map<Long,List<PermitLeave>> map = new HashMap<>();
//        for (long deptId : deptIds){
//            map.put(deptId, new ArrayList<PermitLeave>());
//        }
//        for (PermitLeave permitLeave : list){
//            System.out.println(permitLeave.getUser().getDept().getParentId());
//            if(map.containsKey(permitLeave.getUser().getDept().getParentId())){
//                map.get(permitLeave.getUser().getDept().getParentId()).add(permitLeave);
//            }
//        }
//
//        List<List<PermitLeave>> res = new ArrayList<>(map.values());
//        System.out.println(res);
//        return getDataTable(res);
//    }

    /**
     * 查询假条信息列表(用户id)
     */
    @PreAuthorize("@ss.hasPermi('permit:permit:query')")
    @GetMapping(value = "/list/{userId}")
    public TableDataInfo list(@PathVariable("userId") Long userId)
    {
        startPage();
        List<PermitLeave> list = permitLeaveService.selectPermitLeaveListByUserId(userId);
        return getDataTable(list);
    }

    /**
     * 查询待办假条信息列表
     */
    @PreAuthorize("@ss.hasPermi('permit:permit:query')")
    @GetMapping("/list/backlog")
    public TableDataInfo listBacklog(PermitLeaveDept permitLeaveDept){
        startPage();
        List<PermitLeave> list = permitLeaveService.selectPermitLeaveListBackLog(permitLeaveDept);
        return getDataTable(list);
    }

    /**
     * 查询假条信息列表(用户id,是否销假)
     */
    @PreAuthorize("@ss.hasPermi('permit:permit:query')")
    @GetMapping(value = "/list/isBack")
    public TableDataInfo listByUserId(PermitLeaveBack permitLeaveBack)
    {
        startPage();
        List<PermitLeave> list = permitLeaveService.selectPermitLeaveListByIsBack(permitLeaveBack);
        return getDataTable(list);
    }


    /**
     * 查询对应系部或专业对应假条信息（通过部门id）
     */
    @PreAuthorize("@ss.hasPermi('permit:permit:query')")
    @GetMapping(value = "/list/dept")
    public TableDataInfo listByDeptId(PermitLeaveDept permitLeaveDept)
    {
        startPage();
        List<PermitLeave> list = permitLeaveService.selectPermitLeaveListByDeptId(permitLeaveDept);
        return getDataTable(list);
    }

    /**
     * 导出假条信息列表
     */
    @PreAuthorize("@ss.hasPermi('permit:permit:export')")
    @Log(title = "假条信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PermitLeave permitLeave)
    {
        List<PermitLeave> list = permitLeaveService.selectPermitLeaveList(permitLeave);
        ExcelUtil<PermitLeave> util = new ExcelUtil<PermitLeave>(PermitLeave.class);
        util.exportExcel(response, list, "假条信息数据");
    }

    /**
     * 获取假条信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('permit:permit:query')")
    @GetMapping(value = "/{leaveId}")
    public AjaxResult getInfo(@PathVariable("leaveId") Long leaveId)
    {
        return success(permitLeaveService.selectPermitLeaveByLeaveId(leaveId));
    }

    /**
     * 新增假条信息
     */
    @PreAuthorize("@ss.hasPermi('permit:permit:add')")
    @Log(title = "假条信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PermitLeave permitLeave)
    {
        return toAjax(permitLeaveService.insertPermitLeave(permitLeave));
    }

    /**
     * 修改假条信息
     */
    @PreAuthorize("@ss.hasPermi('permit:permit:edit')")
    @Log(title = "假条信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PermitLeave permitLeave)
    {
        return toAjax(permitLeaveService.updatePermitLeave(permitLeave));
    }

    /**
     * 删除假条信息
     */
    @PreAuthorize("@ss.hasPermi('permit:permit:remove')")
    @Log(title = "假条信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{leaveIds}")
    public AjaxResult remove(@PathVariable Long[] leaveIds)
    {
        return toAjax(permitLeaveService.deletePermitLeaveByLeaveIds(leaveIds));
    }
}
