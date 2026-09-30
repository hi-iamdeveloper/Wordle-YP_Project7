package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

public class WordleGame {

    private static final int MAX_STEPS = 6;

    private final WordleDictionary dictionary;
    private final PrintWriter log;
    private final String answer;
    private final Random random = new Random();

    private int steps;
    private boolean won;
    private final Set<String> usedWords = new HashSet<>();
    private final Set<String> usedHints = new HashSet<>();

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this(dictionary.getRandomWord(), dictionary, log);
    }

    public WordleGame(String answer, WordleDictionary dictionary, PrintWriter log) {
        this.answer = answer;
        this.dictionary = dictionary;
        this.log = log;
        this.steps = MAX_STEPS;
        this.won = false;
        log.println("Игра создана. Загадано слово из " + answer.length() + " букв");
    }

    public MoveResult makeMove(String word) throws WordNotFoundInDictionary {
        if (word.length() != answer.length()) {
            throw new IllegalStateException(
                    "Длина слова не совпадает: " + word.length() + " vs " + answer.length());
        }
        if (isOver()) {
            throw new IllegalStateException("Игра уже завершена");
        }
        if (!dictionary.contains(word)) {
            throw new WordNotFoundInDictionary(word);
        }
        if (usedWords.contains(word)) {
            throw new IllegalStateException("Слово уже использовалось: " + word);
        }

        usedWords.add(word);
        MoveResult result = check(answer, word);

        if (result.getBulls() == answer.length()) {
            won = true;
            log.println("Игрок угадал слово: " + word);
        } else {
            steps--;
            log.println("Ход: " + word
                    + ", быков: " + result.getBulls()
                    + ", коров: " + result.getCows()
                    + ", осталось попыток: " + steps);
        }

        return result;
    }

    public boolean isOver() {
        return won || steps <= 0;
    }

    public boolean isWin() {
        return won;
    }

    public int getSteps() {
        return steps;
    }

    public String getAnswer() {
        return answer;
    }

    public String getHint() {
        List<String> candidates = new ArrayList<>();

        for (String word : dictionary.getWords()) {
            if (!usedWords.contains(word) && !usedHints.contains(word)) {
                candidates.add(word);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        int index = random.nextInt(candidates.size());
        String hint = candidates.get(index);
        usedHints.add(hint);
        log.println("Выдана подсказка: " + hint);
        return hint;
    }

    private MoveResult check(String secret, String guess) {
        if (secret.length() != guess.length()) {
            throw new IllegalStateException(
                    "Длины слов не совпадают: " + secret.length() + " vs " + guess.length());
        }

        char[] s = secret.toCharArray();
        char[] g = guess.toCharArray();
        LetterStatus[] statuses = new LetterStatus[g.length];

        for (int i = 0; i < statuses.length; i++) {
            statuses[i] = LetterStatus.ABSENT;
        }

        int bulls = 0;
        int cows = 0;

        // 1. Быки
        for (int i = 0; i < s.length; i++) {
            if (s[i] == g[i]) {
                bulls++;
                statuses[i] = LetterStatus.BULL;
                s[i] = '#';
                g[i] = '#';
            }
        }

        // 2. Коровы
        for (int i = 0; i < g.length; i++) {
            if (g[i] == '#') continue;
            for (int j = 0; j < s.length; j++) {
                if (s[j] == g[i]) {
                    cows++;
                    statuses[i] = LetterStatus.COW;
                    s[j] = '#';
                    break;
                }
            }
        }

        return new MoveResult(bulls, cows, statuses);
    }

    public enum LetterStatus {
        BULL,    // на своём месте
        COW,     // есть, но не там
        ABSENT   // нет в слове
    }

    public static class MoveResult {
        private final int bulls;
        private final int cows;
        private final LetterStatus[] statuses;

        public MoveResult(int bulls, int cows, LetterStatus[] statuses) {
            this.bulls = bulls;
            this.cows = cows;
            this.statuses = statuses;
        }

        public int getBulls() { return bulls; }
        public int getCows()  { return cows; }
        public LetterStatus getStatus(int i) { return statuses[i]; }
    }
}