package com.zhtj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhtj.common.exception.BusinessException;
import com.zhtj.common.exception.ResourceNotFoundException;
import com.zhtj.domain.Organization;
import com.zhtj.domain.Transfer;
import com.zhtj.domain.User;
import com.zhtj.mapper.TransferMapper;
import com.zhtj.service.OrganizationService;
import com.zhtj.service.RoleService;
import com.zhtj.service.TransferService;
import com.zhtj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 团员关系转接服务实现类
 */
@Service
public class TransferServiceImpl extends ServiceImpl<TransferMapper, Transfer> implements TransferService {

    @Autowired
    private TransferMapper transferMapper;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrganizationService organizationService;
    
    @Autowired
    private RoleService roleService;
    
    @Override
    public IPage<Transfer> getTransferPage(Page<Transfer> page, Integer userId, Integer statusCode, Integer organizationId) {
        IPage<Transfer> pageResult = transferMapper.selectTransferPage(page, userId, statusCode, organizationId);
        
        // 确保每条记录的组织名称字段都有值
        if (pageResult.getRecords() != null && !pageResult.getRecords().isEmpty()) {
            for (Transfer transfer : pageResult.getRecords()) {
                // 处理申请人姓名
                if ((transfer.getTransferUserName() == null || transfer.getTransferUserName().isEmpty()) && transfer.getTransferUserId() != null) {
                    User user = userService.getById(transfer.getTransferUserId());
                    if (user != null) {
                        transfer.setTransferUserName(user.getName());
                        // 设置关联的用户对象，便于前端使用
                        transfer.setTransferUser(user);
                    } else {
                        // 如果用户不存在，使用ID作为默认显示
                        transfer.setTransferUserName("用户ID:" + transfer.getTransferUserId());
                    }
                }
                
                // 处理转出组织名称
                if ((transfer.getTransferOutOrgName() == null || transfer.getTransferOutOrgName().isEmpty()) && transfer.getTransferOutOrgId() != null) {
                    Organization outOrg = organizationService.getById(transfer.getTransferOutOrgId());
                    if (outOrg != null) {
                        transfer.setTransferOutOrgName(outOrg.getName());
                        transfer.setTransferOutOrgFullName(outOrg.getFullName());
                    }
                }
                
                // 处理转入组织名称
                if ((transfer.getTransferInOrgName() == null || transfer.getTransferInOrgName().isEmpty()) && transfer.getTransferInOrgId() != null) {
                    Organization inOrg = organizationService.getById(transfer.getTransferInOrgId());
                    if (inOrg != null) {
                        transfer.setTransferInOrgName(inOrg.getName());
                        transfer.setTransferInOrgFullName(inOrg.getFullName());
                    }
                }
                
                // 如果仍然没有组织名称（可能是因为组织被删除），设置一个默认值
                if (transfer.getTransferOutOrgName() == null || transfer.getTransferOutOrgName().isEmpty()) {
                    transfer.setTransferOutOrgName("未知组织");
                    transfer.setTransferOutOrgFullName("未知组织");
                }
                
                if (transfer.getTransferInOrgName() == null || transfer.getTransferInOrgName().isEmpty()) {
                    transfer.setTransferInOrgName("未知组织");
                    transfer.setTransferInOrgFullName("未知组织");
                }
            }
        }
        
        return pageResult;
    }
    
    @Override
    public Transfer getTransferDetail(Integer id) {
        Transfer transfer = transferMapper.selectTransferDetail(id);
        if (transfer == null) {
            throw new ResourceNotFoundException("转接申请", "id", id);
        }
        
        // 确保申请人姓名字段有值
        if ((transfer.getTransferUserName() == null || transfer.getTransferUserName().isEmpty()) && transfer.getTransferUserId() != null) {
            User user = userService.getById(transfer.getTransferUserId());
            if (user != null) {
                transfer.setTransferUserName(user.getName());
                transfer.setTransferUser(user);
            } else {
                transfer.setTransferUserName("用户ID:" + transfer.getTransferUserId());
            }
        }
        
        // 确保组织名称字段有值
        if (transfer.getTransferOutOrgName() == null || transfer.getTransferOutOrgName().isEmpty()) {
            Organization outOrg = organizationService.getById(transfer.getTransferOutOrgId());
            if (outOrg != null) {
                transfer.setTransferOutOrgName(outOrg.getName());
                transfer.setTransferOutOrgFullName(outOrg.getFullName());
            }
        }
        
        if (transfer.getTransferInOrgName() == null || transfer.getTransferInOrgName().isEmpty()) {
            Organization inOrg = organizationService.getById(transfer.getTransferInOrgId());
            if (inOrg != null) {
                transfer.setTransferInOrgName(inOrg.getName());
                transfer.setTransferInOrgFullName(inOrg.getFullName());
            }
        }
        
        return transfer;
    }
    
    @Override
    @Transactional
    public boolean createTransfer(Transfer transfer) {
        // 验证用户
        User user = userService.getById(transfer.getTransferUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证转出组织
        Organization outOrg = organizationService.getById(transfer.getTransferOutOrgId());
        if (outOrg == null) {
            throw new BusinessException("转出组织不存在");
        }
        
        // 验证转入组织
        Organization inOrg = organizationService.getById(transfer.getTransferInOrgId());
        if (inOrg == null) {
            throw new BusinessException("转入组织不存在");
        }
        
        // 检查用户是否已有正在进行的转接申请
        LambdaQueryWrapper<Transfer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Transfer::getTransferUserId, transfer.getTransferUserId())
               .le(Transfer::getStatusCode, 2);  // 状态小于等于2表示申请正在进行中
        
        long count = count(wrapper);
        if (count > 0) {
            throw new BusinessException("该用户已有正在进行的转接申请");
        }
        
        // 设置申请时间、过期时间和状态
        LocalDateTime now = LocalDateTime.now();
        transfer.setApplicationTime(now);
        transfer.setExpireTime(now.plusMonths(3));  // 3个月后过期
        transfer.setStatus(Transfer.STATUS_APPLYING);
        transfer.setStatusCode(Transfer.STATUS_CODE_APPLYING);
        transfer.setCreateTime(now);
        transfer.setUpdateTime(now);
        
        return save(transfer);
    }
    
    @Override
    @Transactional
    public boolean outApprove(Integer id, boolean approved, Integer approverId, String remark) {
        Transfer transfer = getById(id);
        if (transfer == null) {
            throw new ResourceNotFoundException("转接申请", "id", id);
        }
        
        // 检查状态是否为申请中或转出审批中
        if (!Transfer.STATUS_APPLYING.equals(transfer.getStatus()) && !Transfer.STATUS_OUT_APPROVING.equals(transfer.getStatus())) {
            throw new BusinessException("当前状态不允许进行转出审批");
        }
        
        // 更新审批信息
        LocalDateTime now = LocalDateTime.now();
        transfer.setOutApprovalTime(now);
        transfer.setOutApproverId(approverId);
        transfer.setOutApproved(approved);
        transfer.setOutApprovalRemark(remark);
        transfer.setUpdateTime(now);
        
        // 更新状态
        if (approved) {
            // 转出审批通过，状态变为转入审批中
            transfer.setStatus(Transfer.STATUS_IN_APPROVING);
            transfer.setStatusCode(Transfer.STATUS_CODE_IN_APPROVING);
        } else {
            // 转出审批不通过，状态变为已拒绝
            transfer.setStatus(Transfer.STATUS_REJECTED);
            transfer.setStatusCode(Transfer.STATUS_CODE_REJECTED);
        }
        
        return updateById(transfer);
    }
    
    @Override
    @Transactional
    public boolean inApprove(Integer id, boolean approved, Integer approverId, String remark) {
        Transfer transfer = getById(id);
        if (transfer == null) {
            throw new ResourceNotFoundException("转接申请", "id", id);
        }
        
        // 检查状态是否为转入审批中
        if (!Transfer.STATUS_IN_APPROVING.equals(transfer.getStatus())) {
            throw new BusinessException("当前状态不允许进行转入审批");
        }
        
        // 更新审批信息
        LocalDateTime now = LocalDateTime.now();
        transfer.setInApprovalTime(now);
        transfer.setInApproverId(approverId);
        transfer.setInApproved(approved);
        transfer.setInApprovalRemark(remark);
        transfer.setUpdateTime(now);
        
        // 更新状态
        if (approved) {
            // 转入审批通过，状态变为已通过，同时更新用户组织
            transfer.setStatus(Transfer.STATUS_APPROVED);
            transfer.setStatusCode(Transfer.STATUS_CODE_APPROVED);
            
            // 更新用户组织
            User user = userService.getById(transfer.getTransferUserId());
            if (user != null) {
                user.setOrganization(transfer.getTransferInOrgId());
                userService.updateById(user);
            }
        } else {
            // 转入审批不通过，状态变为已拒绝
            transfer.setStatus(Transfer.STATUS_REJECTED);
            transfer.setStatusCode(Transfer.STATUS_CODE_REJECTED);
        }
        
        return updateById(transfer);
    }

    @Override
    @Transactional
    public boolean cancelTransfer(Integer id, Integer userId) {
        Transfer transfer = getById(id);
        if (transfer == null) {
            throw new ResourceNotFoundException("转接申请", "id", id);
        }
        
        // 检查申请人是否为当前用户
        if (!transfer.getTransferUserId().equals(userId)) {
            throw new BusinessException("只有申请人可以取消转接申请");
        }
        
        // 检查状态是否允许取消
        if (transfer.getStatusCode() > 2) {
            throw new BusinessException("当前状态不允许取消转接申请");
            }
        
        // 直接删除申请
        return removeById(id);
    }
    
    /**
     * 处理转接申请列表，确保每条记录都有组织名称
     * 
     * @param transfers 转接申请列表
     * @return 处理后的转接申请列表
     */
    private List<Transfer> processTransferList(List<Transfer> transfers) {
        if (transfers == null || transfers.isEmpty()) {
            return transfers;
        }
        
        for (Transfer transfer : transfers) {
            // 处理申请人姓名
            if ((transfer.getTransferUserName() == null || transfer.getTransferUserName().isEmpty()) && transfer.getTransferUserId() != null) {
                User user = userService.getById(transfer.getTransferUserId());
                if (user != null) {
                    transfer.setTransferUserName(user.getName());
                    // 设置关联的用户对象，便于前端使用
                    transfer.setTransferUser(user);
                } else {
                    // 如果用户不存在，使用ID作为默认显示
                    transfer.setTransferUserName("用户ID:" + transfer.getTransferUserId());
                }
            }
            
            // 处理转出组织名称
            if ((transfer.getTransferOutOrgName() == null || transfer.getTransferOutOrgName().isEmpty()) && transfer.getTransferOutOrgId() != null) {
                Organization outOrg = organizationService.getById(transfer.getTransferOutOrgId());
                if (outOrg != null) {
                    transfer.setTransferOutOrgName(outOrg.getName());
                    transfer.setTransferOutOrgFullName(outOrg.getFullName());
                }
            }
            
            // 处理转入组织名称
            if ((transfer.getTransferInOrgName() == null || transfer.getTransferInOrgName().isEmpty()) && transfer.getTransferInOrgId() != null) {
                Organization inOrg = organizationService.getById(transfer.getTransferInOrgId());
                if (inOrg != null) {
                    transfer.setTransferInOrgName(inOrg.getName());
                    transfer.setTransferInOrgFullName(inOrg.getFullName());
                }
            }
            
            // 如果仍然没有组织名称（可能是因为组织被删除），设置一个默认值
            if (transfer.getTransferOutOrgName() == null || transfer.getTransferOutOrgName().isEmpty()) {
                transfer.setTransferOutOrgName("未知组织");
                transfer.setTransferOutOrgFullName("未知组织");
            }
            
            if (transfer.getTransferInOrgName() == null || transfer.getTransferInOrgName().isEmpty()) {
                transfer.setTransferInOrgName("未知组织");
                transfer.setTransferInOrgFullName("未知组织");
            }
        }
        
        return transfers;
    }

    @Override
    public List<Transfer> getTransfersByUserId(Integer userId) {
        List<Transfer> transfers = transferMapper.selectTransfersByUserId(userId);
        return processTransferList(transfers);
    }

    @Override
    public List<Transfer> getOutgoingTransfersByOrgId(Integer organizationId) {
        List<Transfer> transfers = transferMapper.selectOutgoingTransfersByOrgId(organizationId);
        return processTransferList(transfers);
    }

    @Override
    public List<Transfer> getIncomingTransfersByOrgId(Integer organizationId) {
        List<Transfer> transfers = transferMapper.selectIncomingTransfersByOrgId(organizationId);
        return processTransferList(transfers);
    }

    @Override
    public List<Transfer> getApprovedOutTransfersByOrgId(Integer organizationId) {
        LambdaQueryWrapper<Transfer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Transfer::getTransferOutOrgId, organizationId)
               .eq(Transfer::getStatusCode, Transfer.STATUS_CODE_IN_APPROVING);  // 状态为2（转入审批中）
        
        List<Transfer> transfers = list(wrapper);
        return processTransferList(transfers);
    }

    @Override
    public List<Transfer> getExpiredTransfers(LocalDateTime currentTime) {
        LambdaQueryWrapper<Transfer> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(Transfer::getExpireTime, currentTime)
               .le(Transfer::getStatusCode, 2);  // 状态小于等于2表示申请正在进行中
        return list(wrapper);
    }

    @Override
    public boolean exists(Integer transferId) {
        if (transferId == null) {
            return false;
        }
        return getById(transferId) != null;
    }

    @Override
    public boolean checkAccessPermission(Integer transferId, Integer userId) {
        if (transferId == null || userId == null) {
            return false;
        }
        
        Transfer transfer = getById(transferId);
        if (transfer == null) {
            return false;
        }
        
        // 检查用户是否为申请人
        if (transfer.getTransferUserId().equals(userId)) {
            return true;
        }
        
        // 获取用户信息
        User user = userService.getById(userId);
        if (user == null) {
            return false;
        }
        
        // 检查用户是否为管理员
        if (roleService.hasRole(userId, "ADMIN")) {
            return true;
        }
        
        // 检查用户是否为组织管理员
        boolean isOrgAdmin = roleService.hasRole(userId, "ORGANIZATION_ADMIN");
        if (!isOrgAdmin) {
            return false;
        }
        
        // 检查用户是否属于转入或转出组织
        Integer userOrganizationId = user.getOrganization();
        if (userOrganizationId == null) {
            return false;
        }
        
        return userOrganizationId.equals(transfer.getTransferOutOrgId()) || 
               userOrganizationId.equals(transfer.getTransferInOrgId());
    }
} 