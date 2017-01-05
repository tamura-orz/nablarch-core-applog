package nablarch.core.log;

import nablarch.core.log.basic.LogWriterSupport;

import java.util.ArrayList;
import java.util.List;

/** 元例外をアサート出来るようにするためのLogWriter */
public class DbConnectionManagementHandlerLog extends LogWriterSupport {

    private static List<String> log = new ArrayList<String>();

    @Override
    protected void onWrite(String formattedMessage) {
        log.add(formattedMessage);
    }

    private static void clear() {
        log.clear();
    }

    private static List<String> getLog() {
        return log;
    }
}