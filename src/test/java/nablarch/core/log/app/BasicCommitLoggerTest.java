package nablarch.core.log.app;

import nablarch.core.log.basic.LogWriterSupport;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * {@link nablarch.core.log.app.BasicCommitLogger}のテストクラス。
 *
 * @author hisaaki sioiri
 */
public class BasicCommitLoggerTest {

    /**
     * {@link BasicCommitLogger#increment(long)}のテスト。
     *
     * @throws Exception
     */
    @Test
    public void testIncrement() throws Exception {

        // 5件間隔でログ出力
        BasicCommitLogger logger = new BasicCommitLogger();
        logger.initialize();
        logger.setInterval(5);

        // ログの初期化
        LogWriter.clear();

        // 4件までインクリメント
        logger.increment(1);
        logger.increment(1);
        logger.increment(1);
        logger.increment(1);

        // 5件に達していないのでログが出力されていないことを確認する。
        List<String> logs = LogWriter.getLog();
        assertThat("サイズは、0であること", logs.size(), is(0));

        // 5件までインクリメント
        logger.increment(1);
        logs = LogWriter.getLog();
        assertThat("サイズは、1であること", logs.size(), is(1));
        assertThat("ログが想定通り出力されていること。", logs.get(0),
                containsString("COMMIT COUNT = [5]"));

        // 9件までインクリメント
        logger.increment(4);
        assertThat("サイズは、1であること", logs.size(), is(1));

        // 10件までインクリメント
        logger.increment(1);
        assertThat("サイズは、2であること", logs.size(), is(2));
        assertThat("ログが想定通り出力されていること。", logs.get(0),
                containsString("COMMIT COUNT = [5]"));
        assertThat("ログが想定通り出力されていること。", logs.get(1),
                containsString("COMMIT COUNT = [10]"));


        // ログ出力間隔より大きい値を加算
        logger.increment(100);
        assertThat("サイズは、2であること", logs.size(), is(3));
        assertThat("ログが想定通り出力されていること。", logs.get(0),
                containsString("COMMIT COUNT = [5]"));
        assertThat("ログが想定通り出力されていること。", logs.get(1),
                containsString("COMMIT COUNT = [10]"));
        assertThat("ログが想定通り出力されていること。", logs.get(2),
                containsString("COMMIT COUNT = [110]"));
    }

    /**
     * {@link BasicCommitLogger#increment(long)}のテスト。
     * 初期化せずに呼び出した場合。
     */
    @Test
    public void testIncrementNotInitialized() {

        BasicCommitLogger logger = new BasicCommitLogger();
        LogWriter.clear();
        try {
            logger.increment(100);
            fail("");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is("not initialized object."));
        }

        // ターミネートは何も起きない。
        logger.terminate();
        List<String> log = LogWriter.getLog();
        assertThat(log.size(), is(0));
    }

    /** {@link nablarch.core.log.app.BasicCommitLogger#terminate()}のテスト。 */
    @Test
    public void testTerminate() {
        BasicCommitLogger logger = new BasicCommitLogger();
        LogWriter.clear();

        logger.initialize();
        logger.setInterval(500);
        logger.increment(1);

        // ログは出力されていない。
        List<String> log = LogWriter.getLog();
        assertThat(log.size(), is(0));

        // 終了処理後に最終コミットログが出力される。
        logger.terminate();
        assertThat(log.size(), is(1));
        assertThat(log.get(0), containsString("TOTAL COMMIT COUNT = [1]"));

    }

    /** テスト用のログライタクラス。 */
    public static class LogWriter extends LogWriterSupport {

        private static List<String> log = new ArrayList<String>();

        @Override
        protected void onWrite(String formattedMessage) {
            log.add(formattedMessage);
        }

        public static void clear() {
            log.clear();
        }

        public static List<String> getLog() {
            return log;
        }
    }
}
