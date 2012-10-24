package kr.co.drdesign.parmtree.aria;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;

import kr.co.drdesign.parmtree.connector.WebConnector;


public class ARIAInputStreamHelper {
	private int bufferSize 	= (16*64) * 8; // 8k byte
	private byte[] buffer 	= new byte[ bufferSize ];
	private long length;

//	private long accReadPos 	= 0; 
	//스트림으로부터 읽은 사이즈 누적
	private int readSize 		= 0; //스트림으로부터 읽은 사이즈
	private long accSendPos 	= 0; //사용자에게 보내준 사이즈 누적
	private int sendSize 		= 0; //스트림으로부터 읽은 사이즈
	private InputStream is;

	private ARIAEngine aRIAEngine;
	private int keySize 	= 128;
	private String keycode 	= WebConnector.keycode;
	
	ARIAHelper ah ;
	
	public ARIAInputStreamHelper( InputStream is, long length)
	{
		this.is 	= is;
		this.length = length;
		try {
			aRIAEngine = new ARIAEngine(keySize);
			aRIAEngine.setKey(keycode.getBytes());
			aRIAEngine.setupRoundKeys();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ah = new ARIAHelper(keycode);
		fill();
	};

	public int read( byte[] to )
	{
		if( accSendPos >= length ) return -1;

		int r = readSize - sendSize;
		if ( r == 0){
			return 0;
		}
		else if( r < 0 ){
			return -1; 
		}
		else{
			r = (to.length < r)?to.length:r;
			System.arraycopy(buffer, sendSize, to, 0, r);
			
			sendSize = sendSize + r;
			if( sendSize == readSize ){
				sendSize = 0;
				fill();
			}
		}
		accSendPos += r;
		return r;
	}
	private void fill(){
		try 
		{
			readSize = is.read(buffer);
			if ( readSize != -1 )
			{
				if ( readSize < bufferSize )
				{
					for( int i = readSize ; i < bufferSize ; i++)
					{
						buffer[i] = 0;
					}
				}
				try{
					int l = readSize;
					l = (l%16==0)?l:(l/16+1)*16 ;
					for( int i =0; i < l ; i += 16){
						aRIAEngine.decrypt(buffer, i, buffer, i);
					}
				} catch (InvalidKeyException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
