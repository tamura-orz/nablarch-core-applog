package nablarch.core.log.app;

import nablarch.core.log.app.LogInitializationHelperTest.RuntimeExceptionThrowingLogUtil.MyException;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


/**
 * {@link LogInitializationHelperTest}のテストクラス。
 *
 * @author T.Kawasaki
 */
public class LogInitializationHelperTest {

    /** デフォルト設定で初期化が成功すること。 */
    @Test
    public void testInitialize() {
        LogInitializationHelper.initialize();
    }

    /** 引数（初期化対象クラス）がnullの場合、例外が発生すること。 */
    @Test(expected = IllegalArgumentException.class)
    public void testInitializeFailNull() {
        LogInitializationHelper.initialize((String[]) null);
    }

    /** 引数（初期化対象クラス）が空の場合、例外が発生すること。 */
    @Test(expected = IllegalArgumentException.class)
    public void testInitializeFailEmpty() {
        LogInitializationHelper.initialize(new String[0]);
    }

    /** 引数（初期化対象クラス）がクラスパスに存在しない場合、何も起こらないこと。*/
    @Test
    public void testInitializeUnknownClass() {
        LogInitializationHelper.initialize("unknown");
    }


    /** 常にチェック例外を発生させる初期化対象クラス。*/
    public static class ExceptionThrowingLogUtil {
        public static void initialize() throws Exception {
            throw new Exception("for test.");
        }
    }

    /**
     * 初期化処理実行に失敗した場合（チェック例外が発生した場合）、
     * 元例外がラップされること。
     */
    @Test
    public void testInvocationFail() {
        try {
            LogInitializationHelper.initialize(ExceptionThrowingLogUtil.class.getName());
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), containsString("failed to initialize."));
            assertThat(e.getMessage(), containsString("ExceptionThrowingLogUtil"));
            assertThat(e.getCause(), instanceOf(InvocationTargetException.class));
        }
    }

    /** 常にチェック例外を発生させる初期化対象クラス。*/
    public static class RuntimeExceptionThrowingLogUtil {
        static class MyException extends RuntimeException {
        }
        public static void initialize() {
            throw new MyException();
        }
    }


    /**
     * 初期化処理実行に失敗した場合（非チェック例外が発生した場合）、
     * 元例外がスローされること。
     */
    @Test
    public void testInvocationFailUnchecked() {
        try {
            LogInitializationHelper.initialize(RuntimeExceptionThrowingLogUtil.class.getName());
            fail();
        } catch (MyException e) {
            // OK
        }
    }

    /** 初期化メソッドがない初期化対象クラス。 */
    public static class NoMethodLogUtil {
    }

    /** 初期化メソッド（initialize()）が存在しない場合、例外が発生すること。*/
    @Test
    public void testNoSuchMethod() {
        try {
            LogInitializationHelper.initialize(NoMethodLogUtil.class.getName());
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), containsString("failed to initialize."));
            assertThat(e.getMessage(), containsString("NoMethodLogUtil"));
            assertThat(e.getCause(), instanceOf(NoSuchMethodException.class));
        }
    }

    /** 初期化メソッドがprivateである初期化対象クラス。*/
    public static class PrivateMethodLogUtil {
        private static void initialize() {
        }
    }

    /** 初期化メソッドにアクセス不可である場合、例外が発生すること。 */
    @Test
    public void testIllegalAccess() {
        try {
            LogInitializationHelper.initialize(PrivateMethodLogUtil.class.getName());
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), containsString("failed to initialize."));
            assertThat(e.getMessage(), containsString("PrivateMethodLogUtil"));
            assertThat(e.getCause(), instanceOf(IllegalAccessException.class));
        }
    }
}
