package net.amar.oreojava;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {

    private static final Logger logger = LoggerFactory.getLogger(Log.class);

    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    private static String getCaller() {
        return STACK_WALKER.walk((s) -> s.skip(2).findFirst())
                .map(StackWalker.StackFrame::getClassName)
                .orElse("Unknown");
    }

    public static void info(String msg) {
        logger.info("[{}] {}", getCaller(), msg);
    }

    public static void warn(String msg) {
        logger.warn("[{}] {}", getCaller(), msg);
    }

    public static void error(String msg) {
        logger.error("[{}] {}", getCaller(), msg);
    }

    public static void error(String msg, Throwable throwable) {
        logger.error("[{}] {}", getCaller(), msg, throwable);
    }
}
