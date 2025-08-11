# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Overview

This is a JSON-to-DOT Converter project built with Spring Boot 3.5.4 and Java 21. The application transforms JSON data models into DOT (Graphviz) format for creating entity relationship diagrams with rich styling and configuration options.

## Architecture

### Core Components

The application follows a layered architecture:

- **CLI Layer** (`cli/`): PicoCLI-based command interface in `JsonToDotCommand.java`
- **Service Layer** (`service/`): Business logic with `DiagramService` handling JSON-to-DOT conversion and `StyleService` managing diagram styling
- **Model Layer** (`model/`): POJOs representing diagram structures (`DiagramModel`, `EntityModel`, `RelationshipModel`)
- **Configuration** (`config/`): Spring configuration classes including `VelocityConfiguration` for template engine setup
- **Templates** (`resources/templates/`): Apache Velocity templates (`entity.vm`, `relationship.vm`, `diagram.vm`) for DOT generation

### Template System

The application uses Apache Velocity 2.3 as its template engine to generate DOT files. Templates are located in `src/main/resources/templates/`:
- `diagram.vm`: Main diagram structure and wrapper
- `entity.vm`: Individual entity box rendering with HTML table formatting
- `relationship.vm`: Relationship lines and labels between entities

Fallback methods exist in `DiagramService` when template processing fails.

## Development Commands

### Build and Package
```bash
# Clean build
./mvnw clean compile

# Run tests
./mvnw test

# Package application (creates JAR in target/)
./mvnw package

# Run in development mode with arguments
./mvnw spring-boot:run --args="input.json output.dot"
```

### Application Usage
```bash
# Basic conversion
java -jar target/converter-0.0.1-SNAPSHOT.jar input.json output.dot

# With custom YAML configuration
java -jar target/converter-0.0.1-SNAPSHOT.jar input.json output.dot --config config.yaml

# Auto-render with Graphviz (requires dot command)
java -jar target/converter-0.0.1-SNAPSHOT.jar input.json output.dot --render --format png

# Generate sample files for testing
java -jar target/converter-0.0.1-SNAPSHOT.jar --create-sample demo

# Verbose output for debugging
java -jar target/converter-0.0.1-SNAPSHOT.jar input.json output.dot --verbose
```

### Visualization Generation
```bash
# From schema-file directory
./create-png.sh  # Creates PNG from existing DOT files

# Manual Graphviz rendering
dot -Tpng model.dot -o model.png
dot -Tsvg model.dot -o model.svg
```

## Key Dependencies

- **Spring Boot**: 3.5.4 (non-web application)
- **Jackson**: JSON and YAML processing (2.16.1)
- **Apache Velocity**: Template engine (2.3)
- **PicoCLI**: Command-line interface (4.7.5)
- **Lombok**: Code generation
- **SnakeYAML**: YAML configuration support

## JSON Schema Structure

The application expects JSON files with this structure:
- `metadata`: Title, version, description, creation info
- `diagram_settings`: Layout direction (TB/LR/RL/BT), node defaults
- `entities`: Array of entities with fields, constraints, special sections
- `relationships`: Array defining connections between entities
- `layout_hints`: Positioning hints like `same_rank_groups`

Entity fields support:
- Standard types: UUID, string, int, timestamp, ENUM
- Properties: `is_required`, `is_key`, descriptions
- Special sections with custom styling (`bold_red`, etc.)

## Configuration System

Configuration is managed through:
1. **Default**: `application.yaml` with comprehensive styling for different entity categories (party, product, location, contact, rate entities)
2. **Custom**: YAML files passed via `--config` option
3. **Merge Strategy**: Custom configs override defaults (TODO: proper merging implementation in DiagramService:74)

The default configuration includes extensive color schemes organized by business domains (party=green, product=gray, location=blue, etc.).

## Development Patterns

### Adding New Features
1. **New Entity Types**: Extend `EntityModel` class and update Velocity templates
2. **Custom Styling**: Add entries to `application.yaml` under `diagram.styles.entities`
3. **Template Customization**: Modify `.vm` files in `resources/templates/`
4. **CLI Options**: Add to `JsonToDotCommand` using PicoCLI annotations

### Error Handling
The application provides comprehensive error handling with:
- File validation (`FileUtils` class)
- JSON parsing error reporting
- Template processing fallbacks (simple DOT generation when Velocity fails)
- Graphviz integration error handling

### Testing Strategy
- Unit tests in `src/test/java/`
- Sample data models in `schema-file/` directories (contact, mdm, party, product, rate)
- Built-in sample file generation via `--create-sample` command

## Sample Data

The repository includes comprehensive sample models in `schema-file/`:
- **MDM**: Master data management entities
- **Party**: Business party and certification models
- **Product**: Product catalog and specification models  
- **Contact**: Contact management models
- **Rate**: Rate management and pricing models

Each sample includes JSON schema, generated DOT file, and PNG visualization.

## Important Notes

- The application is configured as a non-web Spring Boot application (`web-application-type: none`)
- Template resolution may need debugging if custom templates fail to load
- Graphviz (`dot` command) must be installed separately for auto-rendering
- Path validation in `FileUtils` prevents directory traversal attacks
- Fallback DOT generation ensures output even when templates fail