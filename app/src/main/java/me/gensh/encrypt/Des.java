package me.gensh.encrypt;

/**
 * @author gensh
 */

public class Des {
	public static String encode(String strEn,String key){
		   char[] charEnArr=strEn.toCharArray();
		   char[] keyArr=key.toCharArray();
		   for(int i=0;i<charEnArr.length;i++){
			   int position=i%keyArr.length;
			   charEnArr[i]=(char) (charEnArr[i]+keyArr[position]+i);
		   }
		return String.valueOf(charEnArr);
	}
	
	public static String decode(String strDe,String key){
		   char[] charDeArr=strDe.toCharArray();
		   char[] keyArr=key.toCharArray();
		   for(int i=0;i<charDeArr.length;i++){
			 int position=i%keyArr.length;
			 charDeArr[i]=(char) (charDeArr[i]-keyArr[position]-i);
		   }
		return String.valueOf(charDeArr);
	}
	   
}
