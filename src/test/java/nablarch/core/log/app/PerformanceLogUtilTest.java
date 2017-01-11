package nablarch.core.log.app;

import nablarch.core.ThreadContext;
import nablarch.core.log.LogTestSupport;
import nablarch.core.log.LogUtil;
import nablarch.core.log.Logger;
import org.junit.Test;

import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class PerformanceLogUtilTest extends LogTestSupport {
    
    /**
     * 設定不備の場合に例外がスローされること。
     */
    @Test
    public void testInitialize() {
        
        System.setProperty("nablarch.appLog.filePath", "classpath:nablarch/core/log/app/app-log-invalid.properties");
        
        try { 
            PerformanceLogUtil.initialize();
            fail("must throw exception.");
        } catch (Throwable e) {
            assertThat(e.getCause().getMessage(), is("invalid performanceLogFormatter.className"));
        }
    }
    
    /**
     * デフォルト設定で正しく出力されること。
     */
    @Test
    public void testDefault() {
        
        OnMemoryLogWriter.clear();
        
        System.setProperty("nablarch.appLog.filePath", "classpath:nablarch/core/log/app/app-log-settings.properties");
        System.setProperty("performanceLogFormatter.format", "[$point$:$result$] $executionTime$ms");
        
        String point = "target_point";
        
        for (int i = 0; i < 50; i++) {
            String pointSuffix = i % 5 + "";
            ThreadContext.setExecutionId(LogUtil.generateExecutionId());
            PerformanceLogUtil.start(point + pointSuffix);
            PerformanceLogUtil.end(point + pointSuffix, "success" + i, new Object[] {"target_point_option" + i});
        }
        
        List<String> messages = OnMemoryLogWriter.getMessages("writer.appLog");
        assertThat(messages.size(), is(30));
        int messageIndex = 0;
        for (int i = 0; i < 50; i++) {
            int mod = i % 5;
            if (mod == 2 || mod == 3) {
                continue;
            }
            String pointSuffix = mod + "";
            int j = 0;
            String[] splitMsg = messages.get(messageIndex++).split(Logger.LS);
            assertTrue(Pattern.matches("DEBUG PER \\[target_point" + pointSuffix + ":success" + i + "\\] [0-9]+ms", splitMsg[j++]));
            assertThat(splitMsg[j++], is("Object Information[0]: Class Name = [java.lang.String]"));
            assertThat(splitMsg[j++], is("\ttoString() = [target_point_option" + i + "]"));
        }
    }

    /**
     * カスタムのフォーマッタで正しく出力できること。
     */
    @Test
    public void testCustomPerformanceLogFormatter() {
        
        OnMemoryLogWriter.clear();
        
        System.setProperty("nablarch.appLog.filePath", "classpath:nablarch/core/log/app/app-log-custom.properties");
        System.setProperty("performanceLogFormatter.format", "[$point$:$result$] $executionTime$ms");
        System.setProperty("performanceLogFormatter.targetPoints", "target_point0,target_point1,target_point4");
        
        String point = "target_point";
        
        for (int i = 0; i < 50; i++) {
            String pointSuffix = i % 5 + "";
            ThreadContext.setExecutionId(LogUtil.generateExecutionId());
            PerformanceLogUtil.start(point + pointSuffix);
            PerformanceLogUtil.end(point + pointSuffix, "success" + i, new Object[] {"target_point_option" + i});
        }
        
        List<String> messages = OnMemoryLogWriter.getMessages("writer.appLog");
        assertThat(messages.size(), is(30));
        int messageIndex = 0;
        for (int i = 0; i < 50; i++) {
            int mod = i % 5;
            if (mod == 2 || mod == 3) {
                continue;
            }
            String pointSuffix = mod + "";
            int j = 0;
            String[] splitMsg = messages.get(messageIndex++).split(Logger.LS);
            assertTrue(Pattern.matches("DEBUG PER \\[CustomPerformanceLogFormatter\\]\\[target_point" + pointSuffix + ":success" + i + "\\] [0-9]+ms", splitMsg[j++]));
            assertThat(splitMsg[j++], is("Object Information[0]: Class Name = [java.lang.String]"));
            assertThat(splitMsg[j++], is("\ttoString() = [target_point_option" + i + "]"));
        }
    }
}
