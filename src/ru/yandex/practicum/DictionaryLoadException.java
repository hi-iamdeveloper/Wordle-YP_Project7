package ru.yandex.practicum;

import java.io.IOException;

public class DictionaryLoadException extends Exception {
    public DictionaryLoadException() {
    }

    public DictionaryLoadException(String message) {
        super(message);
    }

    public DictionaryLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public DictionaryLoadException(Throwable cause) {
        super(cause);
    }

    public DictionaryLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
