package ch.andre.financemanager.model;

public class CsvImportError {

    public final int lineNumber;
    public final String lineContent;
    public final String reason;

    public CsvImportError(
            int lineNumber,
            String lineContent,
            String reason
    ) {
        this.lineNumber = lineNumber;
        this.lineContent = lineContent;
        this.reason = reason;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getLineContent() {
        return lineContent;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "Zeile %d: %s | Inhalt: %s"
                .formatted(lineNumber, reason, lineContent);
    }
}
