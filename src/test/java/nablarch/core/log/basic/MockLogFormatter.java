package nablarch.core.log.basic;

import nablarch.core.log.Logger;

public class MockLogFormatter implements LogFormatter {

    public void initialize(ObjectSettings settings) {
    }

    public String format(LogContext context) {
        return String.format("%s %s %s %s",
                              MockLogFormatter.class.getSimpleName(),
                              context.getLevel().name(),
                              context.getMessage(),
                              Logger.LS);
    }
}
