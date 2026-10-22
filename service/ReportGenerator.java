package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class ReportGenerator {

    /** Reads every .log file in logsDir, totals deposits/withdrawals and writes a summary file. */
    public static String generate(Path logsDir, Path reportFile) throws IOException {
        long deposits = 0, withdrawals = 0;
        List<String> lines = new ArrayList<>();
        lines.add("===== End-of-day report =====");

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(logsDir)) {
            for (Path p : ds) {
                if (!p.getFileName().toString().endsWith(".log")) continue;   // skip non-.log files
                BasicFileAttributes attrs = Files.readAttributes(p, BasicFileAttributes.class);
                if (attrs.size() == 0) continue;                              // skip empty files
                lines.add("File " + p.getFileName() + " | size " + attrs.size() + " bytes | modified "
                        + attrs.lastModifiedTime());
                try (BufferedReader br = Files.newBufferedReader(p)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.trim().split("\\s+");
                        if (parts.length != 3) continue;
                        long amount = Long.parseLong(parts[2]);
                        if (parts[0].equals("DEPOSIT")) deposits += amount;
                        else if (parts[0].equals("WITHDRAW")) withdrawals += amount;
                    }
                }
            }
        }
        lines.add("Total deposits    : " + deposits);
        lines.add("Total withdrawals : " + withdrawals);
        lines.add("Net change        : " + (deposits - withdrawals));
        Files.write(reportFile, lines);
        return String.join(System.lineSeparator(), lines);
    }
}
