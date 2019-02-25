package com.kp.hbase.consumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.alibaba.fastjson.JSON;
import com.kp.hbase.config.HbaseConfiguration;
import com.kp.hbase.config.KafkaConfiguration;
import com.kp.hbase.model.Tweet;

public class TwitterKafkaConsumer {
	public void consumer() {
	
	      //Kafka consumer configuration settings
	      String topicName = KafkaConfiguration.topicName;
	      Properties props = new Properties();
	      
	      props.put("bootstrap.servers", KafkaConfiguration.servers);
	      props.put("group.id", "test");
	      props.put("enable.auto.commit", "true");
	      props.put("auto.commit.interval.ms", "1000");
	      props.put("session.timeout.ms", "30000");
	      props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	      props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	      KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
	      
	      //Kafka Consumer subscribes list of topics here.
	      consumer.subscribe(Arrays.asList(topicName));
	      
	      //print the topic name
	      System.out.println("Subscribed to topic " + topicName);
	      int i = 0;
	      
	         ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(60));
	         System.out.println("pppppppppppppppppppppppp");
	         while(true) {
	         for (ConsumerRecord<String, String> record : records) {
	        	 if(!records.isEmpty()) {
	        	Tweet tw = JSON.parseObject(record.value(), Tweet.class);
	        //	  System.out.println("llllllllllllllaaaaaaaaaaaaa");
	         // print the offset,key and value for the consumer records.
	         //System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
	         updateOrInsert(tw, record.value());
	        	 }else {
	        	//	 System.out.println("dfdd=============");
	        	 }
	         }
	       //  System.out.println("--------------");
	      }
	         
	}
	
	public void updateOrInsert(Tweet tw, String value) {
		HbaseConfiguration hbase = new HbaseConfiguration() ;
		String rowKeyRegex = String.valueOf("kp_"+ tw.getId());
		List<Cell> result = hbase.scanRegexRowKey("test-kp", rowKeyRegex);
		String rowKey ;
		if (result != null) {
			Cell cell = result.get(0);
			rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
		} else {
			rowKey = String.valueOf("kp_"+ tw.getId());
		}
		hbase.putRowValueBatch("test-kp", rowKey, "0", value);
	}
}
