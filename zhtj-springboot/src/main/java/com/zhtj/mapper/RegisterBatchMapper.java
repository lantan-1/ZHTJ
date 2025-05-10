package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.model.twosystem.RegisterBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 团籍注册批次Mapper接口
 */
@Mapper
public interface RegisterBatchMapper extends BaseMapper<RegisterBatch> {
    
    /**
     * 分页查询批次列表
     * @param page 分页参数
     * @param batchName 批次名称
     * @param registerYear 注册年度
     * @param status 状态
     * @param organizationId 组织ID
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM member_register_batch WHERE 1=1 " +
            "<if test='batchName != null and batchName != \"\"'> AND batch_name LIKE CONCAT('%',#{batchName},'%') </if> " +
            "<if test='registerYear != null and registerYear != \"\"'> AND register_year = #{registerYear} </if> " +
            "<if test='status != null and status != \"\"'> AND status = #{status} </if> " +
            "<if test='organizationId != null'> AND organization_id = #{organizationId} </if> " +
            "ORDER BY create_time DESC" +
            "</script>")
    IPage<RegisterBatch> selectBatchPage(Page<RegisterBatch> page, 
                                        @Param("batchName") String batchName, 
                                        @Param("registerYear") String registerYear, 
                                        @Param("status") String status,
                                        @Param("organizationId") Integer organizationId);
    
    /**
     * 更新批次状态
     * @param id 批次ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE member_register_batch SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);
    
    /**
     * 获取当前有效的注册批次
     * @return 批次信息
     */
    @Select("SELECT * FROM member_register_batch WHERE status = '进行中' ORDER BY create_time DESC LIMIT 1")
    RegisterBatch getCurrentBatch();
    
    /**
     * 获取批次选项列表
     * @return 批次列表
     */
    @Select("SELECT id, batch_name, register_year FROM member_register_batch ORDER BY create_time DESC")
    List<RegisterBatch> getBatchOptions();
} 