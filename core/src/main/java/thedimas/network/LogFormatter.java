package thedimas.network;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


public class LogFormatter extends Formatter {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
    private String getStackTrace(Throwable e){
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString().trim();
    }


    @Override
    public String format(LogRecord record) {
        StringBuilder formattedLog = new StringBuilder();
        formattedLog
                .append(AnsiCodes.BOLD)
                .append(AnsiCodes.DIM)
                .append("[")
                .append(dateFormat.format(new Date(record.getMillis())))
                .append("] ")
                .append(AnsiCodes.RESET);

        String logLevelColor;
        if (record.getLevel().intValue() >= Level.SEVERE.intValue()) {
            logLevelColor = AnsiCodes.BRIGHT_RED;
        } else if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
            logLevelColor = AnsiCodes.BRIGHT_YELLOW;
        } else if (record.getLevel().intValue() >= Level.INFO.intValue()) {
            logLevelColor = AnsiCodes.BRIGHT_BLUE;
        } else if (record.getLevel().intValue() >= Level.CONFIG.intValue()) {
            logLevelColor = AnsiCodes.BRIGHT_GREEN;
        } else {
            logLevelColor = AnsiCodes.DIM;
        }

        formattedLog.append(logLevelColor)
                .append(AnsiCodes.BOLD)
                .append("[")
                .append(record.getLevel().getName().charAt(0))
                .append("]")
                .append(AnsiCodes.RESET)
                .append(" ");

        String[] split = record.getSourceClassName().split("\\.");
        formattedLog.append("[")
                .append(AnsiCodes.ITALIC)
                .append(split[split.length - 1])
                .append(AnsiCodes.RESET)
                .append("] ");

        formattedLog.append(record.getMessage());

        if (record.getThrown() != null) {
            formattedLog.append(": ")
                    .append(getStackTrace(record.getThrown()));
        }

        formattedLog.append("\n");

        return formattedLog.toString();
    }
}
