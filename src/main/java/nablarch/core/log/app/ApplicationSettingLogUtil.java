package nablarch.core.log.app;

import java.util.Map;

import nablarch.core.log.LogUtil;
import nablarch.core.util.ObjectUtil;
import nablarch.core.util.annotation.Published;

/**
 * アプリケーション設定に関するログ出力をサポートするユーティリティクラス。
 * <p/>
 * 本クラスはフォーマッタとして{@link ApplicationSettingLogFormatter}を使用する。
 * {@link ApplicationSettingLogFormatter}は以下の方法で決定される。
 * <ol>
 *     <li>設定ファイルにプロパティ名"applicationSettingLogFormatter.className"で定義されているクラスを使用する</li>
 *     <li>定義されていない場合は、{@link ApplicationSettingLogFormatter}を使用する</li>
 * </ol>
 * <p/>
 *
 * @author hisaaki sioiri
 */
@Published(tag = "architect")
public final class ApplicationSettingLogUtil {

    /** 隠蔽コンストラクタ */
    private ApplicationSettingLogUtil() {
    }

    /** このクラスで使用するログフォーマッターのクラス名 */
    private static final String PROPS_CLASS_NAME =
            ApplicationSettingLogFormatter.PROPS_PREFIX + "className";

    /**
     * このクラスで使用するログフォーマッターを生成する {@link nablarch.core.log.LogUtil.ObjectCreator}
     */
    private static final LogUtil.ObjectCreator<ApplicationSettingLogFormatter> LAUNCHER_LOG_FORMATTER_CREATOR
            = new LogUtil.ObjectCreator<ApplicationSettingLogFormatter>() {
        public ApplicationSettingLogFormatter create() {
            ApplicationSettingLogFormatter formatter;
            Map<String, String> props = AppLogUtil.getProps();
            if (props.containsKey(PROPS_CLASS_NAME)) {
                String className = props.get(PROPS_CLASS_NAME);
                formatter = ObjectUtil.createInstance(className);
            } else {
                formatter = new ApplicationSettingLogFormatter();
            }
            return formatter;
        }
    };

    /**
     * 本クラスで使用する{@link ApplicationSettingLogFormatter}を生成し、
     * カレントスレッドのコンテキストクラスローダに紐付ける。
     *
     */
    public static void initialize() {
        getLogFormatter();
    }

    /**
     * このクラスで使用するログフォーマッターを取得する。
     * @return ログフォーマッター
     */
    private static ApplicationSettingLogFormatter getLogFormatter() {
        return LogUtil.getObjectBoundToClassLoader(
                LAUNCHER_LOG_FORMATTER_CREATOR);
    }

    /**
     * アプリケーション設定を表すフォーマット済みのログメッセージを生成し、返却する。
     * <p/>
     * 本メソッドは{@link ApplicationSettingLogFormatter#getAppSettingsLogMsg()} に処理を委譲する。
     *
     * @return 生成したフォーマット済みログメッセージ
     */
    public static String getAppSettingsLogMsg() {
        return getLogFormatter().getAppSettingsLogMsg();
    }

    /**
     * アプリケーション設定と業務日付を表すフォーマット済みのログメッセージを生成し、返却する。
     * <p/>
     * 本メソッドは{@link ApplicationSettingLogFormatter#getAppSettingsWithDateLogMsg()} に処理を委譲する。
     *
     * @return 生成したフォーマット済みログメッセージ
     */
    public static String getAppSettingsWithDateLogMsg() {
        return getLogFormatter().getAppSettingsWithDateLogMsg();
    }
}

