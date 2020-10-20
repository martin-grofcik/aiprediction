package org.crp.aiflow.ml;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;

import java.util.Arrays;
import java.util.Properties;

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

    // Create DataSet representing the stream of input lines from kafka
    Dataset<String> lines = spark
            .readStream()
            .format("kafka")
            .option("kafka.bootstrap.servers", bootstrapServers)
            .option(subscribeType, topics)
            .load()
            .selectExpr("CAST(value AS STRING)")
            .as(Encoders.STRING());

    // Generate running word count
    Dataset<Row> wordCounts = lines.flatMap(
            (FlatMapFunction<String, String>) x -> Arrays.asList(x.split(" ")).iterator(),
            Encoders.STRING()).groupBy("value").count();

    // Start running the query that prints the running counts to the console
    StreamingQuery query = wordCounts.writeStream()
            .outputMode("complete")
            .format("kafka")
            .option("kafka.bootstrap.servers", bootstrapServers)
            .option("topic", "out")
            .option("checkpointLocation", "c:/tmp/spark/checkpoint")
            .start();

    query.awaitTermination();
  }

  private static KafkaProducer<String, Integer> createProducer(String brokers) {
    Properties props = new Properties();
    props.put("bootstrap.servers", brokers);
    props.put("acks", "all");
    props.put("retries", 0);
    props.put("batch.size", 16384);
    props.put("linger.ms", 1);
    props.put("buffer.memory", 33554432);
    props.put("key.serializer",
            "org.apache.kafka.common.serialization.StringSerializer");

    props.put("value.serializer",
            "org.apache.kafka.common.serialization.IntegerSerializer");
    return new KafkaProducer<>(props);
  }
}
