package nablarch.core.log.basic;

import nablarch.core.log.LogTestUtil;
import nablarch.core.log.MockLogSettings;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * {@link LogWriterSupport}のテスト。<br>
 * <br>
 * {@link FileLogWriter}を使用してテストする。
 * 
 * @author Kiyohito Itoh
 */
public class LogWriterSupportTest {
    
    private static final String FQCN = LogWriterSupportTest.class.getName();
    
    /**
     * ログフォーマッタを設定できること。
     */
    @Test
    public void testLogFormatterSetting() {
        
        File appFile = LogTestUtil.cleanupLog("/log-formatter-setting-app.log");

        Map<String, String> settings = new HashMap<String, String>();
        settings.put("appFile.filePath", "./log/log-formatter-setting-app.log");
        settings.put("appFile.encoding", "UTF-8");
        settings.put("appFile.outputbuffersize", "8");
        settings.put("appFile.formatter.className", MockLogFormatter.class.getName());

        FileLogWriter writer = new FileLogWriter();
        try {
            writer.initialize(new ObjectSettings(new MockLogSettings(settings), "appFile"));
            for (int i = 0; i < 515; i++) {
                writer.write(new LogContext(FQCN, LogLevel.DEBUG, "[[[" + i + "]]]", null));
            }
        } finally {
            writer.terminate();
        }
        
        String appLog = LogTestUtil.getLog(appFile);
        assertTrue(appLog.indexOf("initialized.") != -1);
        assertTrue(appLog.indexOf("terminated.") != -1);
        for (int i = 0; i < 515; i++) {
            assertTrue(appLog.indexOf(MockLogFormatter.class.getSimpleName() + " DEBUG [[[" + i + "]]]") != -1);
        }
        assertTrue(appLog.indexOf("[[[515]]]") == -1);
    }
    
    /**
     * ログフォーマッタに設定されたログレベルで出力制御できること。
     */
    @Test
    public void testLevelSetting() {
        
        File appFile = LogTestUtil.cleanupLog("/level-setting-app.log");

        Map<String, String> settings = new HashMap<String, String>();
        settings.put("appFile.filePath", "./log/level-setting-app.log");
        settings.put("appFile.outputbuffersize", "8");
        settings.put("appFile.level", "ERROR");

        FileLogWriter writer = new FileLogWriter();
        try {
            writer.initialize(new ObjectSettings(new MockLogSettings(settings), "appFile"));
            for (LogLevel level : LogLevel.values()) {
                writer.write(new LogContext(FQCN, level, "[[[" + level.name() + "]]]", null));
            }
        } finally {
            writer.terminate();
        }

        String appLog = LogTestUtil.getLog(appFile);
        assertTrue("INFOレベルではないので初期化メッセージが出ない", appLog.indexOf("initialized.") == -1);
        assertTrue("INFOレベルではないので完了メッセージが出ない", appLog.indexOf("terminated.") == -1);
        assertTrue(appLog.indexOf("[[[FATAL]]]") != -1);
        assertTrue(appLog.indexOf("[[[ERROR]]]") != -1);
        assertTrue(appLog.indexOf("[[[WARN]]]") == -1);
        assertTrue(appLog.indexOf("[[[INFO]]]") == -1);
        assertTrue(appLog.indexOf("[[[DEBUG]]]") == -1);
        assertTrue(appLog.indexOf("[[[TRACE]]]") == -1);
    }

    /**
     * ログフォーマッタが指定されなくてもデフォルトのフォーマッタで動くこと。
     */
    @Test
    public void testNullFormatterClassName() {
        
        File appFile = LogTestUtil.cleanupLog("/null-formatter-classname-app.log");

        Map<String, String> settings = new HashMap<String, String>();
        settings.put("appFile.filePath", "./log/null-formatter-classname-app.log");
        settings.put("appFile.outputbuffersize", "8");
        settings.put("appFile.level", "ERROR");

        FileLogWriter writer = new FileLogWriter();
        try {
            writer.initialize(new ObjectSettings(new MockLogSettings(settings), "appFile"));
            for (LogLevel level : LogLevel.values()) {
                writer.write(new LogContext(FQCN, level, "[[[" + level.name() + "]]]", null));
            }
        } finally {
            writer.terminate();
        }

        String appLog = LogTestUtil.getLog(appFile);
        assertTrue("INFOレベルではないので初期化メッセージが出ない", appLog.indexOf("initialized.") == -1);
        assertTrue("INFOレベルではないので完了メッセージが出ない", appLog.indexOf("terminated.") == -1);
        assertTrue(appLog.indexOf("[[[FATAL]]]") != -1);
        assertTrue(appLog.indexOf("[[[ERROR]]]") != -1);
        assertTrue(appLog.indexOf("[[[WARN]]]") == -1);
        assertTrue(appLog.indexOf("[[[INFO]]]") == -1);
        assertTrue(appLog.indexOf("[[[DEBUG]]]") == -1);
        assertTrue(appLog.indexOf("[[[TRACE]]]") == -1);
    }

    /**
     * ログフォーマッタがブランクが指定されてもデフォルトのフォーマッタで動くこと。
     */
    @Test
    public void testBlankFormatterClassName() {
        
        File appFile = LogTestUtil.cleanupLog("/blank-formatter-classname-app.log");

        Map<String, String> settings = new HashMap<String, String>();
        settings.put("appFile.filePath", "./log/blank-formatter-classname-app.log");
        settings.put("appFile.outputbuffersize", "8");
        settings.put("appFile.level", "ERROR");
        settings.put("appFile.formatter.className", "");

        FileLogWriter writer = new FileLogWriter();
        try {
            writer.initialize(new ObjectSettings(new MockLogSettings(settings), "appFile"));
            for (LogLevel level : LogLevel.values()) {
                writer.write(new LogContext(FQCN, level, "[[[" + level.name() + "]]]", null));
            }
        } finally {
            writer.terminate();
        }

        String appLog = LogTestUtil.getLog(appFile);
        assertTrue("INFOレベルではないので初期化メッセージが出ない", appLog.indexOf("initialized.") == -1);
        assertTrue("INFOレベルではないので完了メッセージが出ない", appLog.indexOf("terminated.") == -1);
        assertTrue(appLog.indexOf("[[[FATAL]]]") != -1);
        assertTrue(appLog.indexOf("[[[ERROR]]]") != -1);
        assertTrue(appLog.indexOf("[[[WARN]]]") == -1);
        assertTrue(appLog.indexOf("[[[INFO]]]") == -1);
        assertTrue(appLog.indexOf("[[[DEBUG]]]") == -1);
        assertTrue(appLog.indexOf("[[[TRACE]]]") == -1);
    }
}
