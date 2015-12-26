package master;

import java.io.IOException;
import java.io.InterruptedIOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.util.Bytes;

public class RankTable {
	
	public static final byte[] TABLE = Bytes.toBytes("RANK");
	public static final byte[] CF_HASHTAGS = Bytes.toBytes("HASHTAGS");
	public static final byte[] CF_COUNTS = Bytes.toBytes("COUNTS");
	public static final byte[] C_HASHTAGS_1 = Bytes.toBytes("HASHTAGS_1");
	public static final byte[] C_HASHTAGS_2 = Bytes.toBytes("HASHTAGS_2");
	public static final byte[] C_HASHTAGS_3 = Bytes.toBytes("HASHTAGS_3");
	public static final byte[] C_COUNTS_1 = Bytes.toBytes("COUNTS_1");
	public static final byte[] C_COUNTS_2 = Bytes.toBytes("COUNTS_2");
	public static final byte[] C_COUNTS_3 = Bytes.toBytes("COUNTS_3");
	
	private static HTable table;
	private static HConnection conn;
	
	public static void open() throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
		
		Configuration conf = HBaseConfiguration.create();
		conn = HConnectionManager.createConnection(conf);
		table = new HTable(TableName.valueOf(TABLE),conn);
		
	}
	
	public static void createTable() {
		
		try {
			
			Configuration conf = HBaseConfiguration.create();
			HBaseAdmin admin = new HBaseAdmin(conf);
			
			HTableDescriptor table = new HTableDescriptor(TableName.valueOf(TABLE));
			
			HColumnDescriptor family;
			
			family = new HColumnDescriptor(CF_HASHTAGS);
			table.addFamily(family);
			
			family = new HColumnDescriptor(CF_COUNTS);
			table.addFamily(family);
			
			admin.createTable(table);
			admin.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void deleteTable() {
		
		try {
			
			Configuration conf = HBaseConfiguration.create();
			HBaseAdmin admin = new HBaseAdmin(conf);
			
			if(admin.tableExists(TableName.valueOf(TABLE))) {
				admin.disableTable(TableName.valueOf(TABLE));
				admin.deleteTable(TableName.valueOf(TABLE));
			}
			
			admin.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void addEntry(byte[] key, String h1, int c1, String h2, int c2, String h3, int c3) {
		Put put = new Put(key);
		
		put.add(CF_HASHTAGS, C_HASHTAGS_1, Bytes.toBytes(h1));
		put.add(CF_COUNTS, C_COUNTS_1, Bytes.toBytes(c1));
		
		if(!h2.equals("null")) {
			put.add(CF_HASHTAGS, C_HASHTAGS_2, Bytes.toBytes(h2));
			put.add(CF_COUNTS, C_COUNTS_2, Bytes.toBytes(c2));
		}
		
		if(!h3.equals("null")) {
			put.add(CF_HASHTAGS, C_HASHTAGS_3, Bytes.toBytes(h3));
			put.add(CF_COUNTS, C_COUNTS_3, Bytes.toBytes(c3));
		}
		
		try {
			table.put(put);
		} catch (RetriesExhaustedWithDetailsException | InterruptedIOException e) {
			e.printStackTrace();
		}
	}
	
	public static void close() {
		
		try {
			table.close();
			conn.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			table = null;
			conn = null;
		}
		
	}

}
