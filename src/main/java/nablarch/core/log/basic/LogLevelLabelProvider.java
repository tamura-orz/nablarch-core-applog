package nablarch.core.log.basic;

/**
 * {@link LogLevel}を表す文言を提供するクラス。<br>
 * <br>
 * {@link LogLevel}を表す文言を{@link LogFormatter}の設定から取得する。<br>
 * 設定がない場合は、{@link LogLevel}の名称を使用する。<br>
 * <br>
 * プロパティファイルの記述ルールを下記に示す。
 * <dl>
 *   <dt>label.&lt;{@link LogLevel}の名称の小文字&gt;
 *   <dd>{@link LogLevel}に使用するラベル。オプション。<br>
 *       指定しなければ{@link LogLevel}の名称を使用する。
 * </dl>
 * 
 * @author Kiyohito Itoh
 */
public class LogLevelLabelProvider {

    /** FATALレベルに使用するラベル */
    private String fatalLabel;
    
    /** ERRORレベルに使用するラベル */
    private String errorLabel;
    
    /** WARNレベルに使用するラベル */
    private String warnLabel;
    
    /** INFOレベルに使用するラベル */
    private String infoLabel;
    
    /** DEBUGレベルに使用するラベル */
    private String debugLabel;
    
    /** TRACEレベルに使用するラベル */
    private String traceLabel;
    
    /**
     * コンストラクタ。<br>
     * <br>
     * {@link LogFormatter}の設定を使用してラベルを初期化する。<br>
     * 指定がない場合は、{@link LogLevel}の名称を使用する。
     * 
     * @param settings {@link LogFormatter}の設定
     */
    public LogLevelLabelProvider(ObjectSettings settings) {
        fatalLabel = getLevelLabel(settings, LogLevel.FATAL);
        errorLabel = getLevelLabel(settings, LogLevel.ERROR);
        warnLabel = getLevelLabel(settings, LogLevel.WARN);
        infoLabel = getLevelLabel(settings, LogLevel.INFO);
        debugLabel = getLevelLabel(settings, LogLevel.DEBUG);
        traceLabel = getLevelLabel(settings, LogLevel.TRACE);
    }
    
    /**
     * 指定された{@link LogLevel}に対するラベルを取得する。
     * @param settings {@link LogFormatter}の設定
     * @param level {@link LogLevel}
     * @return 指定された{@link LogLevel}に対するラベル
     */
    protected String getLevelLabel(ObjectSettings settings, LogLevel level) {
        String label = settings.getProp("label." + level.name().toLowerCase());
        return label != null ? label : level.name();
    }
    
    /**
     * {@link LogLevel}に使用するラベルを取得する。
     * @param level {@link LogLevel}
     * @return {@link LogLevel}に使用するラベル
     */
    public String getLevelLabel(LogLevel level) {
        switch (level) {
            case FATAL: return fatalLabel;
            case ERROR: return errorLabel;
            case WARN: return warnLabel;
            case INFO: return infoLabel;
            case DEBUG: return debugLabel;
            case TRACE: return traceLabel;
            // 列挙型を追加しない限り到達不能
            default: return level.name();
        }
    }
}
