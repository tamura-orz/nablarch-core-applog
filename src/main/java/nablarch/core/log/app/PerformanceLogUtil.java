package nablarch.core.log.app;

import java.util.Map;

import nablarch.core.log.LogUtil;
import nablarch.core.log.LogUtil.ObjectCreator;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.ObjectUtil;
import nablarch.core.util.annotation.Published;

/**
 * パフォーマンスログを出力するクラス。
 * @author Kiyohito Itoh
 */
public final class PerformanceLogUtil {
    
    /** 隠蔽コンストラクタ */
    private PerformanceLogUtil() {
    }
    
    /** パフォーマンスログを出力するロガー */
    private static final Logger PERFORMANCE_LOGGER = LoggerManager.get("PERFORMANCE");
    
    /** {@link PerformanceLogFormatter}のクラス名 */
    private static final String PROPS_CLASS_NAME = PerformanceLogFormatter.PROPS_PREFIX + "className";
    
    /** {@link PerformanceLogFormatter}を生成する{@link ObjectCreator} */
    private static final ObjectCreator<PerformanceLogFormatter> PERFORMANCE_LOG_FORMATTER_CREATOR = new ObjectCreator<PerformanceLogFormatter>() {
        public PerformanceLogFormatter create() {
            PerformanceLogFormatter formatter = null;
            Map<String, String> props = AppLogUtil.getProps();
            if (props.containsKey(PROPS_CLASS_NAME)) {
                String className =  props.get(PROPS_CLASS_NAME);
                formatter = ObjectUtil.createInstance(className);
            } else {
                formatter = new PerformanceLogFormatter();
            }
            return formatter;
        }
    };
    
    /**
     * クラスローダに紐付く{@link PerformanceLogFormatter}を生成する。
     */
    public static void initialize() {
        getPerformanceLogFormatter();
    }
    
    /**
     * クラスローダに紐付く{@link PerformanceLogFormatter}を取得する。
     * @return {@link PerformanceLogFormatter}
     */
    private static PerformanceLogFormatter getPerformanceLogFormatter() {
        return LogUtil.getObjectBoundToClassLoader(PERFORMANCE_LOG_FORMATTER_CREATOR);
    }
    
    /**
     * 測定を開始する。
     * @param point 測定対象を識別するID
     */
    @Published(tag = "architect")
    public static void start(String point) {
        if (!PERFORMANCE_LOGGER.isDebugEnabled()) {
            return;
        }
        PerformanceLogFormatter formatter = getPerformanceLogFormatter();
        if (!formatter.isTargetPoint(point)) {
            return;
        }
        formatter.start(point);
    }
    
    /**
     * 測定を終了しパフォーマンスログを出力する。
     * @param point 測定対象を識別するID
     * @param result 処理結果を表す文字列
     * @param logOptions ログのオプション情報
     */
    @Published(tag = "architect")
    public static void end(String point, String result, Object... logOptions) {
        if (!PERFORMANCE_LOGGER.isDebugEnabled()) {
            return;
        }
        PerformanceLogFormatter formatter = getPerformanceLogFormatter();
        if (!formatter.isTargetPoint(point)) {
            return;
        }
        String message = getPerformanceLogFormatter().end(point, result);
        PERFORMANCE_LOGGER.logDebug(message, logOptions);
    }
}
