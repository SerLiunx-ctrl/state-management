package com.serliunx.statemanagement.support;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 模板名称线程池
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2025/2/17
 */
public final class NamedThreadFactory implements ThreadFactory {

    private final AtomicInteger threadNumber = new AtomicInteger(0);

    private final String namePattern;

    public NamedThreadFactory(String namePattern) {
        this.namePattern = namePattern;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, String.format(namePattern, threadNumber.getAndIncrement()));
    }
}
