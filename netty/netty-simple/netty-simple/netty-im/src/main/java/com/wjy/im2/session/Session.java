/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.im2.session;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wangjiayou 2019/7/8
 * @version ORAS v1.0
 */
@Data
@AllArgsConstructor
public class Session {

    private String userId;

    private String userName;
}
