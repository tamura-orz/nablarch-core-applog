package nablarch.core.log.basic;

import java.io.File;
import java.io.IOException;

import nablarch.core.log.Logger;
import nablarch.core.log.app.FailureLogUtil;
import nablarch.core.util.Builder;

/**
 * ロックファイルを用いて排他制御を行いながらファイルにログを書き込むクラス。
 * <p>
 * 本クラスを使用するとプロセスをまたがってログ出力処理を直列化できるので、複数プロセスから同一のファイルにログ出力を行う場合でも確実にログを出力できる。<br/>
 * 本クラスは障害通知ログのように出力頻度が低く、かつサーバ単位でログファイルを一元管理するほうが効率的なログの出力にのみ使用することを想定している。
 * 頻繁にログの出力が行われる場面で本クラスを使用するとロック取得待ちによって性能が劣化する可能性があるので、
 * アプリケーションログやアクセスログのように出力頻度の高いログの出力に本クラスを使用してはいけない。<br/>
 * </p>
 * <p>
 * 本クラスはロック取得の待機時間を超えてもロックを取得できない場合、
 * 強制的にロックファイルを削除し、ロックファイルを生成してからログの出力を行う。<br/>
 * もし強制的にロックファイルを削除できない場合は、ロックを取得していない状態で強制的にログの出力を行い、処理を終了する。<br/>
 * また、ロックファイルの生成に失敗した場合および、ロック取得待ちの際に割り込みが発生した場合も、ロックを取得していない状態で強制的にログの出力を行い、処理を終了する。
 * </p>
 * @author Masato Inoue
 */
public class SynchronousFileLogWriter extends FileLogWriter {

    /** ロック取得の再試行間隔（ミリ秒）の最小値（0にするとsleepなしのループになるので、1を最小値とする） */
    private static final int MIN_LOCK_RETRY_INTERVAL = 1;

    /** ロック取得の待機時間（ミリ秒）の最小値 */
    private static final int MIN_LOCK_WAIT_TIME = 0;

    /** ロック取得の再試行間隔（ミリ秒）のデフォルト値 */
    private static final int DEFAULT_LOCK_RETRY_INTERVAL = 1;

    /** ロック取得の待機時間（ミリ秒）のデフォルト値 */
    private static final int DEFAULT_LOCK_WAIT_TIME = 1800;

    /** ロックファイル */
    private File lockFile;

    /** ロックファイルのパス */
    private String lockFilePath;

    /** ロック取得の再試行間隔（ミリ秒） */
    private long lockRetryInterval;

    /** ロック取得の待機時間（ミリ秒） */
    private long lockWaitTime;

    /** ロックファイルが生成できない場合の障害通知コード */
    private String failureCodeCreateLockFile = null;

    /** 生成したロックファイルを解放（削除）できない場合の障害通知コード */
    private String failureCodeReleaseLockFile = null;

    /** 解放されないロックファイルを強制削除できない場合の障害通知コード（待機時間を超えた場合に発生） */
    private String failureCodeForceDeleteLockFile = null;

    /** ロック待ちでスレッドをスリープしている際に、割り込みが発生した場合の障害通知コード */
    private String failureCodeInterruptLockWait = null;

    /**{@inheritDoc} */
    @Override
    protected void onInitialize(ObjectSettings settings) {

        lockFilePath = settings.getRequiredProp("lockFilePath");
        lockFile = new File(lockFilePath);
        if (lockFile.exists() && lockFile.isDirectory()) {
            // ロックファイルが既に存在していて、それがディレクトリの場合には処理を終了する。
            throw new IllegalArgumentException("lock file path is already exists of directory. lock file path =[" + lockFilePath + "]");
        }

        if (settings.getProp("lockRetryInterval") == null) {
            lockRetryInterval = DEFAULT_LOCK_RETRY_INTERVAL;
        } else {
            try {
                lockRetryInterval = Long.parseLong(settings.getProp("lockRetryInterval"));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(Builder.concat(
                        "invalid property was specified. 'lockRetryInterval' must be able to convert to Long. value=["
                      , settings.getProp("lockRetryInterval")
                      , "]."), e);
            }

            if (lockRetryInterval < MIN_LOCK_RETRY_INTERVAL) {
                throw new IllegalArgumentException(Builder.concat(
                        "invalid property was specified. 'lockRetryInterval' must be more than ", MIN_LOCK_RETRY_INTERVAL, ". value=["
                      , settings.getProp("lockRetryInterval")
                      , "]."));
            }

        }

        if (settings.getProp("lockWaitTime") == null) {
            lockWaitTime = DEFAULT_LOCK_WAIT_TIME;
        } else {
            try {
                lockWaitTime = Long.parseLong(settings.getProp("lockWaitTime"));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(Builder.concat(
                        "invalid property was specified. 'lockWaitTime' must be able to convert to Long. value=["
                      , settings.getProp("lockWaitTime")
                      , "]."), e);
            }
            if (lockWaitTime < MIN_LOCK_WAIT_TIME) {
                throw new IllegalArgumentException(Builder.concat(
                        "invalid property was specified. 'lockWaitTime' must be more than 0. value=["
                      , settings.getProp("lockWaitTime")
                      , "]."));
            }
        }

        if (settings.getProp("failureCodeCreateLockFile") != null) {
            failureCodeCreateLockFile = settings.getProp("failureCodeCreateLockFile");
        }
        if (settings.getProp("failureCodeReleaseLockFile") != null) {
            failureCodeReleaseLockFile = settings.getProp("failureCodeReleaseLockFile");
        }
        if (settings.getProp("failureCodeForceDeleteLockFile") != null) {
            failureCodeForceDeleteLockFile = settings.getProp("failureCodeForceDeleteLockFile");
        }
        if (settings.getProp("failureCodeInterruptLockWait") != null) {
            failureCodeInterruptLockWait = settings.getProp("failureCodeInterruptLockWait");
        }
        super.onInitialize(settings);
    }

    /**
     * 設定情報を取得する。<br>
     * <br>
     * 設定情報のフォーマットを下記に示す。<br>
     * <br>
     * <pre>
     * {@literal
     * WRITER NAME        = [<{@link LogWriter}の名称>]
     * WRITER CLASS       = [<{@link LogWriter}のクラス名>]
     * FORMATTER CLASS    = [<{@link LogFormatter}のクラス名>]
     * LEVEL              = [<ログの出力制御の基準とするLogLevel>]
     * FILE PATH          = [<書き込み先のファイルパス>]
     * ENCODING           = [<書き込み時に使用する文字エンコーディング>]
     * OUTPUT BUFFER SIZE = [<出力バッファのサイズ>]
     * FILE AUTO CHANGE   = [<ログファイルを自動で切り替えるか否か。>]
     * MAX FILE SIZE      = [<書き込み先ファイルの最大サイズ>]
     * CURRENT FILE SIZE  = [<書き込み先ファイルの現在のサイズ>]
     * LOCK FILE PATH                      = [<ロックファイルのパス>]
     * LOCK RETRY INTERVAL                 = [<ロック取得の再試行間隔（ミリ秒）>]
     * LOCK WAIT TIME                      = [<ロック取得の待機時間（ミリ秒）>]
     * FAILURE CODE CREATE LOCK FILE       = [<生成したロックファイルを削除できない場合の障害コード>]
     * FAILURE CODE RELEASE LOCK FILE      = [<生成したロックファイルを解放（削除）できない場合の障害コード>]
     * FAILURE CODE FORCE DELETE LOCK FILE = [<解放されないロックファイルを強制削除できない場合の障害コードド>]
     * FAILURE CODE INTERRUPT LOCK WAIT    = [<ロック待ちでスレッドをスリープしている際に、割り込みが発生した場合の障害コード>]
     * }
     * </pre>
     * @return 設定情報
     * @see FileLogWriter#getSettings()
     * @see LogLevel
     */
    @Override
    protected String getSettings() {
        return new StringBuilder(768)
                .append(super.getSettings())
                .append("\tLOCK FILE PATH                      = [").append(lockFilePath).append("]").append(Logger.LS)
                .append("\tLOCK RETRY INTERVAL                 = [").append(lockRetryInterval).append("]").append(Logger.LS)
                .append("\tLOCK WAIT TIME                      = [").append(lockWaitTime).append("]").append(Logger.LS)
                .append("\tFAILURE CODE CREATE LOCK FILE       = [").append(failureCodeCreateLockFile).append("]").append(Logger.LS)
                .append("\tFAILURE CODE RELEASE LOCK FILE      = [").append(failureCodeReleaseLockFile).append("]").append(Logger.LS)
                .append("\tFAILURE CODE FORCE DELETE LOCK FILE = [").append(failureCodeForceDeleteLockFile).append("]").append(Logger.LS)
                .append("\tFAILURE CODE INTERRUPT LOCK WAIT    = [").append(failureCodeInterruptLockWait).append("]").append(Logger.LS)
                .toString();
    }

    /** {@inheritDoc}
     */
    @Override
    public void write(LogContext context) {
        if (needsToWrite(context)) {
            onWrite(getFormatter().format(context), context);
        }
    }

    /**
     * ロックファイルを使用して排他制御を行いながらファイルにログ書き込みを行う。
     * <p/>
     * 排他制御の仕様を以下に示す。
     * <ul>
     * <li>ロックファイルの生成に成功した場合、ログ書き込みを行い、ロックファイルを削除する。</li>
     * <li>ファイルパス不正などでロックファイルの生成に失敗した場合、引数で渡されたメッセージとロックファイルが作成できなかった旨のメッセージを、強制的にログファイルに出力する。</li>
     * <li>既にロックファイルが存在するためにロックファイルの生成に失敗した場合、一定時間処理をスリープさせ、再試行する。</li>
     * <li>ロック取得待機時間を超えてもロックファイルを生成できなかった場合、不要なロックファイルが残存しているとみなしロックファイルを削除し、再度ロックファイルの生成を試みる。
     * もしロックファイルの削除に失敗した場合、引数で渡されたメッセージとロックファイルの削除に失敗した旨のメッセージを、強制的にログファイルに出力する。</li>
     * <li>ロック取得待ちの際に割り込みが発生した場合、引数で渡されたメッセージと割り込みが発生した旨のメッセージを、強制的にログファイルに出力する。</li>
     * </ul>
     * @param formattedMessage フォーマット済みのログ
     * @param context ログエントリオブジェクト
     */
    protected synchronized void onWrite(String formattedMessage, LogContext context) {
        // ロックファイルを生成する
        if (lockFile(formattedMessage, context)) {
            try {
                super.onWrite(formattedMessage);
            } finally {
                // ロックファイルを解放する
                releaseLock(formattedMessage, context);
            }
        }
    }

    /**
     * ロックファイルを作成し、ログファイルをロックする。
     * @param formattedMessage フォーマット済みのログ
     * @param context ログエントリオブジェクト
     * @return ロックファイルの作成結果（true:成功 false:失敗）
     */
    protected boolean lockFile(String formattedMessage, LogContext context) {
        long before = System.currentTimeMillis();

        // ロックファイル作成が成功するまで何度も試行する
        while (true) {
            boolean isLockSuccess;
            try {
                isLockSuccess = lockFile.createNewFile();
            } catch (IOException e) {
                String lockFileAbsolutePath = lockFile.getAbsolutePath();
                // ロックファイルの生成に失敗する場合、ロックの取得は不可能と判断し強制的にログを出力する
                String failureMessage = getFormattingFailureMessage(
                        context
                      , Builder.concat("failed to create lock file. perhaps lock file path was invalid. lock file path=[", lockFileAbsolutePath, "].")
                      , failureCodeCreateLockFile
                      , lockFileAbsolutePath);
                forceWrite(formattedMessage, context, failureMessage);
                return false;
            }
            if (isLockSuccess) {
                return true;
            }
            if (lockWaitTime == 0 || System.currentTimeMillis() - before > lockWaitTime) {
                // ロック取得の待機時間が経過してもロックを取得できない場合、ロックファイルの強制削除を試みる
                if (!deleteLockFileExceedsLockWaitTime(lockFile, formattedMessage, context)) {
                    return false; // ロックファイルの強制削除ができなかった場合は処理を終了する
                }
            } else {
                // ロック取得できない場合、一定時間処理をスリープした後、再度ロック取得を試みる
                if (!waitLock(lockFile, formattedMessage, context)) {
                    return false; // もしスレッドの割り込みが発生した場合は処理を終了する
                }
            }
        }
    }


    /**
     * ロック待ち処理を行う。
     * <p/>
     * ロック取得の再試行間隔（ミリ秒）で設定された時間、スレッドをスリープさせる。
     * @param lockFile ロックファイル
     * @param formattedMessage フォーマット済みのログ
     * @param context ログエントリオブジェクト
     * @return もし割り込みが発生した場合にはfalse
     */
    protected boolean waitLock(File lockFile, String formattedMessage, LogContext context) {
        try {
            Thread.sleep(lockRetryInterval);
        } catch (InterruptedException e) {
            String lockFileAbsolutePath = lockFile.getAbsolutePath();
            // 割り込み発生時にはロック取得の再試行は行わず、強制的にログを出力し処理を終了する
            String failureMessage = getFormattingFailureMessage(
                    context
                  , Builder.concat("interrupted while waiting for lock retry.")
                  , failureCodeInterruptLockWait
                  , lockFileAbsolutePath);
            forceWrite(formattedMessage, context, failureMessage);
            return false;
        }
        return true;
    }

    /**
     * 待機時間を過ぎても残存しているロックファイルを強制的に削除する。
     * @param lockFile ロックファイル
     * @param formattedMessage フォーマット済みのログ
     * @param context ログエントリオブジェクト
     * @return ロックファイルの強制削除が正常に終了したかどうか
     */
    protected boolean deleteLockFileExceedsLockWaitTime(File lockFile, String formattedMessage, LogContext context) {
        // 不要なロックファイルが残存しているとみなし削除
        if (!lockFile.delete()) {
            String lockFileAbsolutePath = lockFile.getAbsolutePath();
            // ロックファイルの強制削除ができなかった場合、ロックの取得は不可能と判断し強制的にログを出力する
            String failureMessage = getFormattingFailureMessage(
                    context
                  , Builder.concat("failed to delete lock file forcedly. lock file was opened illegally. lock file path=[", lockFileAbsolutePath, "].")
                  , failureCodeForceDeleteLockFile
                  , lockFileAbsolutePath);
            forceWrite(formattedMessage, context, failureMessage);
            return false;
        }
        return true;
    }

    /**
     * ロック取得に失敗した場合に、強制的にログ出力を行う。
     * @param formattedMessage フォーマット済みのログ
     * @param context ログエントリオブジェクト
     * @param lockErrorMessage ロック取得に失敗した原因のログ
     */
    protected void forceWrite(String formattedMessage, LogContext context, String lockErrorMessage) {
        // フォーマット済みのログを強制的に出力する
        super.onWrite(formattedMessage);
        super.onWrite(lockErrorMessage);
    }

    /**
     * ログ出力後に、ロックを解放する。
     * <p/>
     * ロックの解放処理は、ロックファイルを削除することによって行う。
     * @param formattedMessage フォーマット済みのログ(本メソッドでは使用していない)
     * @param context ログエントリオブジェクト
     */
    protected void releaseLock(String formattedMessage, LogContext context) {
        if (!lockFile.delete()) {
            String lockFileAbsolutePath = lockFile.getAbsolutePath();
            String failureMessage = getFormattingFailureMessage(
                    context
                  , Builder.concat("failed to delete lock file. lock file path=[", lockFileAbsolutePath, "].")
                  , failureCodeReleaseLockFile
                  , lockFileAbsolutePath);
            super.onWrite(failureMessage);
        }
    }

    /**
     * 障害メッセージを取得する。
     * @param context ログエントリオブジェクト
     * @param defaultMessage デフォルトメッセージ
     * @param failureCode 障害コード
     * @param messageOptions 障害コードからメッセージを取得する際に使用するオプション情報
     * @return 障害メッセージ
     */
    private String getFormattingFailureMessage(LogContext context, String defaultMessage, String failureCode, Object... messageOptions) {
        String failureMessage;
        if (failureCode != null) {
            failureMessage = FailureLogUtil.getNotificationMessage(null, failureCode, messageOptions);
        } else {
            failureMessage = defaultMessage;
        }
        return getFormattingMessage(LogLevel.FATAL, context, failureMessage);
    }

    /**
     * デフォルトフォーマットのメッセージを取得する。
     * @param level ログレベル
     * @param context ログエントリオブジェクト
     * @param defaultMessage メッセージ
     * @return デフォルトフォーマットのメッセージ
     */
    private String getFormattingMessage(LogLevel level, LogContext context, String defaultMessage) {
        String message = getFormatter().format(
                new LogContext(
                        context.getLoggerName(), level, defaultMessage, null));
        return message;
    }

}
