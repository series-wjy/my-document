/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im2.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangjiayou 2019/7/9
 * @version ORAS v1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {

    private String groupId;

    private String groupName;
}
