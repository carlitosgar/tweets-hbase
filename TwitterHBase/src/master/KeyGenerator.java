package master;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;

public class KeyGenerator {

	public static byte[] generateKey(Long time, String lang) {
		return Bytes.concat(Longs.toByteArray(time), lang.getBytes());
	}
	
	public static byte[] getStartKey(Long time) {
		return generateKey(time, "aa");
	}
	
	public static byte[] getEndKey(Long time) {
		return generateKey(time, "zz");
	}

}
