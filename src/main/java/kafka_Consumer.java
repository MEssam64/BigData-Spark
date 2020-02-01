
        import org.apache.kafka.clients.consumer.ConsumerConfig;

        import org.apache.kafka.clients.consumer.ConsumerRecord;

        import java.util.Arrays;
        import java.util.Collection;
        import java.util.HashMap;
        import java.util.Map;

        import org.apache.spark.SparkConf;
        import org.apache.spark.streaming.Durations;
        import org.apache.spark.streaming.api.java.JavaDStream;
        import org.apache.spark.streaming.api.java.JavaInputDStream;
        import org.apache.spark.streaming.api.java.JavaPairDStream;
        import org.apache.spark.streaming.api.java.JavaStreamingContext;
        import org.apache.spark.streaming.kafka010.ConsumerStrategies;
        import org.apache.spark.streaming.kafka010.KafkaUtils;
        import org.apache.spark.streaming.kafka010.LocationStrategies;
        import scala.Tuple2;



public class kafka_Consumer {

    public static void main(String[] args) throws InterruptedException {
        kafka_Consumer c = new kafka_Consumer();
        c.display();
    }


    public void display() throws InterruptedException{

        // Configure Spark to connect to Kafka running on local machine

        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG,"group1");
        kafkaParams.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
        kafkaParams.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,true);

        //Configure Spark to listen messages in topic test
        Collection<String> topics = Arrays.asList("final-lab-project");

        SparkConf conf = new SparkConf().setMaster("local[1]").setAppName("kafka_Consumer");

        //Read messages in batch of 50 seconds
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(50));

        // Start reading messages from Kafka and get DStream
        final JavaInputDStream<ConsumerRecord<String, String>> stream =
                KafkaUtils.createDirectStream(jssc, LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.<String,String>Subscribe(topics,kafkaParams));



        // Count occurance of each word


        //Print the word count
        JavaPairDStream<String, String> s = stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));
        JavaDStream<String> lines = s
                .map(
                        tuple2 -> tuple2._2()
                );
        JavaDStream<Long> count = lines.count();
        // Get the lines, split them into words, count the words and print


        lines.print();

        //System.out.println("Message received: " + lines);
        count.print();
        jssc.start();
        jssc.awaitTermination();

        jssc.start();
        jssc.awaitTermination();


    }

}


