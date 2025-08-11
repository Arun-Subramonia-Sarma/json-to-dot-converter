package com.example.converter.service;

import com.example.converter.config.DiagramProperties;
import com.example.converter.model.DiagramModel;
import com.example.converter.model.EntityModel;
import com.example.converter.model.RelationshipModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for generating DOT diagrams from JSON models
 */
@Service
public class DiagramService {

    private static final Logger logger = LoggerFactory.getLogger(DiagramService.class);

    @Autowired
    private DiagramProperties diagramProperties;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private StyleService styleService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Generate DOT content from JSON model
     */
    public String generateDotContent(JsonNode jsonData, String configFile) throws Exception {
        // Load custom configuration if provided
        DiagramProperties config = loadConfiguration(configFile);

        // Parse JSON into model objects
        DiagramModel diagram = parseJsonModel(jsonData);

        // Apply styling
        styleService.applyStyles(diagram, config);

        // Generate DOT using Velocity template
        return renderTemplate(diagram, config);
    }

    private DiagramProperties loadConfiguration(String configFile) throws Exception {
        if (configFile == null) {
            return diagramProperties; // Use default from application.yaml
        }

        logger.info("Loading custom configuration from: {}", configFile);

        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        Map<String, Object> customConfig = yamlMapper.readValue(new File(configFile), Map.class);

        // Merge with default configuration
        DiagramProperties mergedConfig = objectMapper.convertValue(customConfig, DiagramProperties.class);

        // TODO: Implement proper configuration merging logic
        return mergedConfig;
    }

    private DiagramModel parseJsonModel(JsonNode jsonData) {
        DiagramModel diagram = new DiagramModel();

        // Parse metadata
        JsonNode metadata = jsonData.path("metadata");
        if (!metadata.isMissingNode()) {
            diagram.setTitle(getStringValue(metadata, "title", "Data Model"));
            diagram.setVersion(getStringValue(metadata, "version", "1.0"));
            diagram.setDescription(getStringValue(metadata, "description", ""));
        }

        // Parse diagram settings
        JsonNode settings = jsonData.path("diagram_settings");
        if (!settings.isMissingNode()) {
            diagram.setRankdir(getStringValue(settings, "rankdir", "TB"));
        }

        // Parse entities
        JsonNode entitiesNode = jsonData.path("entities");
        if (entitiesNode.isArray()) {
            List<EntityModel> entityList = new ArrayList<>();
            for (JsonNode entityNode : entitiesNode) {
                entityList.add(parseEntity(entityNode));
            }
            diagram.setEntities(entityList);
        }

        // Parse relationships
        JsonNode relationshipsNode = jsonData.path("relationships");
        if (relationshipsNode.isArray()) {
            List<RelationshipModel> relationshipList = new ArrayList<>();
            for (JsonNode relationshipNode : relationshipsNode) {
                relationshipList.add(parseRelationship(relationshipNode));
            }
            diagram.setRelationships(relationshipList);
        }

        // Parse layout hints
        JsonNode layoutHints = jsonData.path("layout_hints");
        if (!layoutHints.isMissingNode()) {
            JsonNode sameRankGroups = layoutHints.path("same_rank_groups");
            if (sameRankGroups.isArray()) {
                List<List<String>> groups = new ArrayList<>();
                for (JsonNode group : sameRankGroups) {
                    if (group.isArray()) {
                        List<String> entityIds = new ArrayList<>();
                        for (JsonNode entityIdNode : group) {
                            entityIds.add(entityIdNode.asText());
                        }
                        groups.add(entityIds);
                    }
                }
                diagram.setSameRankGroups(groups);
            }
        }

        return diagram;
    }

    private EntityModel parseEntity(JsonNode entityNode) {
        EntityModel entityModel = new EntityModel();

        entityModel.setId(entityNode.get("id").asText());
        entityModel.setName(entityNode.get("name").asText());
        entityModel.setDescription(getStringValue(entityNode, "description", ""));

        // Parse fields
        JsonNode fieldsNode = entityNode.path("fields");
        if (fieldsNode.isArray()) {
            List<EntityModel.Field> fieldList = new ArrayList<>();
            for (JsonNode fieldNode : fieldsNode) {
                EntityModel.Field fieldModel = new EntityModel.Field();
                fieldModel.setName(fieldNode.get("name").asText());
                fieldModel.setType(fieldNode.get("type").asText());
                fieldModel.setRequired(fieldNode.path("is_required").asBoolean(false));
                fieldModel.setKey(fieldNode.path("is_key").asBoolean(false));
                fieldModel.setDescription(getStringValue(fieldNode, "description", ""));
                fieldList.add(fieldModel);
            }
            entityModel.setFields(fieldList);
        }

        // Parse special sections
        JsonNode specialSectionsNode = entityNode.path("special_sections");
        if (specialSectionsNode.isArray()) {
            List<EntityModel.SpecialSection> sectionList = new ArrayList<>();
            for (JsonNode sectionNode : specialSectionsNode) {
                EntityModel.SpecialSection sectionModel = new EntityModel.SpecialSection();
                sectionModel.setName(sectionNode.get("name").asText());
                sectionModel.setType(sectionNode.get("type").asText());
                sectionModel.setStyle(getStringValue(sectionNode, "style", "bold_red"));
                sectionModel.setIs_required(sectionNode.path("is_required").asBoolean(false));
                sectionList.add(sectionModel);
            }
            entityModel.setSpecialSections(sectionList);
        }

        // Parse constraints
        JsonNode constraintsNode = entityNode.path("constraints");
        if (constraintsNode.isArray()) {
            List<String> constraintList = new ArrayList<>();
            for (JsonNode constraintNode : constraintsNode) {
                constraintList.add(constraintNode.asText());
            }
            entityModel.setConstraints(constraintList);
        }

        return entityModel;
    }

    private RelationshipModel parseRelationship(JsonNode relationshipNode) {
        RelationshipModel relationshipModel = new RelationshipModel();

        relationshipModel.setId(getStringValue(relationshipNode, "id", ""));
        relationshipModel.setFromEntity(relationshipNode.get("from_entity").asText());
        relationshipModel.setToEntity(relationshipNode.get("to_entity").asText());
        relationshipModel.setLabel(relationshipNode.get("label").asText());
        relationshipModel.setType(getStringValue(relationshipNode, "relationship_type", "one_to_many"));
        relationshipModel.setDescription(getStringValue(relationshipNode, "description", ""));

        return relationshipModel;
    }

    private String renderTemplate(DiagramModel diagram, DiagramProperties config) throws Exception {
        VelocityContext context = new VelocityContext();
        context.put("diagram", diagram);
        context.put("config", config);
        context.put("styleService", styleService);

        StringWriter writer = new StringWriter();

        try {
            String templateName = config.getTemplates().getMainTemplate();
            velocityEngine.getTemplate(templateName).merge(context, writer);
            return writer.toString();
        } catch (Exception e) {
            // Fallback to simple template generation if Velocity fails
            logger.warn("Velocity template failed, falling back to simple generation: {}", e.getMessage());
            return generateSimpleDot(diagram, config);
        }
    }

    /**
     * Fallback method to generate DOT without Velocity templates
     */
    private String generateSimpleDot(DiagramModel diagram, DiagramProperties config) {
        StringBuilder dot = new StringBuilder();

        // Header
        dot.append("// ").append(diagram.getTitle()).append("\n");
        if (diagram.getVersion() != null) {
            dot.append("// Version: ").append(diagram.getVersion()).append("\n");
        }
        dot.append("\n");

        // Start digraph
        String diagramName = diagram.getTitle().toLowerCase().replaceAll("[\\s-]", "_");
        dot.append("digraph ").append(diagramName).append(" {\n");
        dot.append("    rankdir=").append(diagram.getRankdir()).append(";\n");
        // Apply node defaults from configuration
        Map<String, String> nodeDefaults = config.getSettings().getNodeDefaults();
        if (!nodeDefaults.isEmpty()) {
            dot.append("    node [");
            boolean first = true;
            for (Map.Entry<String, String> entry : nodeDefaults.entrySet()) {
                if (!first) dot.append(", ");
                dot.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
                first = false;
            }
            dot.append("];\n");
        }
        dot.append("\n");

        // Entities
        for (EntityModel entity : diagram.getEntities()) {
            dot.append(generateEntityDot(entity, config));
        }

        // Relationships
        if (diagram.getRelationships() != null && !diagram.getRelationships().isEmpty()) {
            dot.append("    // Relationships\n");
            for (RelationshipModel relationship : diagram.getRelationships()) {
                dot.append(generateRelationshipDot(relationship, config));
            }
            dot.append("\n");
        }

        // Layout hints
        if (diagram.getSameRankGroups() != null && !diagram.getSameRankGroups().isEmpty()) {
            dot.append("    // Layout hints\n");
            for (List<String> group : diagram.getSameRankGroups()) {
                dot.append("    {rank=same; ");
                for (int i = 0; i < group.size(); i++) {
                    dot.append(group.get(i));
                    if (i < group.size() - 1) dot.append("; ");
                }
                dot.append(";}\n");
            }
        }

        dot.append("}\n");
        return dot.toString();
    }

    private String generateEntityDot(EntityModel entity, DiagramProperties config) {
        // Try to use the entity template first
        try {
            VelocityContext context = new VelocityContext();
            context.put("entity", entity);
            context.put("config", config);
            context.put("styleService", styleService);

            StringWriter writer = new StringWriter();
            String templateName = config.getTemplates().getEntityTemplate();
            velocityEngine.getTemplate(templateName).merge(context, writer);
            return writer.toString();
        } catch (Exception e) {
            logger.warn("Entity template failed for entity {}, using simple fallback: {}", entity.getId(), e.getMessage());
            return generateSimpleEntityDot(entity);
        }
    }

    /**
     * Ultra-simple fallback for entity generation without HTML tables
     */
    private String generateSimpleEntityDot(EntityModel entity) {
        StringBuilder dot = new StringBuilder();
        
        dot.append("    // ").append(entity.getName()).append("\n");
        dot.append("    ").append(entity.getId()).append(" [label=\"").append(entity.getName());
        
        // Add fields as simple text
        if (entity.getFields() != null && !entity.getFields().isEmpty()) {
            dot.append("\\n");
            for (EntityModel.Field field : entity.getFields()) {
                if (field.isRequired()) {
                    dot.append("+ ");
                } else {
                    dot.append("- ");
                }
                dot.append(field.getName()).append(": ").append(field.getType()).append("\\n");
            }
        }
        
        dot.append("\", shape=box];\n");
        return dot.toString();
    }

    private String generateRelationshipDot(RelationshipModel relationship, DiagramProperties config) {
        // Try to use the relationship template first
        try {
            VelocityContext context = new VelocityContext();
            context.put("relationship", relationship);
            context.put("config", config);
            context.put("styleService", styleService);

            StringWriter writer = new StringWriter();
            String templateName = config.getTemplates().getRelationshipTemplate();
            velocityEngine.getTemplate(templateName).merge(context, writer);
            return writer.toString();
        } catch (Exception e) {
            logger.warn("Relationship template failed for relationship {}, using simple fallback: {}", relationship.getId(), e.getMessage());
            return generateSimpleRelationshipDot(relationship);
        }
    }

    /**
     * Ultra-simple fallback for relationship generation
     */
    private String generateSimpleRelationshipDot(RelationshipModel relationship) {
        return "    " + relationship.getFromEntity() + " -> " + relationship.getToEntity() + 
               " [label=\"" + relationship.getLabel() + "\"];\n";
    }

    private String getStringValue(JsonNode node, String fieldName, String defaultValue) {
        JsonNode fieldNode = node.path(fieldName);
        return fieldNode.isMissingNode() ? defaultValue : fieldNode.asText();
    }
}