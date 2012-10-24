package kr.co.drdesign.client.aria;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;

import kr.co.drdesign.client.connector.WebConnector;
import kr.re.nsri.aria.ARIAEngine;

/**
 * 
 * @author drdesign
 *
 */
//���� ���Ǳ� ������ �ݵ�� ����ȭ ���Ѿ� ��.
public class ARIAHelper {

	//keySize must be 128 or 192 or 256. 
	private static final int KEYSIZE = 128;
	//key is 16, 24, 32 byte 
	public byte[] key;
	public ARIAEngine aRIAEngine;

	public ARIAHelper(){
		try {
			aRIAEngine = new ARIAEngine(KEYSIZE);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}
	public ARIAHelper(String keyword){
		this();
		setKeys(keyword);
		setupRoundKey();
	}
	public void setKeys(String keyword){
		setKeys(keyword.getBytes());
	}

	public  void setKeys(byte[] key){
		
		byte[] fixedLengthkey = new byte[KEYSIZE/8];
		System.arraycopy(key, 0, fixedLengthkey, 0, key.length);
		
		try {
			aRIAEngine.setKey(fixedLengthkey) ;
			this.key = fixedLengthkey;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}
	public void setupRoundKey(){
		try {
			aRIAEngine.setupRoundKeys();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}
	public byte[] encrypt(CharSequence word)
	{
		return this.encrypt(word.toString().getBytes());	
	}

	//	����ȭ�� ���ؼ� .length ���� ������ ��� ���� �ߴ�.
	// Object�� ���� �����ϴ� ����� ������?
	public byte[] encrypt(byte[] word)
	{
		int length = word.length;
		length = (length%16==0)?length:(length/16+1)*16 ;

		byte[] b = new byte[length];
		System.arraycopy(word, 0, b, 0, word.length);

		try{
			for( int i =0; i < length ; i += 16){
				aRIAEngine.encrypt(b, i, b, i);
			}
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return b;
	}
	public String decryptToString(byte[] word)
	{
		try {
			return new String(this.decrypt(word), WebConnector.CHARACTER_SET).trim();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(this.decrypt(word)).trim();
	}
	//	����ȭ�� ���ؼ� .length ���� ������ ��� ���� �ߴ�.
	public byte[] decrypt(byte[] word)
	{
		int length = word.length;
		length = (length%16==0)?length:(length/16+1)*16 ;

		byte[] b = new byte[length];
		System.arraycopy(word, 0, b, 0, word.length);

		try{
			for( int i =0; i < length ; i += 16){
				aRIAEngine.decrypt(b, i, b, i);
			}
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return b;
	}
	public static void main( String[] args)
	{

		byte[] key = new byte[32];
		for (int i=0; i<32; i++)
			key[i]=(byte)i;

		ARIAHelper ah = new ARIAHelper("0123456789012345");
		//ah.setKeys(new String(key));
		ARIAHelper ah2 = new ARIAHelper("0123456789012345");
//		ah.setupRoundKey();

		System.out.println( new String( ah2.decrypt(ah.encrypt("0123456789012345012345678901234501234567890123450".getBytes()))).trim());

	}
}
