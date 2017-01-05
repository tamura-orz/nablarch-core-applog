package nablarch.core.log;

import java.util.Map;

public class MockLogSettings extends LogSettings {

    private Map<String, String> settings;
    
    public MockLogSettings(String filePath) {
        super(filePath);
    }
    
    public MockLogSettings(Map<String, String> settings) {
        super(null);
        this.settings = settings;
    }

    public Map<String, String> getProps() {
        return settings != null ? settings : super.getProps();
    }

    protected Map<String, String> loadSettings(String filePath) {
        return filePath != null ? super.loadSettings(filePath) : null;
    }
}
