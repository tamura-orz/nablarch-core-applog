package nablarch.core.log.app;

import nablarch.core.log.LogItem;

public class CustomFailureLogFormatter extends FailureLogFormatter {
    @Override
    protected String format(LogItem<FailureLogContext>[] formattedLogItems,
                             Throwable error, Object data, String failureCode,
                             Object[] messageOptions) {
        return "[CustomFailureLogFormatter]"
            + super.format(formattedLogItems, error, data, failureCode, messageOptions);
    }
}
