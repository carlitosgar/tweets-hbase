package master.modes;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;

import master.FileLog;
import master.KeyGenerator;
import master.RankTable;
import master.structures.HashtagRank;
import master.structures.HashtagRankEntry;

public class Mode1 {
	
	/**
	 * 
	 * @param args mode startTS endTS N language outputFolder
	 */
	public static void execute(String[] args) {
		
		if(args.length != 6) {
			System.err.println("Invalid arguments for mode 1");
			return;
		}
		
		Long startTs = Long.parseLong(args[1]);
		Long endTs = Long.parseLong(args[2]);
		int rankSize = Integer.parseInt(args[3]);
		String lang = args[4].toLowerCase();
		String logPath = (args[5].endsWith(File.separator) ? args[5] : args[5] + File.separator);
		
		if(lang.length() != 2) {
			System.err.println("Lang parameter should have two characters.");
			return;
		}
		
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
		byte[] startKey = KeyGenerator.generateKey(startTs, lang);
		byte[] endKey = KeyGenerator.generateEndKey(endTs, lang);

		Scan scan = new Scan(startKey, endKey);
		
		// Add filter by lang
		RegexStringComparator endsWithLang = new RegexStringComparator(lang + "$");
		RowFilter langFilter = new RowFilter(CompareOp.EQUAL, endsWithLang);
		scan.setFilter(langFilter);
		
		try {
			ResultScanner rs = table.getScanner(scan);
			
			// Create rank object
			HashtagRank rank = new HashtagRank();
			
			Result res = rs.next();
			while (res != null && !res.isEmpty()) {
				
				// Process each resutl adding its containing entries to the rank
				addEntryToRankFromResult(rank, res);
				
				// Next result
				res = rs.next();
			}
			
			// Log results
			FileLog logger = new FileLog(logPath + "02_query1.out");
			logger.writeToFile(rank.getBestN(rankSize), startTs, endTs, false);
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
				
				String key = Bytes.toString(res.getRow());
				String lang = key.substring(key.length()-2);
				
				if(hashtagRaw != null && countRaw != null) {
					rank.add(new HashtagRankEntry(lang, Bytes.toString(hashtagRaw), Bytes.toInt(countRaw)));
				}
				
			}
			
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
				
	}
	

}
