// DiagramModel.java
package com.example.converter.model;

import java.util.List;

public class DiagramModel {
    private String title;
    private String version;
    private String description;
    private String rankdir = "TB";
    private List<EntityModel> entities;
    private List<RelationshipModel> relationships;
    private List<List<String>> sameRankGroups;
    
    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getRankdir() { return rankdir; }
    public void setRankdir(String rankdir) { this.rankdir = rankdir; }
    
    public List<EntityModel> getEntities() { return entities; }
    public void setEntities(List<EntityModel> entities) { this.entities = entities; }
    
    public List<RelationshipModel> getRelationships() { return relationships; }
    public void setRelationships(List<RelationshipModel> relationships) { this.relationships = relationships; }
    
    public List<List<String>> getSameRankGroups() { return sameRankGroups; }
    public void setSameRankGroups(List<List<String>> sameRankGroups) { this.sameRankGroups = sameRankGroups; }
}


