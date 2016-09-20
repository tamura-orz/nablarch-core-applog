package nablarch.core.log.app;

import nablarch.core.util.annotation.Published;
import nablarch.fw.ExecutionContext;

/**
 * コミットログ出力インタフェース。
 *
 * @author hisaaki sioiri
 */
@Published(tag = "architect")
public interface CommitLogger {

    /** セッションに自身のインスタンスを格納する際に使用するキー */
    String SESSION_SCOPE_KEY = ExecutionContext.FW_PREFIX + ".commit-logger";

    /** 初期処理を行う。 */
    void initialize();

    /**
     * コミット件数のインクリメントを行う。
     *
     * @param count コミット済み件数
     */
    void increment(long count);

    /** 終了処理を行う。 */
    void terminate();
}
