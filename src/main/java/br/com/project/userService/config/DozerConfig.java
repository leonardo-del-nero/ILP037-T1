package br.com.project.userService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

@Configuration
public class DozerConfig {

    @Bean
    Mapper dozerBeanMapper() {
        return DozerBeanMapperBuilder.buildDefault();
    }
}
