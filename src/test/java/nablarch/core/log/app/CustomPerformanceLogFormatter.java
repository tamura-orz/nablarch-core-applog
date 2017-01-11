package nablarch.core.log.app;

public class CustomPerformanceLogFormatter extends PerformanceLogFormatter {
    
    public String end(String point, String result) {
        return "[CustomPerformanceLogFormatter]" + super.end(point, result);
    }

}
