package nablarch.core.log.basic;

import nablarch.core.log.LogTestSupport;
import nablarch.core.log.MockLogSettings;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * {@link ObjectSettings}のテスト。
 * @author Kiyohito Itoh
 */
public class ObjectSettingsTest extends LogTestSupport {
    
    /**
     * オブジェクトの設定内容を取得できること。
     */
    @Test
    public void testGetting() {
        
        Map<String, String> settings = new HashMap<String, String>();
        settings.put("writer.appFile.className", "nablarch.core.log.basic.FileLogWriter");
        settings.put("writer.appFile.filePath", "/var/log/app/app.log");
        settings.put("writer.appFile.encoding", "UTF-8");
        settings.put("writer.appFile.maxFileSize", "10000");
        settings.put("writer.appFile.formatter.className", "nablarch.core.log.basic.BasicLogFormatter");
        settings.put("writer.appFile.formatter.label.fatal", "F");
        settings.put("writer.appFile.formatter.label.error", "E");
        settings.put("writer.appFile.formatter.label.warn", "W");
        settings.put("writer.appFile.blank", "");
        settings.put("writer.appFile.formatter.blank", "");
        
        ObjectSettings writerSettings = new ObjectSettings(new MockLogSettings(settings), "writer.appFile");
        assertThat(writerSettings.getName(), is("writer.appFile"));
        assertThat(writerSettings.getProp("className"), is("nablarch.core.log.basic.FileLogWriter"));
        assertThat(writerSettings.getProp("formatter.label.warn"), is("W"));
        
        try {
            writerSettings.getRequiredProp("unknown");
            fail("must be thrown the IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // success
        }
        
        try {
            writerSettings.getRequiredProp("blank");
            fail("must be thrown the IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // success
        }
        
        ObjectSettings formatterSettings = new ObjectSettings(new MockLogSettings(settings), "writer.appFile.formatter");
        assertThat(formatterSettings.getName(), is("writer.appFile.formatter"));
        assertThat(formatterSettings.getProp("className"), is("nablarch.core.log.basic.BasicLogFormatter"));
        assertThat(formatterSettings.getProp("label.fatal"), is("F"));
        
        try {
            formatterSettings.getRequiredProp("unknown");
            fail("must be thrown the IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // success
        }
        
        try {
            formatterSettings.getRequiredProp("blank");
            fail("must be thrown the IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // success
        }
    }
}
