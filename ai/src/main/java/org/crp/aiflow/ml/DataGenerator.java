package org.crp.aiflow.ml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.StringJoiner;

public class DataGenerator {
    public static void main(String[] args) {
        try (FileOutputStream os = new FileOutputStream(new File("data/userTask.data"), false)) {
            for (double amount = 1; amount < 50; amount+=0.01) {
                for (double currency = 0; currency < 3; currency+=1.0) {
                            StringJoiner stringJoiner = new StringJoiner(",");
                            stringJoiner
                                    .add(Double.toString(amount))
                                    .add(Double.toString(currency))
                                    .add(getDecision(amount, currency));
                            os.write(stringJoiner.toString().getBytes(Charset.defaultCharset()));
                            os.write("\n".getBytes(Charset.defaultCharset()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getDecision(double amount, double currency) {
        if (currency == 0.0 && amount < 10) {
            return Boolean.TRUE.toString();
        }
        if (currency == 1.0 && amount < 20) {
            return Boolean.TRUE.toString();
        }
        if (currency == 2.0 && amount < 30) {
            return Boolean.TRUE.toString();
        }
        return Boolean.FALSE.toString();
    }
}
