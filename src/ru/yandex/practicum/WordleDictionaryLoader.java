package ru.yandex.practicum;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {

    private final WordleDictionary words;

    public WordleDictionaryLoader(String path) throws DictionaryLoadException {
        try {
            List<String> rawList = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            List<String> normalized = new ArrayList<>();
            for (String s : rawList) {

                String trimmed = s.trim();
                if (!trimmed.isEmpty()) {
                    normalized.add(trimmed.replace('ё', 'е').toLowerCase(Locale.ROOT));
                }
            }
            if (normalized.isEmpty()) {
                throw new DictionaryLoadException("Словарь пуст");
            }
            words = new WordleDictionary(normalized);

        } catch (IOException e) {
            throw new DictionaryLoadException("Не удалось загрузить словарь", e);
        }
    }

    public WordleDictionary getWords() {
        return words;
    }

}
