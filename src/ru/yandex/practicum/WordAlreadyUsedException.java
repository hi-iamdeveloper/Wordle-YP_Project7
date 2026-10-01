package ru.yandex.practicum;

public class WordAlreadyUsedException extends Exception {
    private final String word;

    public WordAlreadyUsedException(String word) {
        super("Слово уже использовалось: " + word);
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}