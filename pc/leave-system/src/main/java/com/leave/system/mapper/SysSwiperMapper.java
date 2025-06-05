package com.leave.system.mapper;

import java.util.List;
import com.leave.system.domain.SysSwiper;

/**
 * 轮播图管理Mapper接口
 * 
 * @author admin
 * @date 2025-03-09
 */
public interface SysSwiperMapper 
{
    /**
     * 查询轮播图管理
     * 
     * @param swiperId 轮播图管理主键
     * @return 轮播图管理
     */
    public SysSwiper selectSysSwiperBySwiperId(Long swiperId);

    /**
     * 查询轮播图管理列表
     * 
     * @param sysSwiper 轮播图管理
     * @return 轮播图管理集合
     */
    public List<SysSwiper> selectSysSwiperList(SysSwiper sysSwiper);

    /**
     * 新增轮播图管理
     * 
     * @param sysSwiper 轮播图管理
     * @return 结果
     */
    public int insertSysSwiper(SysSwiper sysSwiper);

    /**
     * 修改轮播图管理
     * 
     * @param sysSwiper 轮播图管理
     * @return 结果
     */
    public int updateSysSwiper(SysSwiper sysSwiper);

    /**
     * 删除轮播图管理
     * 
     * @param swiperId 轮播图管理主键
     * @return 结果
     */
    public int deleteSysSwiperBySwiperId(Long swiperId);

    /**
     * 批量删除轮播图管理
     * 
     * @param swiperIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysSwiperBySwiperIds(Long[] swiperIds);
}
