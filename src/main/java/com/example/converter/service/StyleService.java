package com.example.converter.service;

import com.example.converter.config.DiagramProperties;
import com.example.converter.model.DiagramModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing diagram styling
 */
@Service
public class StyleService {
    
    @Autowired
    private DiagramProperties diagramProperties;
    
    /**
     * Apply styles to the diagram model
     */
    public void applyStyles(DiagramModel diagram, DiagramProperties config) {
        // Styling is handled via templates and getEntityStyles method
        // This method can be used for any pre-processing if needed
    }
    
    /**
     * Get complete style configuration for an entity
     */
    public Map<String, String> getEntityStyles(String entityId) {
        Map<String, String> styles = new HashMap<>();
        
        // Get default styles
        DiagramProperties.StyleDefinition defaultStyles = diagramProperties.getStyles().getDefault();
        
        // Get entity-specific styles
        DiagramProperties.StyleDefinition entityStyles = 
            diagramProperties.getStyles().getEntities().get(entityId);
        
        // Header styles
        styles.put("header_bg", getStyleValue(entityStyles, defaultStyles, "header", "bgcolor", "#333333"));
        styles.put("header_text", getStyleValue(entityStyles, defaultStyles, "header", "forecolor", "white"));
        styles.put("header_font", getStyleValue(entityStyles, defaultStyles, "header", "font", "Arial"));
        styles.put("header_font_size", getStyleValue(entityStyles, defaultStyles, "header", "fontSize", "12"));
        
        // Body styles
        styles.put("body_bg", getStyleValue(entityStyles, defaultStyles, "body", "bgcolor", "#FFFFFF"));
        styles.put("body_text", getStyleValue(entityStyles, defaultStyles, "body", "forecolor", "#000000"));
        
        // Separator styles
        styles.put("separator_color", getStyleValue(entityStyles, defaultStyles, "separator", "color", "#333333"));
        
        // Mandatory field styles
        styles.put("mandatory_bg", getStyleValue(entityStyles, defaultStyles, "mandatory", "bgcolor", "#FFFFFF"));
        styles.put("mandatory_text", getStyleValue(entityStyles, defaultStyles, "mandatory", "forecolor", "#DC2626"));
        
        // Special section styles
        styles.put("special_section_bg", getStyleValue(entityStyles, defaultStyles, "specialSection", "bgcolor", "#FFFFFF"));
        styles.put("special_section_text", getStyleValue(entityStyles, defaultStyles, "specialSection", "forecolor", "#DC2626"));
        
        // Constraint styles
        styles.put("constraint_bg", getStyleValue(entityStyles, defaultStyles, "constraint", "bgcolor", "#F5F5F5"));
        styles.put("constraint_text", getStyleValue(entityStyles, defaultStyles, "constraint", "forecolor", "#666666"));
        
        return styles;
    }
    
    /**
     * Get relationship styles
     */
    public Map<String, String> getRelationshipStyles() {
        Map<String, String> styles = new HashMap<>();
        
        DiagramProperties.StyleDefinition defaultStyles = diagramProperties.getStyles().getDefault();
        
        styles.put("color", getStyleValue(null, defaultStyles, "relationship", "color", "#666666"));
        styles.put("font_size", getStyleValue(null, defaultStyles, "relationship", "fontSize", "9"));
        styles.put("style", getStyleValue(null, defaultStyles, "relationship", "style", "solid"));
        
        return styles;
    }
    
    private String getStyleValue(DiagramProperties.StyleDefinition entityStyles,
                                DiagramProperties.StyleDefinition defaultStyles,
                                String section, String property, String fallback) {
        
        // Try entity-specific first
        if (entityStyles != null) {
            String value = getPropertyValue(entityStyles, section, property);
            if (value != null) {
                return value;
            }
        }
        
        // Fall back to default
        if (defaultStyles != null) {
            String value = getPropertyValue(defaultStyles, section, property);
            if (value != null) {
                return value;
            }
        }
        
        return fallback;
    }
    
    private String getPropertyValue(DiagramProperties.StyleDefinition styles, String section, String property) {
        DiagramProperties.StyleSection styleSection = getSectionFromStyles(styles, section);
        if (styleSection == null) {
            return null;
        }
        
        return switch (property) {
            case "bgcolor" -> styleSection.getBgcolor();
            case "forecolor" -> styleSection.getForecolor();
            case "color" -> styleSection.getColor();
            case "font" -> styleSection.getFont();
            case "fontSize" -> styleSection.getFontSize() != null ? styleSection.getFontSize().toString() : null;
            case "bold" -> styleSection.getBold() != null ? styleSection.getBold().toString() : null;
            case "style" -> styleSection.getStyle();
            default -> null;
        };
    }
    
    private DiagramProperties.StyleSection getSectionFromStyles(DiagramProperties.StyleDefinition styles, String section) {
        return switch (section) {
            case "header" -> styles.getHeader();
            case "body" -> styles.getBody();
            case "separator" -> styles.getSeparator();
            case "mandatory" -> styles.getMandatory();
            case "specialSection" -> styles.getSpecialSection();
            case "constraint" -> styles.getConstraint();
            case "relationship" -> styles.getRelationship();
            default -> null;
        };
    }
}