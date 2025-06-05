package com.leave.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.leave.system.mapper.SysSwiperMapper;
import com.leave.system.domain.SysSwiper;
import com.leave.system.service.ISysSwiperService;

/**
 * 轮播图管理Service业务层处理
 * 
 * @author admin
 * @date 2025-03-09
 */
@Service
public class SysSwiperServiceImpl implements ISysSwiperService 
{
    @Autowired
    private SysSwiperMapper sysSwiperMapper;

    /**
     * 查询轮播图管理
     * 
     * @param swiperId 轮播图管理主键
     * @return 轮播图管理
     */
    @Override
    public SysSwiper selectSysSwiperBySwiperId(Long swiperId)
    {
        return sysSwiperMapper.selectSysSwiperBySwiperId(swiperId);
    }

    /**
     * 查询轮播图管理列表
     * 
     * @param sysSwiper 轮播图管理
     * @return 轮播图管理
     */
    @Override
    public List<SysSwiper> selectSysSwiperList(SysSwiper sysSwiper)
    {
        return sysSwiperMapper.selectSysSwiperList(sysSwiper);
    }

    /**
     * 新增轮播图管理
     * 
     * @param sysSwiper 轮播图管理
     * @return 结果
     */
    @Override
    public int insertSysSwiper(SysSwiper sysSwiper)
    {
        return sysSwiperMapper.insertSysSwiper(sysSwiper);
    }

    /**
     * 修改轮播图管理
     * 
     * @param sysSwiper 轮播图管理
     * @return 结果
     */
    @Override
    public int updateSysSwiper(SysSwiper sysSwiper)
    {
        return sysSwiperMapper.updateSysSwiper(sysSwiper);
    }

    /**
     * 批量删除轮播图管理
     * 
     * @param swiperIds 需要删除的轮播图管理主键
     * @return 结果
     */
    @Override
    public int deleteSysSwiperBySwiperIds(Long[] swiperIds)
    {
        return sysSwiperMapper.deleteSysSwiperBySwiperIds(swiperIds);
    }

    /**
     * 删除轮播图管理信息
     * 
     * @param swiperId 轮播图管理主键
     * @return 结果
     */
    @Override
    public int deleteSysSwiperBySwiperId(Long swiperId)
    {
        return sysSwiperMapper.deleteSysSwiperBySwiperId(swiperId);
    }
}
