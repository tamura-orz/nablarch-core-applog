package nablarch.core.log.basic;

import nablarch.core.util.annotation.Published;

/**
 * ログのフォーマットを行うインタフェース。<br>
 * <br>
 * ログのフォーマットの種類毎に本インタフェースの実装クラスを作成する。
 * 
 * @author Kiyohito Itoh
 */
@Published(tag = "architect")
public interface LogFormatter {
    
    /**
     * 初期処理を行う。
     * @param settings LogFormatterの設定
     */
    void initialize(ObjectSettings settings);
    
    /**
     * ログのフォーマットを行う。
     * @param context {@link LogContext}
     * @return フォーマット済みのログ
     */
    String format(LogContext context);
}
