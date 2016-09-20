package nablarch.core.log.app;

import java.util.Map;

import nablarch.core.log.LogUtil;
import nablarch.core.log.LogUtil.ObjectCreator;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.ObjectUtil;
import nablarch.core.util.annotation.Published;

/**
 * 障害ログを出力するユーティリティクラス。
 * <p/>
 * 本ユーティリティを使用するには、app-log.propertiesの設定が必要である。<br/>
 * 障害通知ログは"MONITOR"、障害解析ログは本クラス名(FQCN)をロガー名に使用する。<br/>
 * ログレベルは、ログ出力に使用したメソッドにより決まる。<br/>
 *
 * @author Kiyohito Itoh
 */
public final class FailureLogUtil {
    
    /** 隠蔽コンストラクタ。 */
    private FailureLogUtil() {
    }
    
    /** 障害通知ログを出力するロガー */
    private static final Logger MONITOR_LOGGER = LoggerManager.get("MONITOR");
    
    /** 障害解析ログを出力するロガー */
    private static final Logger ANALYSIS_LOGGER = LoggerManager.get(FailureLogUtil.class);
    
    /** {@link FailureLogFormatter}を生成する{@link ObjectCreator} */
    private static final ObjectCreator<FailureLogFormatter> FAILURE_LOG_FORMATTER_CREATOR = new ObjectCreator<FailureLogFormatter>() {
        public FailureLogFormatter create() {
            FailureLogFormatter formatter = null;
            Map<String, String> props = AppLogUtil.getProps();
            if (props.containsKey(PROPS_CLASS_NAME)) {
                String className =  props.get(PROPS_CLASS_NAME);
                formatter = ObjectUtil.createInstance(className);
            } else {
                formatter = new FailureLogFormatter();
            }
            return formatter;
        }
    };
    
    /** 使用する{@link FailureLogFormatter}のクラス名を取得する際に使用するプロパティ名 */
    private static final String PROPS_CLASS_NAME = FailureLogFormatter.PROPS_PREFIX + "className";
    
    /** クラスローダに紐付く{@link FailureLogFormatter}を生成する。 */
    public static void initialize() {
        getFailureLogFormatter();
    }
    
    /**
     * クラスローダに紐付く{@link FailureLogFormatter}を取得する。
     * @return {@link FailureLogFormatter}
     */
    private static FailureLogFormatter getFailureLogFormatter() {
        return LogUtil.getObjectBoundToClassLoader(FAILURE_LOG_FORMATTER_CREATOR);
    }
    
    /**
     * FATALレベルの障害通知ログと障害解析ログを出力する。
     * @param data 処理対象データ
     * @param failureCode 障害コード
     * @param messageOptions 障害コードからメッセージを取得する際に使用するオプション情報
     */
    @Published(tag = "architect")
    public static void logFatal(Object data, String failureCode, Object... messageOptions) {
        logFatal(null, data, failureCode, messageOptions);
    }
    
    /**
     * FATALレベルの障害通知ログと障害解析ログを出力する。
     * @param error エラー情報
     * @param data 処理対象データ
     * @param failureCode 障害コード
     * @param messageOptions 障害コードからメッセージを取得する際に使用するオプション情報
     */
    @Published(tag = "architect")
    public static void logFatal(Throwable error, Object data, String failureCode, Object... messageOptions) {
        logFatal(error, data, failureCode, messageOptions, null);
    }
    
    /**
     * FATALレベルの障害通知ログと障害解析ログを出力する。
     * @param error エラー情報
     * @param data 処理対象データ
     * @param failureCode 障害コード
     * @param messageOptions 障害コードからメッセージを取得する際に使用するオプション情報
     * @param logOptions ログのオプション情報
     */
    @Published(tag = "architect")
    public static void logFatal(Throwable error, Object data, String failureCode, Object[] messageOptions, Object[] logOptions) {
        String notificationMessage = getFailureLogFormatter().formatNotificationMessage(error, data, failureCode, messageOptions);
        String errorMessage = getFailureLogFormatter().formatAnalysisMessage(error, data, failureCode, messageOptions);
        MONITOR_LOGGER.logFatal(notificationMessage, error, logOptions);
        ANALYSIS_LOGGER.logFatal(errorMessage, error, logOptions);
    }
    
    /**
     * ERRORレベルの障害通知ログと障害解析ログを出力する。
     * @param data 処理対象データ
     * @param failureCode 障害コード
     * @param messageOptions 障害コードからメッセージを取得する際に使用するオプション情報
     */
    @Published(tag = "architect")
    public static void logError(Object data, String failureCode, Object... messageOptions) {
        logError(null, data, failureCode, messageOptions);
    }
    
    /**
     * ERRORレベルの障害通知ログと障害解析ログを出力する。
     * @param error エラー情報
     * @param data 処理対象データ
     * @param failureCode 障害コード 
     * @param messageOptions 障害コードからメッセージを取得する際に使用するオプション情報
     */
    @Published(tag = "architect")
    public static void logError(Throwable error, Object data, String failureCode, Object... messageOptions) {
        logError(error, data, failureCode, messageOptions, null);
    }
    
    /**
     * ERRORレベルの障害通知ログと障害解析ログを出力する。
     * @param error エラー情報
     * @param data 処理対象データ
     * @param failureCode 障害コード
     * @param messageOptions 障害コードからメッセージを取得する際に使用するオプション情報
     * @param logOptions ログのオプション情報
     */
    @Published(tag = "architect")
    public static void logError(Throwable error, Object data, String failureCode, Object[] messageOptions, Object[] logOptions) {
        String notificationMessage = getNotificationMessage(error, data, failureCode, messageOptions);
        String errorMessage = getFailureLogFormatter().formatAnalysisMessage(error, data, failureCode, messageOptions);
        MONITOR_LOGGER.logError(notificationMessage, error, logOptions);
        ANALYSIS_LOGGER.logError(errorMessage, error, logOptions);
    }
    
    /**
     * WARNレベルの障害解析ログを出力する。
     * <p/>
     * フレームワークにおいて複数例外発生時に障害ログとして出力できない例外をログ出力する場合に使用する。
     * @param error エラー情報
     * @param data 処理対象データ
     * @param failureCode 障害コード 
     * @param messageOptions 障害コードからメッセージを取得する際に使用するオプション情報
     */
    @Published(tag = "architect")
    public static void logWarn(Throwable error, Object data, String failureCode, Object... messageOptions) {
        String errorMessage = getFailureLogFormatter().formatAnalysisMessage(error, data, failureCode, messageOptions);
        ANALYSIS_LOGGER.logWarn(errorMessage, error);
    }
    
    /**
     * フォーマットされた障害通知ログのメッセージを取得する。
     * @param data 処理対象データ
     * @param failureCode 障害コード
     * @param messageOptions 障害コードからメッセージを取得する際に使用するオプション情報
     * @return フォーマット済みのメッセージ
     */
    public static String getNotificationMessage(Object data, String failureCode, Object... messageOptions) {
        return getNotificationMessage(null, data, failureCode, messageOptions);
    }
    
    /**
     * フォーマットされた障害通知ログのメッセージを取得する。
     * @param error エラー情報
     * @param data 処理対象データ
     * @param failureCode 障害コード
     * @param messageOptions 障害コードからメッセージを取得する際に使用するオプション情報
     * @return フォーマット済みのメッセージ
     */
    public static String getNotificationMessage(Throwable error, Object data, String failureCode, Object[] messageOptions) {
        return getFailureLogFormatter().formatNotificationMessage(error, data, failureCode, messageOptions);
    }
}
