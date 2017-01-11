package nablarch.core.log;

import nablarch.core.log.basic.LogWriterSupport;

import java.util.ArrayList;
import java.util.List;

/** テスト用のログライタ。 */
public class ProcessResidentHandlerLog extends LogWriterSupport {

    /** ログ出力されたメッセージを保持するオブジェクト */
    private static List<String> logMessages = new ArrayList<String>();

    /** ログに出力されたメッセージを削除する。 */
    private static void logClear() {
        logMessages.clear();
    }

    @Override
    protected void onWrite(String formattedMessage) {
        logMessages.add(formattedMessage);
    }
}