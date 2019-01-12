package xyz.tcreopargh.textconverter;

public class Range {
    private int start;
    private int end;

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int start() {
        return this.start;
    }

    public int end() {
        return this.end;
    }

    public int getLength() {
        return end - start + 1;
    }

    public boolean checkLegality() {
        return end >= start;
    }
}
