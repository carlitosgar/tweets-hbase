package master;

import com.google.common.primitives.Bytes;

public class KeyGenerator {

	public static byte[] generateKey(Long time, String lang) {
		byte[] bytes = Bytes.concat(time.toString().getBytes(), lang.getBytes());
		return bytes;
	}
	
	public static byte[] generateEndKey(Long time, String lang) {
		byte[] langBytes = lang.getBytes();
		langBytes[langBytes.length - 1]++;
		return Bytes.concat(time.toString().getBytes(), langBytes);
	}
	
	public static byte[] generateKey(Long time) {
		return time.toString().getBytes();
	}
	
	public static byte[] generateEndKey(Long time) {
		return ((Long)(time + 1)).toString().getBytes();
	}

}
