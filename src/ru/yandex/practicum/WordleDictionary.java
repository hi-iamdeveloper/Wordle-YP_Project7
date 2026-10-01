package ru.yandex.practicum;

import java.util.List;
import java.util.Random;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private final List<String> words;
    private final Random random = new Random();

    public WordleDictionary(List<String> words) {
        this.words = words;
    }

    String getRandomWord() {

        if (words.isEmpty()) {
            throw new IllegalStateException("Словарь пуст");
        }
        int index = random.nextInt(words.size());
        return words.get(index);
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public List<String> getWords() {
        return words;
    }

}
