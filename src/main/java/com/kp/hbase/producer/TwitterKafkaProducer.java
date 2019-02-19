package com.kp.hbase.producer;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kp.hbase.config.*;
import com.kp.hbase.model.Tweet;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterKafkaProducer {
	
	Tweet tweet = new Tweet();
	private String consumerKey;
	private String consumerSecret;
	private String accessToken;
	private String accessSecret;
	LinkedBlockingQueue<Tweet> queue = new LinkedBlockingQueue<Tweet>();
	
	public void run() throws Exception {
		
		System.out.print(" ");
		consumerKey    = TwitterConfiguration.consumerKey;
		consumerSecret = TwitterConfiguration.consumerSecret;
		accessToken    = TwitterConfiguration.accessKey;
		accessSecret   = TwitterConfiguration.accessSecret; 
		ConfigurationBuilder cb = new ConfigurationBuilder();
	      cb.setDebugEnabled(true)
	      
	         .setOAuthConsumerKey(consumerKey)
	         .setOAuthConsumerSecret(consumerSecret)
	         .setOAuthAccessToken(accessToken)
	         .setOAuthAccessTokenSecret(accessSecret).setJSONStoreEnabled(true);
	      	

	      TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
	      StatusListener listener = new StatusListener() {
	        
	         public void onStatus(Status status) {   
	        	String json = TwitterObjectFactory.getRawJSON(status);
	        	ObjectMapper mapper = new ObjectMapper();
	 			try {
					tweet = mapper.readValue(json, Tweet.class);
					queue.offer(tweet);
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	 			if(status.isRetweet()) {
	 				System.out.println("Retweet");
	 			} else {
	 				System.out.println("Post");
	 			}
	         }
	         
	         public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
	             System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
	         }
	         
	         public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
	             System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
	         }

	         public void onScrubGeo(long userId, long upToStatusId) {
	             System.out.println("Got scrub_geo event userId:" + userId + "upToStatusId:" + upToStatusId);
	         }      
	         
	         public void onStallWarning(StallWarning warning) {
	             System.out.println("Got stall warning:" + warning);
	         }
	         
	         public void onException(Exception ex) {
	            ex.printStackTrace();
	         }
	      };
	      twitterStream.addListener(listener);
	      twitterStream.sample();  
	      Thread.sleep(5000);
	     
	      //Add Kafka producer config settings
	      Properties props = new Properties();
	      props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfiguration.servers);
	      props.put(ProducerConfig.ACKS_CONFIG, "all");
	      props.put(ProducerConfig.RETRIES_CONFIG, 0);
	      props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
	      props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
	      props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
	      props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "com.kp.hbase.model.TwSerializer");
	      props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "com.kp.hbase.model.TwSerializer"); 
	      
	      Producer<String, Tweet> producer = new KafkaProducer<String, Tweet>(props);
	      int j = 0;
	      while(true) {
	         Tweet tw = queue.poll();
	         
	         if (tw == null) {
	        	 
	            Thread.sleep(100);
	         }else {
	   
	               producer.send(new ProducerRecord<String, Tweet>(KafkaConfiguration.topicName, Integer.toString(j++), tw));
	         }
	      }
	}
	
}
	
