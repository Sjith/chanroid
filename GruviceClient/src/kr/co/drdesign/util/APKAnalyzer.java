package kr.co.drdesign.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/* APK 파일을 분석해서 패키지 명을 알려주는 Class 
 */
public class APKAnalyzer {

	private static int bufferSize = 2048;

	private static String findPackageName( String str )
	{
		int index1 = str.indexOf("manifest") + "manifest".length();
		int index2 = str.indexOf("application");

		if( index1 == -1 || index2 == -1 ) return null;

		boolean isStart = false, isFinish = false;
		for( int i = index1 ; i < index2 ; i++ )
		{
			isFinish = true;
			char d = str.charAt(i);
			//패턴은 되도록 쓰지말라는 권고에 따라서 아스키값으로 비교
			if( 46 <= d && d <= 122 )
			{
				isStart = true;
				isFinish = false;
			}

			if( isStart == false ) index1++;
			if( isStart && isFinish) {
				index2 = i;
				break;
			}
		}
		return str.substring(index1, index2);
	}

	public static String findPackageNameFromAPK( String path ) throws IOException
	{ 
		FileInputStream fis = new FileInputStream(path);
		ZipInputStream zis = new ZipInputStream(fis);

		ZipEntry zentry;
//		String fileName;
		String packageName = null;
		while( (zentry = zis.getNextEntry()) != null )
		{
//			fileName = zentry.getName();
			if( zentry.getName().equalsIgnoreCase("AndroidManifest.xml") )
			{
				BufferedInputStream bis = new BufferedInputStream( zis );
				int c = 0;
				int toggle = 0xFFFFFFFF;


				byte[] buffer1 = new byte[bufferSize];
				byte[] buffer2 = new byte[bufferSize];

				int cnt1=0, cnt2=0;
				while( (c = bis.read()) != -1 )
				{
					if ( ( toggle ^= 0xFFFFFFFF ) == 0)
						buffer1[cnt1++] = (byte)c;
					else
						buffer2[cnt2++] = (byte)c;

					if( cnt1 == bufferSize || cnt2 == bufferSize  ) break;
				}

				String str1 = new String( buffer1 );
				packageName = findPackageName(str1);
				if( packageName == null )
				{
					String str2 = new String( buffer2 );
					packageName = findPackageName(str2);
				}
				break;
			}
		}
		return packageName;
	}
}