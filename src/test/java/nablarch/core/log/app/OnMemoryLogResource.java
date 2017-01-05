package nablarch.core.log.app;

import org.junit.rules.ExternalResource;

/**
 * メモリログの管理を行うクラス。
 *
 * @author T.Kawasaki
 */
public class OnMemoryLogResource extends ExternalResource {

    @Override
    protected void before() throws Throwable {
        OnMemoryLogWriter.clear();
    }

    @Override
    protected void after() {
        OnMemoryLogWriter.clear();
    }
}
