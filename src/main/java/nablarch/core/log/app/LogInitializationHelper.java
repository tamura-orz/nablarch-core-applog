package nablarch.core.log.app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ログ初期化処理を助けるクラス。
 *
 * 依存関係にないために静的に初期化処理を呼び出せない
 * ログユーティリティクラスに対して初期化処理を起動する。
 *
 * @author T.Kawasaki
 */
public class LogInitializationHelper {

    /** デフォルトの初期化対象クラスの一覧 */
    static final String[] DEFAULT_CLASSES_TO_INITIALIZE = {
            "nablarch.core.db.statement.SqlLogUtil",
            "nablarch.fw.messaging.logging.MessagingLogUtil"
    };

    /** 初期化対象となるクラス */
    private final String[] classesToInitialize;

    /**
     * コンストラクタ。
     *
     * @param classesToInitialize 初期化対象クラスの一覧(非null)
     */
    LogInitializationHelper(String... classesToInitialize) {
        if (classesToInitialize == null || classesToInitialize.length == 0) {
            throw new IllegalArgumentException("argument must not be null or empty.");
        }
        this.classesToInitialize = classesToInitialize;
    }

    /**
     * 初期化を行う。
     *
     * デフォルトの初期化対象クラスに対して初期化処理を呼び出す。
     */
    public static void initialize() {
        initialize(DEFAULT_CLASSES_TO_INITIALIZE);
    }

    /**
     * 初期化を行う。
     * 指定された初期化対象クラスに対して初期化処理を呼び出す。
     * @param classesToInitialize 初期化対象クラスの一覧（非null）
     */
    static void initialize(String... classesToInitialize) {
        LogInitializationHelper initializer = new LogInitializationHelper(classesToInitialize);
        initializer.initializeAll();
    }

    /** 全ての初期化対象クラスの初期化を行う。*/
    void initializeAll() {
        for (String className : classesToInitialize) {
            invokeInitialize(className);
        }
    }

    /**
     * 指定されたクラスの初期化処理を呼び出す。
     * 指定されたクラスが存在しない場合、何もしない。
     *
     * @param classNameToInit 初期化対象となるクラス名
     */
    private void invokeInitialize(String classNameToInit) {
        Class<?> clazz;
        try {
            clazz = Class.forName(classNameToInit);
        } catch (ClassNotFoundException e) {
            // 対象となるクラスがクラスパスにないので、
            // 初期化は不要
            return;
        }
        invokeInitialize(clazz);
    }

    /**
     * 指定されたクラスの初期化処理を呼び出す。
     *
     * @param clazz 初期化対象となるクラス
     * @throws IllegalStateException 初期化処理呼び出しに失敗した場合
     */
    private void invokeInitialize(Class<?> clazz) throws IllegalStateException {
        try {
            Method initializeMethod = clazz.getDeclaredMethod("initialize");
            initializeMethod.invoke(null);
        } catch (NoSuchMethodException e) {
            throw wrap(clazz, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw wrap(clazz, e);
        } catch (IllegalAccessException e) {
            throw wrap(clazz, e);
        }
    }

    /**
     * 発生したチェック例外をラップする。
     *
     * @param clazz 初期化に失敗したクラス
     * @param e 初期化時に発生した例外
     * @return 元例外をラップした非チェック例外
     */
    private IllegalStateException wrap(Class<?> clazz, Exception e) {
        String msg = "failed to initialize. class=[" + clazz.getName() + "]";
        return new IllegalStateException(msg, e);
    }

}
