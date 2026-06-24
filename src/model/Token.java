package src.model;

public class Token {
    private String lexeme;
    private String tokenType;

    public Token(String lexeme, String tokenType) {
        this.lexeme = lexeme;
        this.tokenType = tokenType;
    }

    public String getLexeme() {
        return lexeme;
    }

    public String getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        return "{ \"lexeme\": \"" + lexeme + "\", \"token\": \"" + tokenType + "\" }";
    }
}