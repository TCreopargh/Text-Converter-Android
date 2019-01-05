package xyz.tcreopargh.textconverter;

public class ListItems {
    private String label;
    private String subtitle;

    public ListItems(String label, String subtitle) {
        this.label = label;
        this.subtitle = subtitle;
    }

    public String getLabel() {
        return label;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
