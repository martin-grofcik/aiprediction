package org.crp.aiflow.ml;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;

import java.util.Arrays;

/**
 * Consumes messages from one or more topics in Kafka and does wordcount.
 * Usage: JavaStructuredKafkaWordCount <bootstrap-servers> <subscribe-type> <topics>
 *   <bootstrap-servers> The Kafka "bootstrap.servers" configuration. A
 *   comma-separated list of host:port.
 *   <subscribe-type> There are three kinds of type, i.e. 'assign', 'subscribe',
 *   'subscribePattern'.
 *   |- <assign> Specific TopicPartitions to consume. Json string
 *   |  {"topicA":[0,1],"topicB":[2,4]}.
 *   |- <subscribe> The topic list to subscribe. A comma-separated list of
 *   |  topics.
 *   |- <subscribePattern> The pattern used to subscribe to topic(s).
 *   |  Java regex string.
 *   |- Only one of "assign, "subscribe" or "subscribePattern" options can be
 *   |  specified for Kafka source.
 *   <topics> Different value format depends on the value of 'subscribe-type'.
 *
 * Example:
 *    `$ bin/run-example \
 *      sql.streaming.JavaStructuredKafkaWordCount host1:port1,host2:port2 \
 *      subscribe topic1,topic2 outTopic`
 */
public final class JavaStructuredKafkaWordCount {

  public static void main(String[] args) throws Exception {
    if (args.length < 4) {
      System.err.println("Usage: JavaStructuredKafkaWordCount <bootstrap-servers> " +
              "<subscribe-type> <topics> <out-topics>");
      System.exit(1);
    }

    String bootstrapServers = args[0];
    String subscribeType = args[1];
    String topics = args[2];
    String outTopics = args[3];

    SparkSession spark = SparkSession
            .builder()
            .appName("JavaStructuredKafkaWordCount")
            .getOrCreate();
    spark.sparkContext().setLogLevel("ERROR");

    LogisticRegressionModel sameModel = LogisticRegressionModel.load(spark.sparkContext(), "model-89\\logistic-regression");

    // Create DataSet representing the stream of input lines from kafka
    Dataset<String> lines = spark
            .readStream()
            .format("kafka")
            .option("kafka.bootstrap.servers", bootstrapServers)
            .option(subscribeType, topics)
            .load()
            .selectExpr("CAST(value AS STRING)")
            .as(Encoders.STRING());

    lines.map((MapFunction<String, String>) s -> {
      double amount = getAmount(s);
      double currency =  getCurrency(s);
      Vector newData = Vectors.dense(new double[]{amount,currency});
      double prediction = sameModel.predict(newData);
      return "{ \"approval\" : " + (prediction != 0.0) + "}";
    }, Encoders.STRING()).writeStream()
            .outputMode("append")
            .format("kafka")
            .option("kafka.bootstrap.servers", bootstrapServers)
            .option("topic", "out")
            .option("checkpointLocation", "c:/tmp/spark/checkpoint")
            .start()
            .awaitTermination();
  }

  private static double getAmount(String s) {
    return Double.parseDouble(
            Arrays.stream(s.split(",")).filter(subString -> subString.contains("amount")).findFirst().
            map( amountString -> amountString.substring( amountString.indexOf(":")+1)).orElse("0.0")
    );
  }
  private static double getCurrency(String s) {
    return Arrays.stream(s.split(",")).filter(subString -> subString.contains("currency")).findFirst()
            .map( amountString -> amountString.substring( amountString.indexOf(":")+3, amountString.length() -3))
            .map( currency -> "EUR".equals(currency) ? 0.0 : ("USD".equals(currency) ? 1.0 : ("CHF".equals(currency) ? 2.0 : 3.0)))
            .orElse(3.0);
  }

}
