package com.wjy.jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月05日 15:31:00
 */
@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private String name;
    private String email;
    private String idCard;
}
