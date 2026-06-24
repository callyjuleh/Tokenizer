package src.model;

import java.util.*;
import java.util.regex.*;

public class Lexer {

    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "int", "float", "double", "char", "boolean", "void", "String",
            "if", "else", "while", "for", "do", "switch", "case", "break",
            "continue", "return", "class", "public", "private", "protected",
            "static", "final", "new", "null", "true", "false", "import",
            "package", "extends", "implements", "interface", "abstract",
            "try", "catch", "finally", "throw", "throws", "this", "super",
            "byte", "short", "long", "instanceof"));

    private static final Map<String, Pattern> TOKEN_PATTERNS = new LinkedHashMap<>();

    static {
        TOKEN_PATTERNS.put("COMMENT", Pattern.compile("//[^\\n]*|/\\*[\\s\\S]*?\\*/"));
        TOKEN_PATTERNS.put("STRING_LITERAL", Pattern.compile("\"([^\"\\\\]|\\\\.)*\""));
        TOKEN_PATTERNS.put("CHAR_LITERAL", Pattern.compile("'([^'\\\\]|\\\\.)*'"));
        TOKEN_PATTERNS.put("REAL_LITERAL", Pattern.compile("\\b\\d+\\.\\d+\\b"));
        TOKEN_PATTERNS.put("INTEGER_LITERAL", Pattern.compile("\\b\\d+\\b"));
        TOKEN_PATTERNS.put("IDENTIFIER", Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b"));
        TOKEN_PATTERNS.put("RELATIONAL_OP", Pattern.compile("==|!=|<=|>=|<|>"));
        TOKEN_PATTERNS.put("LOGICAL_OP", Pattern.compile("&&|\\|\\||!"));
        TOKEN_PATTERNS.put("ASSIGNMENT_OP", Pattern.compile("=|\\+=|-=|\\*=|/=|%="));
        TOKEN_PATTERNS.put("ARITHMETIC_OP", Pattern.compile("[+\\-*/%]"));
        TOKEN_PATTERNS.put("BITWISE_OP", Pattern.compile("&|\\||\\^|~|<<|>>"));
        TOKEN_PATTERNS.put("DELIMITER", Pattern.compile("[;,.]"));
        TOKEN_PATTERNS.put("OPEN_PAREN", Pattern.compile("\\("));
        TOKEN_PATTERNS.put("CLOSE_PAREN", Pattern.compile("\\)"));
        TOKEN_PATTERNS.put("OPEN_BRACE", Pattern.compile("\\{"));
        TOKEN_PATTERNS.put("CLOSE_BRACE", Pattern.compile("\\}"));
        TOKEN_PATTERNS.put("OPEN_BRACKET", Pattern.compile("\\["));
        TOKEN_PATTERNS.put("CLOSE_BRACKET", Pattern.compile("\\]"));
        TOKEN_PATTERNS.put("WHITESPACE", Pattern.compile("[ \\t\\r\\n]+"));
    }

    private final TokenDatabase tokenDB;

    public Lexer(TokenDatabase tokenDB) {
        this.tokenDB = tokenDB;
    }

    public List<Token> tokenize(String source) {
        List<Token> tokens = new ArrayList<>();
        int pos = 0;

        while (pos < source.length()) {
            boolean matched = false;

            for (var entry : TOKEN_PATTERNS.entrySet()) {
                String type = entry.getKey();
                Pattern p = entry.getValue();
                Matcher m = p.matcher(source.substring(pos));

                if (m.lookingAt()) {
                    String lex = m.group();
                    if (!type.equals("WHITESPACE")) {
                        String resolved = resolveTokenType(lex, type);
                        // Valid
                        String finalType = tokenDB.isValid(resolved) ? resolved : "INVALID";
                        tokens.add(new Token(lex, finalType));
                    }
                    pos += lex.length();
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                tokens.add(new Token(String.valueOf(source.charAt(pos)), "INVALID"));
                pos++;
            }
        }

        return tokens;
    }

    private String resolveTokenType(String lexeme, String rawType) {
        if (rawType.equals("IDENTIFIER")) {
            if (KEYWORDS.contains(lexeme.toLowerCase()) || KEYWORDS.contains(lexeme)) {
                return "KEYWORD";
            }
            return "IDENTIFIER";
        }
        if (rawType.equals("INTEGER_LITERAL") || rawType.equals("REAL_LITERAL")
                || rawType.equals("STRING_LITERAL") || rawType.equals("CHAR_LITERAL")) {
            return "LITERAL";
        }
        if (rawType.equals("ARITHMETIC_OP"))
            return "ARITHMETIC OPERATOR";
        if (rawType.equals("RELATIONAL_OP"))
            return "RELATIONAL OPERATOR";
        if (rawType.equals("LOGICAL_OP"))
            return "LOGICAL OPERATOR";
        if (rawType.equals("ASSIGNMENT_OP"))
            return "ASSIGNMENT OPERATOR";
        if (rawType.equals("BITWISE_OP"))
            return "BITWISE OPERATOR";
        if (rawType.equals("OPEN_PAREN") || rawType.equals("CLOSE_PAREN"))
            return "PARENTHESIS";
        if (rawType.equals("OPEN_BRACE") || rawType.equals("CLOSE_BRACE"))
            return "BRACE";
        if (rawType.equals("OPEN_BRACKET") || rawType.equals("CLOSE_BRACKET"))
            return "BRACKET";
        if (rawType.equals("DELIMITER"))
            return "DELIMITER";
        if (rawType.equals("COMMENT"))
            return "COMMENT";
        return rawType;
    }
}