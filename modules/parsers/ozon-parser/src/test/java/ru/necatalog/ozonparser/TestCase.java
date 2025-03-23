package ru.necatalog.ozonparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class TestCase {

    @Test
    void test() {
        String input = "NVIDIA GeForce RTX 4050 для ноутбуков (6     тб)".replaceAll("[()]", " ");
        String regex = "\\s(ГБ|GB|TB|ТБ|MB|МБ|ГЦ)";
        Matcher matcher = Pattern.compile(regex).matcher(input.toUpperCase());
        System.out.println(matcher.find() ? matcher.group() : null);
    }


}
