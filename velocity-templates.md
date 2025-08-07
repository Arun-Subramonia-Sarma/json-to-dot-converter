# Velocity Template Files Documentation

## Overview
The `src/main/resources/templates` directory contains three Apache Velocity template files that work together to generate DOT graph files from JSON input. These templates create structured diagrams with entities, relationships, and styling.

## Template Files

### 1. `diagram.vm` - Main DOT Diagram Template
**Purpose**: Root template that orchestrates the complete DOT graph generation

**Key Features**:
- **Header Generation**: Creates diagram title, version, and description comments (`src/main/resources/templates/diagram.vm:2-8`)
- **Diagram Structure**: Generates `digraph` declaration with sanitized name (`src/main/resources/templates/diagram.vm:10-11`)
- **Global Configuration**: Sets rankdir and node defaults from config (`src/main/resources/templates/diagram.vm:12-18`)
- **Entity Rendering**: Iterates through entities and includes `entity.vm` template (`src/main/resources/templates/diagram.vm:20-22`)
- **Relationship Processing**: Handles relationships via `relationship.vm` template (`src/main/resources/templates/diagram.vm:24-29`)
- **Layout Hints**: Applies same-rank groupings for layout control (`src/main/resources/templates/diagram.vm:31-36`)

**Template Structure**:
```velocity
digraph $diagramName {
    // Global settings
    // Entities (via entity.vm)
    // Relationships (via relationship.vm) 
    // Layout hints
}
```

### 2. `entity.vm` - Entity/Table Template
**Purpose**: Renders individual entities as HTML-like tables in DOT format

**Key Features**:
- **Style Integration**: Uses `$styleService.getEntityStyles()` for dynamic styling (`src/main/resources/templates/entity.vm:2`)
- **Table Structure**: Creates bordered table with header, fields, and optional sections
- **Field Rendering**: Distinguishes required vs optional fields with different formatting (`src/main/resources/templates/entity.vm:14-18`)
- **Dynamic Content**:
  - Header with entity name (`src/main/resources/templates/entity.vm:7-9`)
  - Field list with types (`src/main/resources/templates/entity.vm:12-20`)
  - Special sections (`src/main/resources/templates/entity.vm:21-32`)
  - Description and constraints (`src/main/resources/templates/entity.vm:33-41`)

**Styling Elements**:
- `header_bg/header_text`: Header styling
- `body_bg/body_text`: Body content styling  
- `mandatory_text`: Required field highlighting
- `separator_color`: Visual separators
- `constraint_bg`: Constraint highlighting

### 3. `relationship.vm` - Relationship Template
**Purpose**: Renders connections between entities

**Key Features**:
- **Simple Edge Definition**: Creates directed edges between entities (`src/main/resources/templates/relationship.vm:3`)
- **Style Application**: Uses `$styleService.getRelationshipStyles()` for consistent styling (`src/main/resources/templates/relationship.vm:2`)
- **Properties**: Sets label, font size, and color for relationships

**Output Format**:
```dot
fromEntity -> toEntity [label="relationship_label", fontsize=12, color="#000000"];
```

## Template Integration

### Service Dependencies
- **StyleService**: Provides styling configuration for entities and relationships
- **Config Settings**: Supplies global diagram settings and node defaults

### Data Model Requirements
- **Diagram**: Contains title, version, description, rankdir, entities, relationships, sameRankGroups
- **Entity**: Includes id, name, fields, specialSections, description, constraints
- **Field**: Has name, type, required properties
- **Relationship**: Contains fromEntity, toEntity, label

### Template Flow
1. `diagram.vm` serves as the entry point
2. For each entity, `#parse("entity.vm")` is called
3. For each relationship, `#parse("relationship.vm")` is called
4. Final DOT graph is assembled with proper structure and styling