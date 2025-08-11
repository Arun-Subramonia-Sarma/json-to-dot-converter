package com.example.converter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for diagram generation
 */
@Component
@ConfigurationProperties(prefix = "diagram")
public class DiagramProperties {

    @NestedConfigurationProperty
    private Settings settings = new Settings();

    @NestedConfigurationProperty
    private Templates templates = new Templates();

    @NestedConfigurationProperty
    private Styles styles = new Styles();

    // Getters and setters
    public Settings getSettings() { return settings; }
    public void setSettings(Settings settings) { this.settings = settings; }

    public Templates getTemplates() { return templates; }
    public void setTemplates(Templates templates) { this.templates = templates; }

    public Styles getStyles() { return styles; }
    public void setStyles(Styles styles) { this.styles = styles; }

    /**
     * Diagram settings
     */
    public static class Settings {
        private String rankdir = "TB";
        private Map<String, String> nodeDefaults = new HashMap<String, String>() {{
            put("fontname", "Arial");
            put("shape", "none");
        }};
        private TableSettings tableSettings = new TableSettings();

        public String getRankdir() { return rankdir; }
        public void setRankdir(String rankdir) { this.rankdir = rankdir; }

        public Map<String, String> getNodeDefaults() { return nodeDefaults; }
        public void setNodeDefaults(Map<String, String> nodeDefaults) { this.nodeDefaults = nodeDefaults; }

        public TableSettings getTableSettings() { return tableSettings; }
        public void setTableSettings(TableSettings tableSettings) { this.tableSettings = tableSettings; }
    }

    /**
     * Table formatting settings
     */
    public static class TableSettings {
        private String border = "2";
        private String cellBorder = "1";
        private String cellSpacing = "0";
        private String cellPadding = "2";
        private String separatorHeight = "2";

        public String getBorder() { return border; }
        public void setBorder(String border) { this.border = border; }

        public String getCellBorder() { return cellBorder; }
        public void setCellBorder(String cellBorder) { this.cellBorder = cellBorder; }

        public String getCellSpacing() { return cellSpacing; }
        public void setCellSpacing(String cellSpacing) { this.cellSpacing = cellSpacing; }

        public String getCellPadding() { return cellPadding; }
        public void setCellPadding(String cellPadding) { this.cellPadding = cellPadding; }

        public String getSeparatorHeight() { return separatorHeight; }
        public void setSeparatorHeight(String separatorHeight) { this.separatorHeight = separatorHeight; }
    }

    /**
     * Template configuration
     */
    public static class Templates {
        private String basePath = "classpath:/templates/";
        private String entityTemplate = "entity.vm";
        private String relationshipTemplate = "relationship.vm";
        private String mainTemplate = "diagram.vm";

        public String getBasePath() { return basePath; }
        public void setBasePath(String basePath) { this.basePath = basePath; }

        public String getEntityTemplate() { return entityTemplate; }
        public void setEntityTemplate(String entityTemplate) { this.entityTemplate = entityTemplate; }

        public String getRelationshipTemplate() { return relationshipTemplate; }
        public void setRelationshipTemplate(String relationshipTemplate) { this.relationshipTemplate = relationshipTemplate; }

        public String getMainTemplate() { return mainTemplate; }
        public void setMainTemplate(String mainTemplate) { this.mainTemplate = mainTemplate; }
    }

    /**
     * Style configuration
     */
    public static class Styles {
        @NestedConfigurationProperty
        private StyleDefinition defaultStyles = new StyleDefinition();

        private Map<String, StyleDefinition> entities = new HashMap<>();

        public StyleDefinition getDefault() { return defaultStyles; }
        public void setDefault(StyleDefinition defaultStyles) { this.defaultStyles = defaultStyles; }

        public Map<String, StyleDefinition> getEntities() { return entities; }
        public void setEntities(Map<String, StyleDefinition> entities) { this.entities = entities; }
    }

    /**
     * Style definition for entities
     */
    public static class StyleDefinition {
        @NestedConfigurationProperty
        private StyleSection header = new StyleSection();

        @NestedConfigurationProperty
        private StyleSection body = new StyleSection();

        @NestedConfigurationProperty
        private StyleSection separator = new StyleSection();

        @NestedConfigurationProperty
        private StyleSection mandatory = new StyleSection();

        @NestedConfigurationProperty
        private StyleSection specialSection = new StyleSection();

        @NestedConfigurationProperty
        private StyleSection constraint = new StyleSection();

        @NestedConfigurationProperty
        private StyleSection relationship = new StyleSection();

        // Getters and setters
        public StyleSection getHeader() { return header; }
        public void setHeader(StyleSection header) { this.header = header; }

        public StyleSection getBody() { return body; }
        public void setBody(StyleSection body) { this.body = body; }

        public StyleSection getSeparator() { return separator; }
        public void setSeparator(StyleSection separator) { this.separator = separator; }

        public StyleSection getMandatory() { return mandatory; }
        public void setMandatory(StyleSection mandatory) { this.mandatory = mandatory; }

        public StyleSection getSpecialSection() { return specialSection; }
        public void setSpecialSection(StyleSection specialSection) { this.specialSection = specialSection; }

        public StyleSection getConstraint() { return constraint; }
        public void setConstraint(StyleSection constraint) { this.constraint = constraint; }

        public StyleSection getRelationship() { return relationship; }
        public void setRelationship(StyleSection relationship) { this.relationship = relationship; }
    }

    /**
     * Style section properties
     */
    public static class StyleSection {
        private String bgcolor;
        private String forecolor;
        private String color;
        private String font;
        private Integer fontSize;
        private Boolean bold;
        private String style;

        // Getters and setters
        public String getBgcolor() { return bgcolor; }
        public void setBgcolor(String bgcolor) { this.bgcolor = bgcolor; }

        public String getForecolor() { return forecolor; }
        public void setForecolor(String forecolor) { this.forecolor = forecolor; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public String getFont() { return font; }
        public void setFont(String font) { this.font = font; }

        public Integer getFontSize() { return fontSize; }
        public void setFontSize(Integer fontSize) { this.fontSize = fontSize; }

        public Boolean getBold() { return bold; }
        public void setBold(Boolean bold) { this.bold = bold; }

        public String getStyle() { return style; }
        public void setStyle(String style) { this.style = style; }
    }
}