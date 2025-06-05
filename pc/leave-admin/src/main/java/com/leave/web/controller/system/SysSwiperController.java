package com.leave.web.controller.system;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.leave.common.utils.uuid.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.leave.common.annotation.Log;
import com.leave.common.core.controller.BaseController;
import com.leave.common.core.domain.AjaxResult;
import com.leave.common.enums.BusinessType;
import com.leave.system.domain.SysSwiper;
import com.leave.system.service.ISysSwiperService;
import com.leave.common.utils.poi.ExcelUtil;
import com.leave.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 轮播图管理Controller
 * 
 * @author admin
 * @date 2025-03-09
 */
@RestController
@RequestMapping("/system/swiper")
public class SysSwiperController extends BaseController
{
    @Autowired
    private ISysSwiperService sysSwiperService;

    @Value("${ruoyi.profile}")
    private String profile;

    /**
     * 查询轮播图管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:swiper:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysSwiper sysSwiper)
    {
        startPage();
        List<SysSwiper> list = sysSwiperService.selectSysSwiperList(sysSwiper);
        return getDataTable(list);
    }

    /**
     * 导出轮播图管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:swiper:export')")
    @Log(title = "轮播图管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysSwiper sysSwiper)
    {
        List<SysSwiper> list = sysSwiperService.selectSysSwiperList(sysSwiper);
        ExcelUtil<SysSwiper> util = new ExcelUtil<SysSwiper>(SysSwiper.class);
        util.exportExcel(response, list, "轮播图管理数据");
    }

    /**
     * 获取轮播图管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:swiper:query')")
    @GetMapping(value = "/{swiperId}")
    public AjaxResult getInfo(@PathVariable("swiperId") Long swiperId)
    {
        return success(sysSwiperService.selectSysSwiperBySwiperId(swiperId));
    }

    /**
     * 新增轮播图管理
     */
    @PreAuthorize("@ss.hasPermi('system:swiper:add')")
    @Log(title = "轮播图管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysSwiper sysSwiper)
    {
        return toAjax(sysSwiperService.insertSysSwiper(sysSwiper));
    }

    /**
     * 修改轮播图管理
     */
    @PreAuthorize("@ss.hasPermi('system:swiper:edit')")
    @Log(title = "轮播图管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysSwiper sysSwiper)
    {
        return toAjax(sysSwiperService.updateSysSwiper(sysSwiper));
    }

    /**
     * 上传轮播图图片
     */
    @PreAuthorize("@ss.hasPermi('system:swiper:edit')")
    @Log(title = "轮播图管理", businessType = BusinessType.UPDATE)
    @PostMapping("/img")
    public String editImg(@RequestBody MultipartFile file){
        if (file.isEmpty()){
            return "文件为空，请重新上传";
        }

        try {
            // 创建存储目录（如果不存在）
            File dir = new File(profile);
            if(!dir.exists()){
                dir.mkdirs();
            }

            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(profile, fileName);

            // 保存文件
            file.transferTo(filePath.toFile());

            // 返回文件的访问路径
            return "/dev-api/profile/" + fileName;
        }catch (IOException e){
            e.printStackTrace();
            return "文件上传失败";
        }
    }

    /**
     * 删除轮播图管理
     */
    @PreAuthorize("@ss.hasPermi('system:swiper:remove')")
    @Log(title = "轮播图管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{swiperIds}")
    public AjaxResult remove(@PathVariable Long[] swiperIds)
    {
        return toAjax(sysSwiperService.deleteSysSwiperBySwiperIds(swiperIds));
    }
}
