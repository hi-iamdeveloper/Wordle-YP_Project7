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
 */
public class Wordle {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try (PrintWriter log = new PrintWriter("wordle.log", StandardCharsets.UTF_8)) {
            try {
                WordleDictionaryLoader loader = new WordleDictionaryLoader("words_ru.txt");
                WordleDictionary dictionary = loader.getWords();
                log.println("Словарь инициализирован");

                System.out.println("Добро пожаловать в игру Wordle!");

                while (true) {

                    int userAnswer = printMenu(scanner);

                    switch (userAnswer) {
                        case 1:
                            WordleGame game = new WordleGame(dictionary, log);

                            while (!game.isOver()) {
                                System.out.println("Введите слово (или 'подсказка' для подсказки):");
                                String input = scanner.nextLine().trim().toLowerCase(Locale.ROOT).replace('ё', 'е');

                                if (input.isEmpty()) {
                                    System.out.println("Пустой ввод, попробуйте снова.");
                                    continue;
                                }

                                if (input.equals("подсказка")) {
                                    String hint = game.getHint();
                                    if (hint != null) {
                                        System.out.println("Подсказка: " + hint);
                                    } else {
                                        System.out.println("Подсказок больше нет.");
                                    }
                                    continue;
                                }

                                if (input.length() != game.getAnswer().length()) {
                                    System.out.println("Слово должно быть из " + game.getAnswer().length() + " букв.");
                                    continue;
                                }

                                try {
                                    WordleGame.MoveResult result = game.makeMove(input);

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
                                    System.out.println(sb);
                                    System.out.println("Быков: " + result.getBulls()
                                            + ", коров: " + result.getCows()
                                            + ", осталось попыток: " + game.getSteps());

                                } catch (WordNotFoundInDictionary e) {
                                    System.out.println("Такого слова нет в словаре: " + e.getWord());
                                }
                            }

                            if (game.isWin()) {
                                System.out.println("Победа! Слово: " + game.getAnswer());
                            } else {
                                System.out.println("Попытки кончились. Было загадано: " + game.getAnswer());
                            }
                            break;
                        case 2:
                            return;
                    }
                }

            } catch (DictionaryLoadException e) {
                log.println(e.getMessage());
                return;
            }
        } catch (IOException e) {
            System.out.println("Не удалось создать лог-файл: " + e.getMessage());
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
                continue;
            }
        }


    }

}
