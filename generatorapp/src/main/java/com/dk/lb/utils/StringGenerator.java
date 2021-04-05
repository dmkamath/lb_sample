package com.dk.lb.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.List;

public class StringGenerator {
    private static String RESOURCE_LOCATION = "C:\\gitrepos\\web_resources\\";
    private static String FIRST_NAME_OPEN_SOURCE = "first_name_open_source.txt";
    private static String LAST_NAME_CENSUS = "census_last_names_clean.txt";
    private static String ENGLISH_DICTIONARY = "engmix_clean.txt";

    private List<String> firstNames = null;
    private List<String> lastNames = null;
    private List<String> englishWords = null;

    public StringGenerator() {
        initialize();
    }

    private void initialize() {
        String value = "";
        try {
            Path firstNamesPath = Paths.get(RESOURCE_LOCATION + FIRST_NAME_OPEN_SOURCE);
            firstNames = Files.readAllLines(firstNamesPath);
            //System.out.println("number of first names read "+firstNames.size());

            Path lastNamesPath = Paths.get(RESOURCE_LOCATION + LAST_NAME_CENSUS);
            lastNames = Files.readAllLines(lastNamesPath);
            //System.out.println("number of last names read "+lastNames.size());

            Path englishWordsPath = Paths.get(RESOURCE_LOCATION + ENGLISH_DICTIONARY);
            englishWords = Files.readAllLines(englishWordsPath);
            //System.out.println("number of english words read " + englishWords.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String generate(int length, boolean digitsOnly) {
        String value = "";
        SecureRandom random = new SecureRandom();
        long lvalue = Math.abs(random.nextLong());
        String formatString = "%0" + length + "d";
        String formatted = String.format(formatString, lvalue);
        //System.out.println("formatted value:"+formatted);
        value = formatted.length() > length ? formatted.substring(0, length) : formatted;
        //System.out.println("return value:"+value);

        return value;
    }

    public long generateLong(int length) {
        String value = generate(length, true);
        return Long.parseLong(value);
    }

    public String generatePersonName() {
        SecureRandom sr = new SecureRandom();
        int fnRandom = sr.nextInt(firstNames.size());
        //System.out.println("got random number "+fnRandom + ", so first name is " + firstNames.get(fnRandom).toUpperCase());

        int lnRandom = sr.nextInt(lastNames.size());
        //System.out.println("got random number "+lnRandom + ", so last name is " + lastNames.get(lnRandom));

        return firstNames.get(fnRandom).toUpperCase() + " " + lastNames.get(lnRandom);
    }

    public String generateCompanyName() {
        SecureRandom sr = new SecureRandom();
        int fnRandom = sr.nextInt(englishWords.size());
        //System.out.println("got random number " + fnRandom + ", so first word is " + englishWords.get(fnRandom).toUpperCase());

        int lnRandom = sr.nextInt(englishWords.size());
        //System.out.println("got random number " + lnRandom + ", so last word is " + englishWords.get(lnRandom).toUpperCase());

        String companyName = englishWords.get(fnRandom).toUpperCase() + " " +
                englishWords.get(lnRandom).toUpperCase() + " INC.";
        //System.out.println("returning companyName "+companyName);

        return companyName;
    }

    public String genarateSimpleEnglishWord() {
        SecureRandom sr = new SecureRandom();
        int fnRandom = sr.nextInt(englishWords.size());
        //System.out.println("got random number " + fnRandom + ", so returning word " + englishWords.get(fnRandom).toLowerCase());
        return englishWords.get(fnRandom).toLowerCase();
    }
}
