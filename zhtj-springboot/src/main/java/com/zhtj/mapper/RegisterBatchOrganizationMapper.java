package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhtj.model.twosystem.RegisterBatchOrganization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 注册批次-组织关联Mapper接口
 */
@Mapper
public interface RegisterBatchOrganizationMapper extends BaseMapper<RegisterBatchOrganization> {

    /**
     * 根据批次ID获取关联组织ID列表
     * @param batchId 批次ID
     * @return 组织ID列表
     */
    @Select("SELECT organization_id FROM member_register_batch_organization WHERE batch_id = #{batchId}")
    List<Integer> getOrganizationIdsByBatchId(@Param("batchId") Integer batchId);
    
    /**
     * 检查组织是否在批次允许范围内
     * @param batchId 批次ID
     * @param organizationId 组织ID
     * @return 计数（>0表示在批次中）
     */
    @Select("SELECT COUNT(*) FROM member_register_batch_organization " +
           "WHERE batch_id = #{batchId} AND organization_id = #{organizationId}")
    Integer checkOrganizationInBatch(@Param("batchId") Integer batchId, @Param("organizationId") Integer organizationId);
    
    /**
     * 根据批次ID和组织路径计数
     * @param batchId 批次ID
     * @param organizationIds 组织ID列表
     * @return 计数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM member_register_batch_organization " +
            "WHERE batch_id = #{batchId} " +
            "AND organization_id IN " +
            "<foreach collection='organizationIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int countByBatchIdAndOrganizationPath(@Param("batchId") Integer batchId,
                                         @Param("organizationIds") List<Integer> organizationIds);
} 