package com.kp.hbase;


import com.kp.hbase.consumer.TwitterKafkaConsumer;
import com.kp.hbase.producer.TwitterKafkaProducer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	
        TwitterKafkaProducer producerKafka = new TwitterKafkaProducer();
        TwitterKafkaConsumer consumerKafka = new TwitterKafkaConsumer();
        producerKafka.run();
        consumerKafka.consumer();
    }
}
