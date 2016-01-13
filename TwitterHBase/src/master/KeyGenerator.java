package master;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;

public class KeyGenerator {

	public static byte[] generateKey(Long time, String lang) {
		return Bytes.concat(Longs.toByteArray(time), lang.getBytes());
	}
	
	public static byte[] generateEndKey(Long time, String lang) {
		byte[] langBytes = lang.getBytes();
		langBytes[langBytes.length - 1]++;
		return Bytes.concat(Longs.toByteArray(time), langBytes);
	}
	
	public static byte[] generateKey(Long time) {
		return Longs.toByteArray(time);
	}
	
	public static byte[] generateEndKey(Long time) {
		return Longs.toByteArray(time++);
	}

}
