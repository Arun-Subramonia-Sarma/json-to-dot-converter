package com.example.converter.cli;

import com.example.converter.service.DiagramService;
import com.example.converter.util.FileUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 * Command-line interface for JSON to DOT conversion
 */
@Component
@Command(
        name = "json-to-dot",
        description = "Convert JSON data models to DOT (Graphviz) diagrams",
        mixinStandardHelpOptions = true,
        version = "1.0.0"
)
public class JsonToDotCommand implements CommandLineRunner, Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(JsonToDotCommand.class);

    @Parameters(index = "0", description = "Input JSON file", arity = "0..1")
    private String inputFile;

    @Parameters(index = "1", description = "Output DOT file", arity = "0..1")
    private String outputFile;

    @Option(names = {"-c", "--config"}, description = "Custom configuration file (YAML)")
    private String configFile;

    @Option(names = {"--create-sample"}, description = "Create sample JSON and configuration files")
    private String samplePrefix;

    @Option(names = {"-v", "--verbose"}, description = "Enable verbose output")
    private boolean verbose;

    @Option(names = {"-f", "--format"}, description = "Output format (png, svg, pdf)", defaultValue = "png")
    private String format;

    @Option(names = {"--render"}, description = "Automatically render diagram using Graphviz")
    private boolean autoRender;

    @Autowired
    private DiagramService diagramService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) {
        int exitCode = new CommandLine(this).execute(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    @Override
    public Integer call() throws Exception {
        try {
            if (samplePrefix != null) {
                return createSampleFiles(samplePrefix);
            }

            if (inputFile == null || outputFile == null) {
                System.err.println("Error: Both input and output files must be specified");
                CommandLine.usage(this, System.err);
                return 1;
            }

            return convertJsonToDot();

        } catch (Exception e) {
            logger.error("Error during conversion", e);
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }

    private Integer convertJsonToDot() throws Exception {
        // Validate input file
        if (!FileUtils.isValidInputFile(inputFile)) {
            System.err.println("Error: Input file not found or not readable: " + inputFile);
            return 1;
        }

        // Validate output file path
        String pathValidationError = FileUtils.validateFilePath(outputFile);
        if (pathValidationError != null) {
            System.err.println("Error: Invalid output file path - " + pathValidationError);
            return 1;
        }

        if (verbose) {
            System.out.println("Reading JSON from: " + FileUtils.getAbsolutePath(inputFile));
            if (configFile != null) {
                System.out.println("Using config file: " + FileUtils.getAbsolutePath(configFile));
            }
        }

        // Validate config file if provided
        if (configFile != null && !FileUtils.isValidInputFile(configFile)) {
            System.err.println("Error: Config file not found or not readable: " + configFile);
            return 1;
        }

        // Read and parse JSON
        JsonNode jsonData = objectMapper.readTree(new File(inputFile));

        // Generate DOT content
        String dotContent = diagramService.generateDotContent(jsonData, configFile);

        // Create parent directories safely
        if (!FileUtils.createParentDirectories(outputFile)) {
            if (verbose) {
                System.out.println("Warning: Could not create parent directories for " + outputFile);
            }
        }

        // Write the output file
        try {
            Path outputPath = Paths.get(outputFile);
            Files.write(outputPath, dotContent.getBytes());
            System.out.println("Successfully converted " + inputFile + " to " + outputFile);

            if (verbose) {
                System.out.println("Output file size: " + Files.size(outputPath) + " bytes");
                System.out.println("Absolute path: " + outputPath.toAbsolutePath());
            }

            // Auto-render if requested
            if (autoRender) {
                return renderDiagram(outputPath);
            }

            // Show render command
            String imageFile = FileUtils.changeFileExtension(outputFile, format);
            System.out.println("To generate diagram, run: dot -T" + format + " " + outputFile + " -o " + imageFile);

            return 0;

        } catch (Exception e) {
            System.err.println("Error writing output file: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
            return 1;
        }
    }

    private Integer renderDiagram(Path dotFile) {
        try {
            String imageFile = dotFile.toString().replace(".dot", "." + format);

            ProcessBuilder pb = new ProcessBuilder("dot", "-T" + format, dotFile.toString(), "-o", imageFile);
            Process process = pb.start();

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Diagram rendered: " + imageFile);
                return 0;
            } else {
                System.err.println("Error: Graphviz rendering failed with exit code " + exitCode);
                System.err.println("Make sure Graphviz is installed and 'dot' command is available");
                return 1;
            }

        } catch (Exception e) {
            System.err.println("Error rendering diagram: " + e.getMessage());
            System.err.println("Make sure Graphviz is installed and 'dot' command is available");
            return 1;
        }
    }

    private Integer createSampleFiles(String prefix) throws Exception {
        // Create sample JSON
        String sampleJson = """
                {
                  "metadata": {
                    "title": "Sample Data Model",
                    "version": "1.0",
                    "description": "Sample data model for testing"
                  },
                  "diagram_settings": {
                    "rankdir": "TB"
                  },
                  "entities": [
                    {
                      "id": "user",
                      "name": "User",
                      "fields": [
                        {"name": "id", "type": "UUID", "is_required": true, "is_key": true},
                        {"name": "username", "type": "string", "is_required": true},
                        {"name": "email", "type": "string", "is_required": true},
                        {"name": "created_at", "type": "timestamp", "is_required": false}
                      ],
                      "special_sections": [
                        {"name": "Auditable fields", "type": "object", "style": "bold_red"}
                      ],
                      "description": "System user entity"
                    },
                    {
                      "id": "profile",
                      "name": "User Profile",
                      "fields": [
                        {"name": "id", "type": "UUID", "is_required": true, "is_key": true},
                        {"name": "user_id", "type": "UUID", "is_required": true},
                        {"name": "first_name", "type": "string", "is_required": false},
                        {"name": "last_name", "type": "string", "is_required": false},
                        {"name": "bio", "type": "string", "is_required": false}
                      ],
                      "constraints": [
                        "FOREIGN KEY (user_id) REFERENCES user(id)"
                      ],
                      "description": "Extended user profile information"
                    }
                  ],
                  "relationships": [
                    {"from_entity": "user", "to_entity": "profile", "label": "HAS_PROFILE", "relationship_type": "one_to_one"}
                  ]
                }
                """;

        // Create sample configuration
        String sampleConfig = """
                # Custom diagram configuration
                diagram:
                  settings:
                    rankdir: LR
                  
                  styles:
                    entities:
                      user:
                        header:
                          bgcolor: "#2563EB"
                          forecolor: white
                        body:
                          bgcolor: "#EFF6FF"
                      
                      profile:
                        header:
                          bgcolor: "#059669"
                          forecolor: white
                        body:
                          bgcolor: "#ECFDF5"
                
                logging:
                  level:
                    com.example.converter: DEBUG
                """;

        // Write files
        String jsonFile = prefix + "_sample.json";
        String configFile = prefix + "_config.yaml";

        Files.write(Paths.get(jsonFile), sampleJson.getBytes());
        Files.write(Paths.get(configFile), sampleConfig.getBytes());

        System.out.println("Sample files created:");
        System.out.println("  JSON model: " + jsonFile);
        System.out.println("  Config file: " + configFile);
        System.out.println();
        System.out.println("Usage examples:");
        System.out.println("  java -jar app.jar " + jsonFile + " output.dot");
        System.out.println("  java -jar app.jar " + jsonFile + " output.dot --config " + configFile);
        System.out.println("  java -jar app.jar " + jsonFile + " output.dot --render --format png");

        return 0;
    }
}