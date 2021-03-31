package ru.hse.java.streams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.sqrt;

public final class SecondPartTasks {

    private SecondPartTasks() {
    }

    // Найти строки из переданных файлов, в которых встречается указанная подстрока.
    public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
        return paths
                .stream()
                .flatMap(file -> {
                    try {
                        return Files.lines(Paths.get(file));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .filter(line -> line.contains(sequence))
                .collect(Collectors.toList());

    }

    // В квадрат с длиной стороны 1 вписана мишень.
    // Стрелок атакует мишень и каждый раз попадает в произвольную точку квадрата.
    // Надо промоделировать этот процесс с помощью класса java.util.Random и посчитать, какова вероятность попасть в мишень.
    public static double piDividedBy4() {
        return piDividedBy4(10000000);
    }

    public static double piDividedBy4(int n) {
        return IntStream.generate(new IntSupplier() {
            private final Random rand = new Random();

            @Override
            public int getAsInt() {
                double r = 0.5;
                double x = rand.nextDouble() - r;
                double y = rand.nextDouble() - r;
                if (sqrt(x * x + y * y) < r) {
                    return 1;
                }
                return 0;
            }
        })
                .limit(n)
                .average()
                .orElse(0);
    }

    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(Map<String, List<String>> compositions) {
        if (compositions.isEmpty()) {
            return null;
        }
        return compositions
                .entrySet()
                .stream()
                .max(Comparator.comparing(str -> str
                        .getValue()
                        .stream()
                        .mapToLong(String::length)
                        .sum()
                ))
                .get()
                .getKey();
    }

    // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.
    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        return orders
                .stream()
                .flatMap(order -> order
                        .entrySet()
                        .stream()
                )
                .collect(
                        Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.summingInt(Map.Entry::getValue)
                        )
                );
    }
}