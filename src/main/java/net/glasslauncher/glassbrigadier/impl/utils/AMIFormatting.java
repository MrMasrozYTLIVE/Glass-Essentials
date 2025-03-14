package net.glasslauncher.glassbrigadier.impl.utils;

/**
 * These are safe to use on vanilla clients, they'll just remove these extra codes when parsing the text, which is pretty convenient.
 */
public enum AMIFormatting {
    OBFUSCATED('k'),
    BOLD('l'),
    STRIKETHROUGH('m'),
    UNDERLINE('n'),
    ITALICS('o'),
    RESET('r'),
    ;

    private final String stringValue;

    AMIFormatting(char code) {
        this.stringValue = "§" + code;
    }

    public String toString() {
        return this.stringValue;
    }
}
