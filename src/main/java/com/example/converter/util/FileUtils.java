package com.example.converter.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for safe file operations
 */
public class FileUtils {

    /**
     * Safely create parent directories for a file path
     * Handles cases where the path has no parent directory
     */
    public static boolean createParentDirectories(String filePath) {
        return createParentDirectories(Paths.get(filePath));
    }

    /**
     * Safely create parent directories for a file path
     * Handles cases where the path has no parent directory
     */
    public static boolean createParentDirectories(Path filePath) {
        try {
            Path parentDir = filePath.getParent();

            // If no parent directory (file is in current directory), nothing to create
            if (parentDir == null) {
                return true;
            }

            // If parent directory is empty string or ".", nothing to create
            String parentStr = parentDir.toString();
            if (parentStr.isEmpty() || ".".equals(parentStr)) {
                return true;
            }

            // Create the directory structure
            Files.createDirectories(parentDir);
            return true;

        } catch (Exception e) {
            // Log the error but don't fail - maybe directory already exists
            System.err.println("Warning: Could not create parent directories for " + filePath + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if a file exists and is readable
     */
    public static boolean isValidInputFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }

        try {
            Path path = Paths.get(filePath);
            return Files.exists(path) && Files.isReadable(path) && Files.isRegularFile(path);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if a file path is valid for writing
     */
    public static boolean isValidOutputPath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }

        try {
            Path path = Paths.get(filePath);
            Path parentDir = path.getParent();

            // If no parent directory, check if current directory is writable
            if (parentDir == null) {
                return Files.isWritable(Paths.get("."));
            }

            // If parent directory exists, check if it's writable
            if (Files.exists(parentDir)) {
                return Files.isWritable(parentDir);
            }

            // If parent directory doesn't exist, try to create it
            return createParentDirectories(path);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get a safe absolute path string
     */
    public static String getAbsolutePath(String filePath) {
        try {
            return Paths.get(filePath).toAbsolutePath().toString();
        } catch (Exception e) {
            return filePath; // Return original if conversion fails
        }
    }

    /**
     * Get file extension
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(lastDotIndex + 1);
    }

    /**
     * Change file extension
     */
    public static String changeFileExtension(String fileName, String newExtension) {
        if (fileName == null) {
            return null;
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        String baseName = lastDotIndex == -1 ? fileName : fileName.substring(0, lastDotIndex);

        if (newExtension == null || newExtension.isEmpty()) {
            return baseName;
        }

        // Add dot if not present in extension
        if (!newExtension.startsWith(".")) {
            newExtension = "." + newExtension;
        }

        return baseName + newExtension;
    }

    /**
     * Validate file path for common issues
     */
    public static String validateFilePath(String filePath) {
        if (filePath == null) {
            return "File path cannot be null";
        }

        if (filePath.trim().isEmpty()) {
            return "File path cannot be empty";
        }

        // Check for invalid characters (basic check)
        String invalidChars = "<>:\"|?*";
        for (char c : invalidChars.toCharArray()) {
            if (filePath.contains(String.valueOf(c))) {
                return "File path contains invalid character: " + c;
            }
        }

        // Check path length (Windows has 260 character limit)
        if (filePath.length() > 250) {
            return "File path is too long (max 250 characters)";
        }

        return null; // No validation errors
    }
}