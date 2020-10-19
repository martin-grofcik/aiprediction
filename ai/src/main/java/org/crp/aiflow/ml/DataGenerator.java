package org.crp.aiflow.ml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.StringJoiner;

public class DataGenerator {
    public static void main(String[] args) {
        try (FileOutputStream os = new FileOutputStream(new File("data/userTask.data"), false)) {
            for (int nationality = 0; nationality < 3; nationality++) {
                for (int age = 0; age < 5; age++) {
                    for (int income = 0; income < 4; income++) {
                        for (int requestedAmount = 0; requestedAmount < 2; requestedAmount++) {
                            StringJoiner stringJoiner = new StringJoiner(",");
                            stringJoiner
                                    .add(Double.toString(nationality))
                                    .add(Double.toString(age))
                                    .add(Double.toString(income))
                                    .add(Double.toString(requestedAmount))
                                    .add(getDecision(nationality, age, income, requestedAmount));
                            os.write(stringJoiner.toString().getBytes(Charset.defaultCharset()));
                            os.write("\n".getBytes(Charset.defaultCharset()));
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getDecision(int nationality, int age, int income, int requestedAmount) {
        return Boolean.toString(nationality < 2);
    }
}
