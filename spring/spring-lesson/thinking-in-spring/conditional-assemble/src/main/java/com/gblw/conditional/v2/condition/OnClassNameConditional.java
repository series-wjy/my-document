package com.gblw.conditional.v2.condition;

import com.gblw.conditional.v2.annotation.ConditionalOnClassName;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月15日 09:44:00
 */
public class OnClassNameConditional implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Object value = metadata.getAnnotationAttributes(ConditionalOnClassName.class.getName()).get("value");
        try {
            Class.forName(value.toString());
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }
}
