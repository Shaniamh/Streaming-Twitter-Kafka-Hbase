package com.kp.hbase.config;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseConfiguration {
	private final static String HMASTER = "namenode004.cluster02.bt";
	private final static String ZOOKEEPER = "master001.cluster02.bt,namenode004.cluster02.bt,namenode005.cluster02.bt";
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Configuration config;
	
	public void config() {
		 config = HBaseConfiguration.create();
		    config.set("hbase.master", HMASTER);
		    config.setInt("timeout", 5000);
		    config.set("hbase.zookeeper.quorum", ZOOKEEPER);
		    config.set("zookeeper.znode.parent", "/hbase-unsecure2");
	}
	
	public void putRowValueBatch(String tableName, String rowKey, String familyColumn, String columnValues) {
		
		logger.info("begin to update table:" + tableName + ",rowKey:" + rowKey + ",family:" + familyColumn + ",columnValues:" + columnValues.toString());
		try (Connection connection = ConnectionFactory.createConnection(config);
		     Table table = connection.getTable(TableName.valueOf(tableName))) {
			Put put = new Put(Bytes.toBytes(rowKey));
				put.addColumn(Bytes.toBytes(familyColumn), Bytes.toBytes("data_idpost"), Bytes.toBytes(columnValues));
			table.put(put);
			logger.info("update table:" + tableName + ",rowKey:" + rowKey + " successfully!");
			System.out.println("update table:" + tableName + ",rowKey:" + rowKey + " successfully!");

		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public List<Cell> scanRegexRowKey(String tableName, String regexKey) {
		config();
		try (Connection connection = ConnectionFactory.createConnection(config);
		     Table table = connection.getTable(TableName.valueOf(tableName))) {
			Scan scan = new Scan();
			Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(regexKey));
			scan.setFilter(filter);
			ResultScanner rs = table.getScanner(scan);
			for (Result r : rs) {
				return r.listCells();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return null;
	}
}
