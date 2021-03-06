/**
 * Copyright(C) 2019 Hangzhou zhaoyunxing Technology Co., Ltd. All rights reserved.
 */
package io.github.sunny.cherry.tomato.security.dto;

import io.github.sunny.cherry.tomato.core.common.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhaoyunxing
 * @date: 2019-10-16 13:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CherryAccountRoleDto extends BaseModel<Long> {
    /**
     * 账户id
     */
    private Long accountId;

    /**
     * 角色id
     */
    private Long roleId;
}