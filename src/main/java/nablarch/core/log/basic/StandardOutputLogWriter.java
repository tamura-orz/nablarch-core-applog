package nablarch.core.log.basic;

/**
 * 標準出力にログを書き込むクラス。<br>
 * <br>
 * 開発時にコンソール上で出力されたログを確認する場合などに使用できる。
 * 
 * @author Kiyohito Itoh
 */
public class StandardOutputLogWriter extends LogWriterSupport {

    /**
     * 標準出力にログを書き込む。
     * @param formattedMessage フォーマット済みのログ
     */
    protected void onWrite(String formattedMessage) {
        System.out.print(formattedMessage);
    }
}
