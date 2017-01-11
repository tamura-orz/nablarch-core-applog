package nablarch.core.log;

import nablarch.core.log.basic.LogLevel;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * {@link Logger}テストのサポートクラス。
 * @author Kiyohito Itoh
 */
public abstract class LoggerTestSupport {

    /**
     * FATALレベルを基準に動作すること。
     */
    @Test
    public void testFatalLevel() {
        File logFile = cleanupLog(getLogFilePath(LogLevel.FATAL));
        Logger logger = createLogger(LogLevel.FATAL);
        LogLevel[] enabled = new LogLevel[] {LogLevel.FATAL};
        LogLevel[] disabled = new LogLevel[] {LogLevel.TRACE, LogLevel.INFO, LogLevel.DEBUG, LogLevel.WARN, LogLevel.ERROR};
        assertLogger(logFile, logger, enabled, disabled);
    }
    
    protected File cleanupLog(String filePath) {
        return LogTestUtil.cleanupLog(filePath);
    }
    
    /**
     * ERRORレベルを基準に動作すること。
     */
    @Test
    public void testErrorLevel() {
        File logFile = cleanupLog(getLogFilePath(LogLevel.ERROR));
        Logger logger = createLogger(LogLevel.ERROR);
        LogLevel[] enabled = new LogLevel[] {LogLevel.FATAL, LogLevel.ERROR};
        LogLevel[] disabled = new LogLevel[] {LogLevel.TRACE, LogLevel.INFO, LogLevel.DEBUG, LogLevel.WARN};
        assertLogger(logFile, logger, enabled, disabled);
    }
    
    /**
     * WARNレベルを基準に動作すること。
     */
    @Test
    public void testWarnLevel() {
        File logFile = cleanupLog(getLogFilePath(LogLevel.WARN));
        Logger logger = createLogger(LogLevel.WARN);
        LogLevel[] enabled = new LogLevel[] {LogLevel.FATAL, LogLevel.ERROR, LogLevel.WARN};
        LogLevel[] disabled = new LogLevel[] {LogLevel.TRACE, LogLevel.INFO, LogLevel.DEBUG};
        assertLogger(logFile, logger, enabled, disabled);
    }
    
    /**
     * INFOレベルを基準に動作すること。
     */
    @Test
    public void testInfoLevel() {
        File logFile = cleanupLog(getLogFilePath(LogLevel.INFO));
        Logger logger = createLogger(LogLevel.INFO);
        LogLevel[] enabled = new LogLevel[] {LogLevel.FATAL, LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO};
        LogLevel[] disabled = new LogLevel[] {LogLevel.TRACE, LogLevel.DEBUG};
        assertLogger(logFile, logger, enabled, disabled);
    }
    
    /**
     * DEBUGレベルを基準に動作すること。
     */
    @Test
    public void testDebugLevel() {
        File logFile = cleanupLog(getLogFilePath(LogLevel.DEBUG));
        Logger logger = createLogger(LogLevel.DEBUG);
        LogLevel[] enabled = new LogLevel[] {LogLevel.FATAL, LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO, LogLevel.DEBUG};
        LogLevel[] disabled = new LogLevel[] {LogLevel.TRACE};
        assertLogger(logFile, logger, enabled, disabled);
    }

    /**
     * TRACEレベルを基準に動作すること。
     */
    @Test
    public void testTraceLevel() {
        File logFile = cleanupLog(getLogFilePath(LogLevel.TRACE));
        Logger logger = createLogger(LogLevel.TRACE);
        LogLevel[] enabled = LogLevel.values();
        LogLevel[] disabled = new LogLevel[0];
        assertLogger(logFile, logger, enabled, disabled);
    }
    
    protected void assertLogger(File logFile, Logger logger, LogLevel[] enabled, LogLevel[] disabled) {
        assertLevelEnabled(logger, enabled, disabled);
        LogTestUtil.logForAllLevels(logger);
        assertLog(logFile, enabled, disabled);
    }

    protected abstract String getLogFilePath(LogLevel level);
    protected abstract Logger createLogger(LogLevel level);
    
    protected void assertLog(File logFile, LogLevel[] enabled, LogLevel[] disabled) {
        LogTestUtil.assertLog(logFile, enabled, disabled);
    }
    
    protected void assertLevelEnabled(Logger logger, LogLevel[] enabled, LogLevel[] disabled) {
        for (LogLevel level : enabled) {
            switch (level) {
                case FATAL:
                    assertTrue(logger.isFatalEnabled());
                    break;
                case ERROR:
                    assertTrue(logger.isErrorEnabled());
                    break;
                case WARN:
                    assertTrue(logger.isWarnEnabled());
                    break;
                case INFO:
                    assertTrue(logger.isInfoEnabled());
                    break;
                case DEBUG:
                    assertTrue(logger.isDebugEnabled());
                    break;
                case TRACE:
                    assertTrue(logger.isTraceEnabled());
            }
        }
        for (LogLevel level : disabled) {
            switch (level) {
                case FATAL:
                    assertFalse(logger.isFatalEnabled());
                    break;
                case ERROR:
                    assertFalse(logger.isErrorEnabled());
                    break;
                case WARN:
                    assertFalse(logger.isWarnEnabled());
                    break;
                case INFO:
                    assertFalse(logger.isInfoEnabled());
                    break;
                case DEBUG:
                    assertFalse(logger.isDebugEnabled());
                    break;
                case TRACE:
                    assertFalse(logger.isTraceEnabled());
            }
        }
    }
}
