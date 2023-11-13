package com.ruoyi.data;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.*;

/**
 * @author Lizyang
 * @date Created in 2023/1/20 11:49
 * @description 类描述
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("test")
public class TestEntity extends BaseEntity {


    /**
     * demo 用户主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * demo 用户名称
     */
    @TableField(value = "values")
    private String values;

}
