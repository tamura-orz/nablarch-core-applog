package nablarch.core.log.basic;

import nablarch.core.log.LogTestSupport;
import nablarch.core.log.LogTestUtil;
import nablarch.core.log.MockLogSettings;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class StandardOutputLogWriterTest extends LogTestSupport {
    
    private static final String FQCN = StandardOutputLogWriterTest.class.getName();
    
    /**
     * 初期化せずに利用できること。
     */
    @Test
    public void testNotInitialized() throws Exception {
        
        File debugFile = LogTestUtil.cleanupLog("/debug.log");
        System.setOut(new PrintStream(new FileOutputStream("./log/debug.log", true)));
        
        Map<String, String> settings = new HashMap<String, String>();
        StandardOutputLogWriter writer = new StandardOutputLogWriter();
        writer.initialize(new ObjectSettings(new MockLogSettings(settings), "dummy"));
        
        try {
            for (int i = 0; i < 515; i++) {
                writer.write(new LogContext(FQCN, LogLevel.DEBUG, "[[[" + i + "]]]", null));
            }
        } finally {
            writer.terminate();
        }
        
        String debugLog = LogTestUtil.getLog(debugFile);
        for (int i = 0; i < 515; i++) {
            assertTrue(debugLog.indexOf("[[[" + i + "]]]") != -1);
        }
        
        assertTrue(debugLog.indexOf("[[[515]]]") == -1);
    }
}
