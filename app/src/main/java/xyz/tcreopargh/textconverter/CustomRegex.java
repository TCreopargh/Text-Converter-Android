package xyz.tcreopargh.textconverter;

import androidx.annotation.NonNull;

public class CustomRegex {

    public static final String LABEL_KEY = "regex_label_";
    public static final String REGEX_KEY = "regex_value_";
    public static final String SIZE_KEY = "regex_size";

    private String label;
    private String regex;

    public CustomRegex(String label, String regex) {
        this.label = label;
        this.regex = regex;
    }

    @NonNull
    @Override
    public String toString() {
        return label + ":" + regex;
    }

    public String getLabel() {
        return label;
    }

    public String getRegex() {
        return regex;
    }
}
