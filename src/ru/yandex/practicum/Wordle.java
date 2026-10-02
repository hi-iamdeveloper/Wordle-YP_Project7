package ru.yandex.practicum;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
    1
 */
public class Wordle {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (PrintWriter log = new PrintWriter("wordle.log", StandardCharsets.UTF_8)) {
            runApplication(scanner, log);
        } catch (IOException e) {
            System.out.println("Не удалось создать лог-файл: " + e.getMessage());
        }
    }

    private static void runApplication(Scanner scanner, PrintWriter log) {
        WordleDictionary dictionary;
        try {
            WordleDictionaryLoader loader = new WordleDictionaryLoader("words_ru.txt");
            dictionary = loader.getWords();
            log.println("Словарь инициализирован");
        } catch (DictionaryLoadException e) {
            log.println(e.getMessage());
            return;
        }

        System.out.println("Добро пожаловать в игру Wordle!");
        runMenuLoop(scanner, dictionary, log);
    }

    private static void runMenuLoop(Scanner scanner, WordleDictionary dictionary, PrintWriter log) {
        while (true) {
            int userAnswer = printMenu(scanner);

            switch (userAnswer) {
                case 1:
                    playGame(scanner, dictionary, log);
                    break;
                case 2:
                    return;
            }
        }
    }

    private static void playGame(Scanner scanner, WordleDictionary dictionary, PrintWriter log) {
        WordleGame game = new WordleGame(dictionary, log);

        while (!game.isOver()) {
            handleMove(scanner, game);
        }

        printGameResult(game);
    }

    private static void handleMove(Scanner scanner, WordleGame game) {
        System.out.println("Введите слово (или 'подсказка' для подсказки):");
        String input = readInput(scanner);

        if (input.isEmpty()) {
            System.out.println("Пустой ввод, попробуйте снова.");
            return;
        }

        if (input.equals("подсказка")) {
            printHint(game);
            return;
        }

        int expectedLength = game.getAnswer().length();
        if (input.length() != expectedLength) {
            System.out.println("Слово должно быть из " + expectedLength + " букв.");
            return;
        }

        try {
            WordleGame.MoveResult result = game.makeMove(input);
            System.out.println(renderMoveResult(input, result));
            System.out.println("Быков: " + result.getBulls()
                    + ", коров: " + result.getCows()
                    + ", осталось попыток: " + game.getSteps());
        } catch (WordNotFoundInDictionary e) {
            System.out.println("Такого слова нет в словаре: " + e.getWord());
        } catch (WordAlreadyUsedException e) {
            System.out.println("Слово уже использовалось: " + e.getWord());
        }
    }

    private static String readInput(Scanner scanner) {
        return scanner.nextLine().trim().toLowerCase(Locale.ROOT).replace('ё', 'е');
    }

    private static void printHint(WordleGame game) {
        String hint = game.getHint();
        if (hint != null) {
            System.out.println("Подсказка: " + hint);
        } else {
            System.out.println("Подсказок больше нет.");
        }
    }

    private static String renderMoveResult(String input, WordleGame.MoveResult result) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (result.getStatus(i)) {
                case BULL:
                    sb.append('[').append(Character.toUpperCase(c)).append(']');
                    break;
                case COW:
                    sb.append('(').append(c).append(')');
                    break;
                case ABSENT:
                    sb.append(' ').append(c).append(' ');
                    break;
            }
            sb.append(' ');
        }
        return sb.toString();
    }

    private static void printGameResult(WordleGame game) {
        if (game.isWin()) {
            System.out.println("Победа! Слово: " + game.getAnswer());
        } else {
            System.out.println("Попытки кончились. Было загадано: " + game.getAnswer());
        }
    }

    static int printMenu(Scanner scanner) {
        System.out.println("Выберите, что хотите сделать:");
        System.out.println("1 - начать игру");
        System.out.println("2 - выйти из приложения");

        while (true) {
            String input = scanner.nextLine();
            try {
                int answer = Integer.parseInt(input);
                if (answer == 1 || answer == 2) {
                    return answer;
                } else {
                    System.out.println("Неверное число!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число!");
            }
        }
    }
}