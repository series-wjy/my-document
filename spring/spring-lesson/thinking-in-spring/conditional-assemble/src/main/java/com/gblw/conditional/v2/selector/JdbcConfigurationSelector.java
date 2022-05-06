package com.gblw.conditional.v2.selector;

import com.gblw.conditional.v2.configuration.JdbcConfigurationAdapter;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月15日 14:24:00
 */
public class JdbcConfigurationSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        List<String> strings = SpringFactoriesLoader
                .loadFactoryNames(JdbcConfigurationAdapter.class, this.getClass().getClassLoader());
        return strings.toArray(new String[strings.size()]);
    }
}
