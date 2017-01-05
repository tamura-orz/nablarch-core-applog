package nablarch.core.log.basic;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerTestSupport;
import nablarch.core.log.MockLogSettings;
import org.junit.After;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link BasicLogger}のテスト。
 * @author Kiyohito Itoh
 */
public class BasicLoggerTest extends LoggerTestSupport {

    private static final String FQCN = BasicLoggerTest.class.getName();
    
    protected String getLogFilePath(LogLevel level) {
        return "/app-basiclogger.log";
    }
    
    protected Logger createLogger(LogLevel level) {
        
        Map<String, String> settings = new HashMap<String, String>();
        settings.put("appFile.filePath", "./log/app-basiclogger.log");
        settings.put("appFile.encoding", "UTF-8");
        
        writer = new FileLogWriter();
        writer.initialize(new ObjectSettings(new MockLogSettings(settings), "appFile"));
        
        return new BasicLogger(FQCN, level, new LogWriter[] {writer});
    }
    
    private FileLogWriter writer;
    
    @After
    public void tearDown() {
        writer.terminate();
    }
}
