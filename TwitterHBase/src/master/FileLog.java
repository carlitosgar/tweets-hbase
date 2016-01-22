package master;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import master.structures.HashtagRankEntry;

public class FileLog {
	
	private BufferedWriter writer;
	
	public FileLog(String path){
		File file;
		try {
			file = new File(path);
			if (!file.exists()){
				file.createNewFile();
			}
			this.writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file, true),"UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void writeToFile(List<HashtagRankEntry> rank, Long startTs, Long endTs, boolean query3) throws IOException{
		HashtagRankEntry entry;
		String s = "";
		for(int i = 0; i < rank.size(); i++){
			entry = rank.get(i);
			if(query3){
				s = (i+1) + "," + entry.hashtag + "," + entry.count + "," + startTs + "," + endTs; 
			} else {
				s = entry.language + "," + (i+1) + "," + entry.hashtag + "," + startTs + "," + endTs;
			}
			this.writer.write(s);
			this.writer.newLine();
			this.writer.flush();
		}
	}
	
	public void cleanup() throws IOException{
		this.writer.close();
	}
}
