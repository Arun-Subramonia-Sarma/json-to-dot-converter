# JSON-to-DOT Converter - Project Documentation

## Overview

The JSON-to-DOT Converter is a Spring Boot application that transforms JSON data models into DOT (Graphviz) format for visualization of entity relationship diagrams. This project is part of a larger LangGraph Complete Course repository and serves as a practical example of data model visualization.

## Architecture

### Core Components

#### 1. Command Line Interface (CLI)
- **Main Class**: `JsonToDotCommand` (`src/main/java/com/example/converter/cli/JsonToDotCommand.java:33`)
- **Framework**: PicoCLI for command-line argument parsing
- **Features**:
    - File input/output validation
    - Verbose logging options
    - Auto-rendering with Graphviz
    - Sample file generation

#### 2. Service Layer
- **DiagramService** (`src/main/java/com/example/converter/service/DiagramService.java`)
    - Handles JSON parsing and DOT generation logic
    - Integrates with Velocity templating engine
- **StyleService** (`src/main/java/com/example/converter/service/StyleService.java`)
    - Manages diagram styling and theming
    - Supports custom color schemes and layouts

#### 3. Model Layer
- **DiagramModel** (`src/main/java/com/example/converter/model/DiagramModel.java`)
    - Root model representing the entire diagram
- **EntityModel** (`src/main/java/com/example/converter/model/EntityModel.java`)
    - Represents database entities with fields and metadata
- **RelationshipModel** (`src/main/java/com/example/converter/model/RelationshipModel.java`)
    - Defines relationships between entities

#### 4. Template System
- **Engine**: Apache Velocity 2.3
- **Templates Location**: `src/main/resources/templates/`
    - `diagram.vm`: Main diagram structure
    - `entity.vm`: Individual entity rendering
    - `relationship.vm`: Relationship connections
- **Configuration**: `VelocityConfiguration` (`src/main/java/com/example/converter/config/VelocityConfiguration.java`)

### Technology Stack

#### Backend Framework
- **Spring Boot**: 3.5.4
- **Java**: 21
- **Maven**: Build automation

#### Key Dependencies
- **Jackson**: JSON/YAML processing
- **Apache Velocity**: Template engine
- **PicoCLI**: Command-line interface
- **Lombok**: Code generation
- **SLF4J**: Logging framework

#### External Tools
- **Graphviz**: DOT file rendering (optional)
- **ImageMagick**: Image processing (optional)

## Data Model

### JSON Schema Structure

The application processes JSON files with the following structure:

```json
{
  "metadata": {
    "title": "Diagram Title",
    "version": "1.0",
    "description": "Description",
    "created_date": "2024-12-15T10:00:00Z",
    "created_by": "Author"
  },
  "diagram_settings": {
    "rankdir": "TB|LR|RL|BT",
    "node_defaults": {
      "fontname": "Arial",
      "shape": "none"
    }
  },
  "entities": [
    {
      "id": "unique_id",
      "name": "Entity Name",
      "fields": [
        {
          "name": "field_name",
          "type": "UUID|string|int|timestamp|ENUM",
          "is_required": true|false,
          "is_key": true|false,
          "description": "Field description"
        }
      ],
      "special_sections": [
        {
          "name": "Section Name",
          "type": "object",
          "style": "bold_red|custom_style"
        }
      ],
      "constraints": [
        "Business rule or constraint description"
      ],
      "description": "Entity description"
    }
  ],
  "relationships": [
    {
      "id": "relationship_id",
      "from_entity": "source_entity_id",
      "to_entity": "target_entity_id", 
      "label": "RELATIONSHIP_LABEL",
      "relationship_type": "one_to_one|one_to_many|many_to_many"
    }
  ],
  "layout_hints": {
    "same_rank_groups": [
      ["entity1", "entity2"]
    ]
  }
}
```

### Configuration Files

YAML configuration files can customize diagram appearance:

```yaml
diagram:
  settings:
    rankdir: LR
  
  styles:
    entities:
      entity_id:
        header:
          bgcolor: "#2563EB"
          forecolor: white
        body:
          bgcolor: "#EFF6FF"

logging:
  level:
    com.example.converter: DEBUG
```

## Usage

### Basic Commands

```bash
# Build the application
./mvnw clean package

# Convert JSON to DOT
java -jar target/converter-0.0.1-SNAPSHOT.jar input.json output.dot

# With custom configuration
java -jar target/converter-0.0.1-SNAPSHOT.jar input.json output.dot --config custom.yaml

# Auto-render with Graphviz
java -jar target/converter-0.0.1-SNAPSHOT.jar input.json output.dot --render --format png

# Generate sample files
java -jar target/converter-0.0.1-SNAPSHOT.jar --create-sample example

# Verbose output
java -jar target/converter-0.0.1-SNAPSHOT.jar input.json output.dot --verbose
```

### Command Line Options

| Option | Description | Default |
|--------|-------------|---------|
| `input.json` | Input JSON file (required) | - |
| `output.dot` | Output DOT file (required) | - |
| `-c, --config` | Custom configuration file | - |
| `--create-sample` | Create sample files with prefix | - |
| `-v, --verbose` | Enable verbose output | false |
| `-f, --format` | Output format (png, svg, pdf) | png |
| `--render` | Auto-render with Graphviz | false |
| `-h, --help` | Show help message | - |
| `--version` | Show version information | - |

### Example Workflow

1. **Create Sample Files**:
   ```bash
   java -jar converter.jar --create-sample demo
   ```

2. **Convert to DOT**:
   ```bash
   java -jar converter.jar demo_sample.json model.dot --config demo_config.yaml
   ```

3. **Generate Visualization**:
   ```bash
   dot -Tpng model.dot -o model.png
   # or use the built-in render option:
   java -jar converter.jar demo_sample.json model.dot --render
   ```

## Features

### Data Model Support
- **Entity Definitions**: Fields with types, constraints, and metadata
- **Relationships**: One-to-one, one-to-many, many-to-many
- **Special Sections**: Auditable fields, computed properties
- **Constraints**: Business rules and validation
- **Layout Hints**: Custom positioning and grouping

### Styling and Theming
- **Color Schemes**: Custom colors for entities and relationships
- **Typography**: Configurable fonts and text styles
- **Layout Control**: Direction, spacing, and alignment
- **Special Formatting**: Bold, italics, different text colors

### File Operations
- **Input Validation**: JSON syntax and schema validation
- **Output Generation**: Clean DOT file creation
- **Path Handling**: Robust file path validation and creation
- **Error Reporting**: Detailed error messages and logging

### Integration Options
- **Graphviz Integration**: Auto-rendering to PNG/SVG/PDF
- **Configuration Files**: YAML-based customization
- **Batch Processing**: Support for multiple file processing
- **CI/CD Ready**: Exit codes and logging for automation

## Sample Data Model

The project includes a comprehensive sample model (`schema-file/model.json`) representing a rate management system with:

- **12+ Entity Types**: Contracts, rates, locations, carriers, etc.
- **Complex Relationships**: Multiple relationship types and cardinalities
- **Field Variations**: Required/optional fields, different data types
- **Business Constraints**: Rate breaks, demurrage rules, service commitments
- **Styling Examples**: Special sections with custom formatting

## File Structure

```
src/
├── main/
│   ├── java/com/example/converter/
│   │   ├── ConverterApplication.java          # Main Spring Boot application
│   │   ├── cli/JsonToDotCommand.java          # CLI interface
│   │   ├── config/                            # Configuration classes
│   │   ├── model/                             # Data models
│   │   ├── service/                           # Business logic
│   │   └── util/FileUtils.java                # Utility functions
│   └── resources/
│       ├── application.yaml                   # Spring configuration
│       └── templates/                         # Velocity templates
├── test/                                      # Unit tests
schema-file/
├── model.json                                 # Sample data model
├── create-png.sh                             # Helper script
└── *.dot.png                                 # Generated diagrams
```

## Development

### Building and Testing

```bash
# Clean build
./mvnw clean compile

# Run tests  
./mvnw test

# Package application
./mvnw package

# Run in development mode
./mvnw spring-boot:run --args="input.json output.dot"
```

### Adding New Features

1. **New Entity Types**: Extend `EntityModel` and update templates
2. **Custom Relationships**: Modify `RelationshipModel` and rendering logic
3. **Styling Options**: Add properties to `DiagramProperties` and templates
4. **Output Formats**: Integrate additional rendering engines

### Template Customization

The Velocity templates can be customized for different diagram styles:

- **entity.vm**: Controls entity box rendering
- **relationship.vm**: Manages connection lines and labels
- **diagram.vm**: Overall diagram structure and settings

## Error Handling

The application provides comprehensive error handling:

- **File Validation**: Checks for file existence and permissions
- **JSON Parsing**: Detailed syntax error reporting
- **Template Processing**: Velocity template error handling
- **Graphviz Integration**: External tool execution error handling
- **Path Validation**: Safe file path construction and validation

## Performance Considerations

- **Memory Usage**: Efficient JSON parsing with Jackson streaming
- **Template Caching**: Velocity template compilation caching
- **Large Models**: Streaming processing for large data models
- **Output Optimization**: Minimal DOT file generation

## Security

- **Path Traversal Protection**: Safe file path validation
- **Input Sanitization**: JSON input validation and sanitization
- **Resource Limits**: Configurable memory and processing limits
- **Dependency Security**: Regular dependency updates and security scanning

---

This documentation provides a comprehensive overview of the JSON-to-DOT Converter project. For detailed API documentation and examples, refer to the individual class documentation and the sample files provided in the `schema-file/` directory.