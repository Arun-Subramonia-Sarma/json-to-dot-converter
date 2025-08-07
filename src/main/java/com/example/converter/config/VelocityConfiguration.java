package com.example.converter.config;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Velocity template engine configuration
 * Uses string-based properties to avoid constant compatibility issues
 */
@Configuration
public class VelocityConfiguration {

    @Bean
    public VelocityEngine velocityEngine() {
        Properties properties = new Properties();

        // Resource loader configuration
        properties.setProperty("resource.loader", "classpath");
        properties.setProperty("classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        // Template encoding
        properties.setProperty("input.encoding", "UTF-8");
        properties.setProperty("output.encoding", "UTF-8");

        // Parser configuration
        properties.setProperty("parser.pool.size", "20");

        // Runtime behavior
        properties.setProperty("runtime.references.strict", "false");
        properties.setProperty("runtime.interpolate.string.literals", "true");

        // Logging configuration - disable to avoid console spam
        properties.setProperty("runtime.log.logsystem.class",
                "org.apache.velocity.runtime.log.NullLogChute");

        // Macro configuration
        properties.setProperty("velocimacro.library.autoreload", "false");
        properties.setProperty("velocimacro.permissions.allow.inline.replace.global", "false");

        // Resource caching
        properties.setProperty("resource.manager.defaultcache.size", "89");
        properties.setProperty("resource.manager.cache.default.size", "89");

        VelocityEngine velocityEngine = new VelocityEngine();

        try {
            velocityEngine.init(properties);
            return velocityEngine;
        } catch (Exception e) {
            // Fallback to minimal configuration
            Properties fallbackProps = new Properties();
            fallbackProps.setProperty("resource.loader", "classpath");
            fallbackProps.setProperty("classpath.resource.loader.class",
                    "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            fallbackProps.setProperty("runtime.references.strict", "false");

            VelocityEngine fallbackEngine = new VelocityEngine();
            fallbackEngine.init(fallbackProps);
            return fallbackEngine;
        }
    }
}