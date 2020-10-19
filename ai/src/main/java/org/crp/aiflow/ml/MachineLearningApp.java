package org.crp.aiflow.ml;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.stat.MultivariateStatisticalSummary;
import org.apache.spark.mllib.stat.Statistics;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;

public class MachineLearningApp {

    public static void main(String[] args) {

        // 1. Setting the Spark Context
        SparkConf conf = new SparkConf().setAppName("Main")
            .setMaster("local[2]")
            .set("spark.executor.memory", "3g")
            .set("spark.driver.memory", "3g");
        JavaSparkContext sc = new JavaSparkContext(conf);
        Logger.getLogger("org")
            .setLevel(Level.OFF);
        Logger.getLogger("akka")
            .setLevel(Level.OFF);

        // 2. Loading the Data-set
        String dataFile = "data\\userTask.data";
        JavaRDD<String> data = sc.textFile(dataFile);

        // 3. Exploratory Data Analysis
        // 3.1. Creating Vector of Input Data
        JavaRDD<Vector> inputData = data.map(line -> {
            String[] parts = line.split(",");
            double[] v = new double[parts.length - 1];
            for (int i = 0; i < parts.length - 1; i++) {
                v[i] = Double.parseDouble(parts[i]);
            }
            return Vectors.dense(v);
        });
        // 3.2. Performing Statistical Analysis
        MultivariateStatisticalSummary summary = Statistics.colStats(inputData.rdd());
        System.out.println("Summary Mean:");
        System.out.println(summary.mean());
        System.out.println("Summary Variance:");
        System.out.println(summary.variance());
        System.out.println("Summary Non-zero:");
        System.out.println(summary.numNonzeros());
        // 3.3. Performing Correlation Analysis
        Matrix correlMatrix = Statistics.corr(inputData.rdd(), "pearson");
        System.out.println("Correlation Matrix:");
        System.out.println(correlMatrix.toString());

        // 4. Data Preparation
        // 4.1. Creating Map for Textual Output Labels
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("true", 1);
        map.put("false", 0);
        // 4.2. Creating LabeledPoint of Input and Output Data
        JavaRDD<LabeledPoint> parsedData = data.map(line -> {
            String[] parts = line.split(",");
            double[] v = new double[parts.length - 1];
            for (int i = 0; i < parts.length - 1; i++) {
                v[i] = Double.parseDouble(parts[i]);
            }
            return new LabeledPoint(map.get(parts[parts.length - 1]), Vectors.dense(v));
        });

        // 5. Data Splitting into 80% Training and 20% Test Sets
        JavaRDD<LabeledPoint>[] splits = parsedData.randomSplit(new double[] { 0.8, 0.2 }, 11L);
        JavaRDD<LabeledPoint> trainingData = splits[0].cache();
        JavaRDD<LabeledPoint> testData = splits[1];

        // 6. Modeling
        // 6.1. Model Training
        LogisticRegressionModel model = new LogisticRegressionWithLBFGS().setNumClasses(5)
            .run(trainingData.rdd());
        // 6.2. Model Evaluation
        JavaPairRDD<Object, Object> predictionAndLabels = testData.mapToPair(p -> new Tuple2<>(model.predict(p.features()), p.label()));
        MulticlassMetrics metrics = new MulticlassMetrics(predictionAndLabels.rdd());
        double accuracy = metrics.accuracy();
        System.out.println("Model Accuracy on Test Data: " + accuracy);

        // 7. Model Saving and Loading
        // 7.1. Model Saving
        model.save(sc.sc(), "model\\logistic-regression");
        // 7.2. Model Loading
        LogisticRegressionModel sameModel = LogisticRegressionModel.load(sc.sc(), "model\\logistic-regression");
        // 7.3. Prediction on New Data
        predict(sameModel, new double[]{0.0,1.0,3.0,0.0});
        predict(sameModel, new double[]{1.0,0.0,3.0,0.0});
        predict(sameModel, new double[]{2.0,2.0,2.0,1.0});
        predict(sameModel, new double[]{2.0,4.0,2.0,1.0});
        predict(sameModel, new double[]{2.0,2.0,3.0,0.0});

        // 8. Clean-up
        sc.close();
    }

    private static void predict(LogisticRegressionModel sameModel, double[] values) {
        Vector newData = Vectors.dense(values);
        double prediction = sameModel.predict(newData);
        System.out.println("Model Prediction on New Data = " + prediction);
    }

}
