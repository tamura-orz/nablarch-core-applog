package nablarch.core.log.basic;

import nablarch.core.util.annotation.Published;

/**
 * ログを出力先に書き込むインタフェース。<br>
 * <br>
 * 出力先の媒体毎に本インタフェースの実装クラスを作成する。
 * 
 * @author Kiyohito Itoh
 */
@Published(tag = "architect")
public interface LogWriter {
    
    /**
     * 初期処理を行う。<br>
     * <br>
     * ログの出力先に応じたリソースの確保などを行う。
     * 
     * @param settings LogWriterの設定
     */
    void initialize(ObjectSettings settings);
    
    /**
     * 終了処理を行う。<br>
     * <br>
     * ログの出力先に応じて確保しているリソースの解放などを行う。
     */
    void terminate();

    /**
     * ログを出力先に書き込む。
     * @param context {@link LogContext}
     */
    void write(LogContext context);
}
