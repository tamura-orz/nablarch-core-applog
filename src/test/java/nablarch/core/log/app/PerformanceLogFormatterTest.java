package nablarch.core.log.app;

import nablarch.core.ThreadContext;
import nablarch.core.log.LogTestSupport;
import nablarch.core.log.LogTestUtil;
import nablarch.core.log.Logger;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * @author Kiyohito Itoh
 */
public class PerformanceLogFormatterTest extends LogTestSupport {
    
    /**
     * メソッド呼び出し順が正しくない場合に例外がスローされること。
     */
    @Test
    public void testInvalidInvokeEndMethod() {
        
        System.setProperty("nablarch.appLog.filePath", "classpath:nablarch/core/log/app/app-log-default.properties");
        
        ThreadContext.setExecutionId("test_exe_id");
        
        PerformanceLogFormatter formatter = new PerformanceLogFormatter();
        
        try {
            formatter.end("NOT_FOUND", null);
            fail("must throw IllegalStateException.");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is("PerformanceLogContext was not found. point = [NOT_FOUND], execution id = [test_exe_id]"));
        }
    }
    
    /**
     * デフォルトのフォーマットで正しくフォーマットされること。
     */
    @Test
    public void testDefaultFormat() throws ParseException {
        
        System.setProperty("nablarch.appLog.filePath", "classpath:nablarch/core/log/app/app-log-default.properties");
        
        PerformanceLogFormatter formatter = new PerformanceLogFormatter();
        
        String point = "point0001";
        
        formatter.start(point);
        String message = formatter.end(point, "success");
        
        String[] splitMsg = message.split(" |" + Logger.LS + "\t");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Pattern pattern = Pattern.compile("^\\[(.+)\\]");
        
        int index = 1;
        assertThat(splitMsg[index++], is("point"));
        assertThat(splitMsg[index++], is("="));
        assertThat(splitMsg[index++], is("[point0001]"));
        assertThat(splitMsg[index++], is("result"));
        assertThat(splitMsg[index++], is("="));
        assertThat(splitMsg[index++], is("[success]"));
        assertThat(splitMsg[index++], is("start_time"));
        assertThat(splitMsg[index++], is("="));
        assertNotNull(LogTestUtil.parseDate(splitMsg[index++] + " " + splitMsg[index++], dateFormat, pattern));
        assertThat(splitMsg[index++], is("end_time"));
        assertThat(splitMsg[index++], is("="));
        assertNotNull(LogTestUtil.parseDate(splitMsg[index++] + " " + splitMsg[index++], dateFormat, pattern));
        assertThat(splitMsg[index++], is("execution_time"));
        assertThat(splitMsg[index++], is("="));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(splitMsg[index++]).matches());
        assertThat(splitMsg[index++], is("max_memory"));
        assertThat(splitMsg[index++], is("="));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(splitMsg[index++]).matches());
        assertThat(splitMsg[index++], is("start_free_memory"));
        assertThat(splitMsg[index++], is("="));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(splitMsg[index++]).matches());
        assertThat(splitMsg[index++], is("start_used_memory"));
        assertThat(splitMsg[index++], is("="));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(splitMsg[index++]).matches());
        assertThat(splitMsg[index++], is("end_free_memory"));
        assertThat(splitMsg[index++], is("="));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(splitMsg[index++]).matches());
        assertThat(splitMsg[index++], is("end_used_memory"));
        assertThat(splitMsg[index++], is("="));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(splitMsg[index++]).matches());
    }

    /**
     * フォーマットの出力項目を入れ替えた場合に正しくフォーマットされること。
     */
    @Test
    public void testFormatSettingsSwap() {
        
        System.setProperty("nablarch.appLog.filePath", "classpath:nablarch/core/log/app/app-log-settings.properties");
        
        PerformanceLogFormatter formatter = new PerformanceLogFormatter();
        
        String point = "point0001";
        
        formatter.start(point);
        String message = formatter.end(point, "success");
        
        String[] splitMsg = message.split(" ");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH-mm-ss[SSS]");
        Pattern pattern = Pattern.compile("^\\[(.+)\\]");
        
        int index = 0;
        assertThat(splitMsg[index++], is("end_used_memory"));
        assertThat(splitMsg[index++], is(":"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(splitMsg[index++]).matches());
        assertThat(splitMsg[index++], is("end_free_memory"));
        assertThat(splitMsg[index++], is(":"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(splitMsg[index++]).matches());
        assertThat(splitMsg[index++], is("start_used_memory"));
        assertThat(splitMsg[index++], is(":"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(splitMsg[index++]).matches());
        assertThat(splitMsg[index++], is("start_free_memory"));
        assertThat(splitMsg[index++], is(":"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(splitMsg[index++]).matches());
        assertThat(splitMsg[index++], is("max_memory"));
        assertThat(splitMsg[index++], is(":"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(splitMsg[index++]).matches());
        assertThat(splitMsg[index++], is("execution_time"));
        assertThat(splitMsg[index++], is(":"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(splitMsg[index++]).matches());
        assertThat(splitMsg[index++], is("end_time"));
        assertThat(splitMsg[index++], is(":"));
        assertNotNull(LogTestUtil.parseDate(splitMsg[index++] + " " + splitMsg[index++], dateFormat, pattern));
        assertThat(splitMsg[index++], is("start_time"));
        assertThat(splitMsg[index++], is(":"));
        assertNotNull(LogTestUtil.parseDate(splitMsg[index++] + " " + splitMsg[index++], dateFormat, pattern));
        assertThat(splitMsg[index++], is("result"));
        assertThat(splitMsg[index++], is(":"));
        assertThat(splitMsg[index++], is("[success]"));
        assertThat(splitMsg[index++], is("point"));
        assertThat(splitMsg[index++], is(":"));
        assertThat(splitMsg[index++], is("[point0001]"));
    }

    /**
     * フォーマットの出力項目を減らした場合に正しくフォーマットされること。
     */
    @Test
    public void testFormatSettingsReduce() {
        
        System.setProperty("nablarch.appLog.filePath", "classpath:nablarch/core/log/app/app-log-settings.properties");
        
        System.setProperty("performanceLogFormatter.format", "[$point$:$result$]");
        
        PerformanceLogFormatter formatter = new PerformanceLogFormatter();
        
        String point = "point0001";
        
        formatter.start(point);
        String message = formatter.end(point, "success");
        
        String[] splitMsg = message.split(" ");
        
        int index = 0;
        assertThat(splitMsg[index++], is("[point0001:success]"));
    }

    /**
     * 内部で使用しているThreadLocalがremoveされているため、2回endを呼ぶと例外が送出されること。
     * 再度、使用を開始したときに問題なく動作すること。
     */
    @Test
    public void testThreadLocalRemoveAndReset() {
        System.setProperty("nablarch.appLog.filePath", "classpath:nablarch/core/log/app/app-log-settings.properties");
        System.setProperty("performanceLogFormatter.format", "[$point$:$result$]");

        PerformanceLogFormatter formatter = new PerformanceLogFormatter();
        String point = "point01";

        formatter.start(point);
        formatter.end(point, "result");
        try {
            formatter.end(point, "result");
            fail();
        } catch (IllegalStateException ignored) {
        }

        formatter.start(point);
        assertThat(formatter.end(point, "result"), is("[point01:result]"));
    }

    /**
     * 2つのポイントでも動作する。
     */
    @Test
    public void testTwoPoint() {
        System.setProperty("nablarch.appLog.filePath", "classpath:nablarch/core/log/app/app-log-settings.properties");
        System.setProperty("performanceLogFormatter.format", "[$point$:$result$]");

        PerformanceLogFormatter formatter = new PerformanceLogFormatter();

        formatter.start("point01");
        formatter.start("point02");

        assertThat(formatter.end("point02", "result02"), is("[point02:result02]"));
        assertThat(formatter.end("point01", "result01"), is("[point01:result01]"));
    }
}
