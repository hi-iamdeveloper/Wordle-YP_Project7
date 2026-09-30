package ru.yandex.practicum;

public class WordNotFoundInDictionary extends Exception {
    private final String word;

    public WordNotFoundInDictionary(String word) {
        super("Слово не найдено в словаре: " + word);
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}