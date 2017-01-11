package nablarch.core.log.basic;

public class ExceptionLogWriter implements LogWriter {

    public void initialize(ObjectSettings settings) {
    }

    public void terminate() {
        throw new IllegalArgumentException(ExceptionLogWriter.class.getName());
    }

    public void write(LogContext context) {
        throw new IllegalArgumentException(ExceptionLogWriter.class.getName());
    }
}
