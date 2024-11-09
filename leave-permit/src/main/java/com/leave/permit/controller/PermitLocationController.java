package com.leave.permit.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.leave.common.annotation.Log;
import com.leave.common.core.controller.BaseController;
import com.leave.common.core.domain.AjaxResult;
import com.leave.common.enums.BusinessType;
import com.leave.permit.domain.PermitLocation;
import com.leave.permit.service.IPermitLocationService;
import com.leave.common.utils.poi.ExcelUtil;
import com.leave.common.core.page.TableDataInfo;

/**
 * 位置信息Controller
 * 
 * @author admin
 * @date 2024-10-17
 */
@RestController
@RequestMapping("/permit/location")
public class PermitLocationController extends BaseController
{
    @Autowired
    private IPermitLocationService permitLocationService;

    /**
     * 查询位置信息列表
     */
    @PreAuthorize("@ss.hasPermi('permit:location:list')")
    @GetMapping("/list")
    public TableDataInfo list(PermitLocation permitLocation)
    {
        startPage();
        List<PermitLocation> list = permitLocationService.selectPermitLocationList(permitLocation);
        return getDataTable(list);
    }

    /**
     * 导出位置信息列表
     */
    @PreAuthorize("@ss.hasPermi('permit:location:export')")
    @Log(title = "位置信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PermitLocation permitLocation)
    {
        List<PermitLocation> list = permitLocationService.selectPermitLocationList(permitLocation);
        ExcelUtil<PermitLocation> util = new ExcelUtil<PermitLocation>(PermitLocation.class);
        util.exportExcel(response, list, "位置信息数据");
    }

    /**
     * 获取位置信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('permit:location:query')")
    @GetMapping(value = "/{locationId}")
    public AjaxResult getInfo(@PathVariable("locationId") String locationId)
    {
        return success(permitLocationService.selectPermitLocationByLocationId(locationId));
    }

    /**
     * 新增位置信息
     */
    @PreAuthorize("@ss.hasPermi('permit:location:add')")
    @Log(title = "位置信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PermitLocation permitLocation)
    {
        return toAjax(permitLocationService.insertPermitLocation(permitLocation));
    }

    /**
     * 修改位置信息
     */
    @PreAuthorize("@ss.hasPermi('permit:location:edit')")
    @Log(title = "位置信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PermitLocation permitLocation)
    {
        return toAjax(permitLocationService.updatePermitLocation(permitLocation));
    }

    /**
     * 删除位置信息
     */
    @PreAuthorize("@ss.hasPermi('permit:location:remove')")
    @Log(title = "位置信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{locationIds}")
    public AjaxResult remove(@PathVariable String[] locationIds)
    {
        return toAjax(permitLocationService.deletePermitLocationByLocationIds(locationIds));
    }
}
