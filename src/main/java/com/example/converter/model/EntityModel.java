// EntityModel.java
package com.example.converter.model;

import java.util.List;

public class EntityModel {
    private String id;
    private String name;
    private String description;
    private List<Field> fields;
    private List<SpecialSection> specialSections;
    private List<String> constraints;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Field> getFields() { return fields; }
    public void setFields(List<Field> fields) { this.fields = fields; }

    public List<SpecialSection> getSpecialSections() { return specialSections; }
    public void setSpecialSections(List<SpecialSection> specialSections) { this.specialSections = specialSections; }

    public List<String> getConstraints() { return constraints; }
    public void setConstraints(List<String> constraints) { this.constraints = constraints; }

    // Inner classes
    public static class Field {
        private String name;
        private String type;
        private boolean required;
        private boolean key;
        private String description;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }

        public boolean isKey() { return key; }
        public void setKey(boolean key) { this.key = key; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class SpecialSection {
        private String name;
        private String type;
        private String style;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getStyle() { return style; }
        public void setStyle(String style) { this.style = style; }
    }
}