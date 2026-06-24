package src.model;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TokenDatabase {

    private static final String DB_FILE = "src/db/token.json";
    private Set<String> validTokenTypes = new HashSet<>();

    public TokenDatabase() {
        load();
    }

    public void load() {
        validTokenTypes.clear();
        try {
            if (!Files.exists(Path.of(DB_FILE))) {
                System.err.println("[TokenDatabase] tokens.json not found: " + Path.of(DB_FILE).toAbsolutePath());
                return;
            }

            String content = Files.readString(Path.of(DB_FILE));

            int arrayStart = content.indexOf('[');
            int arrayEnd = content.lastIndexOf(']');
            if (arrayStart == -1 || arrayEnd == -1)
                return;

            String array = content.substring(arrayStart + 1, arrayEnd);

            int i = 0;
            while ((i = array.indexOf('"', i)) != -1) {
                int close = array.indexOf('"', i + 1);
                if (close == -1)
                    break;
                validTokenTypes.add(array.substring(i + 1, close));
                i = close + 1;
            }

        } catch (IOException e) {
            System.err.println("[TokenDatabase] Failed to load: " + e.getMessage());
        }
    }

    public boolean isValid(String tokenType) {
        return validTokenTypes.contains(tokenType);
    }

    public Set<String> getValidTokenTypes() {
        return Collections.unmodifiableSet(validTokenTypes);
    }

    public String getFilePath() {
        return Path.of(DB_FILE).toAbsolutePath().toString();
    }
}