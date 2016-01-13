package master.modes;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import master.FileLog;
import master.KeyGenerator;
import master.RankTable;
import master.structures.HashtagRank;
import master.structures.HashtagRankEntry;

public class Mode3 {
	
	/**
	 * Do find the Top-N most used words and the frequency of each word regardless 
	 * the language in a time interval defined with the provided start and end timestamp.
	 * Start and end timestamp are in milliseconds.
	 * @param args mode startTS endTS N outputFolder
	 */
	public static void execute(String[] args) {
		if(args.length != 5) {
			System.err.println("Invalid arguments for mode 1");
			return;
		}
		
		Long startTs = Long.parseLong(args[1]);
		Long endTs = Long.parseLong(args[2]);
		int rankSize = Integer.parseInt(args[3]);
		String logPath = (args[4].endsWith(File.separator) ? args[4] : args[4] + File.separator);
		
		// Open Rank HBase table
		HTable table;
		try {
			table = RankTable.open();
		} catch (IOException e) {
			System.err.println("Could not open HBase Rank table!");
			e.printStackTrace();
			return;
		}

		// Calculate start and end keys
		byte[] startKey = KeyGenerator.generateKey(startTs);
		byte[] endKey = KeyGenerator.generateEndKey(endTs);

		Scan scan = new Scan(startKey, endKey);
		
		try {
			ResultScanner rs = table.getScanner(scan);
			
			// Create rank object
			HashtagRank rank = new HashtagRank();
			
			Result res = rs.next();
			while (res != null && !res.isEmpty()) {
				
				// Process each result adding its containing entries to the rank
				addEntryToRankFromResult(rank, res);
				
				// Next result
				res = rs.next();
			}
			
			// Log results
			// Regardless the language? Do we have to log language according to
			// the exercise format?
			FileLog logger = new FileLog(logPath + "02_query3.out");
			logger.writeToFile(rank.getBestN(rankSize), startTs, endTs);
			logger.cleanup();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Close the table
		RankTable.close();		
	}
	
	public static void addEntryToRankFromResult(HashtagRank rank, Result res) {
		try {
			
			for(int x = 1; x <= 3; x++) {
				
				Field hashtagColumnField = RankTable.class.getField(RankTable.HASHTAG_COLUMN_PREFIX + x);
				Field countColumnField = RankTable.class.getField(RankTable.COUNT_COLUMN_PREFIX + x);
				
				byte[] hashtagColumn = (byte[]) hashtagColumnField.get(null);
				byte[] countColumn = (byte[]) countColumnField.get(null);
				
				byte[] hashtagRaw = res.getValue(RankTable.CF_HASHTAGS, hashtagColumn);
				byte[] countRaw = res.getValue(RankTable.CF_COUNTS, countColumn);
				
				if(hashtagRaw != null && countRaw != null) {
					rank.add(new HashtagRankEntry("", Bytes.toString(hashtagRaw), Bytes.toInt(countRaw)));
				}
				
			}
			
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
				
	}

}
