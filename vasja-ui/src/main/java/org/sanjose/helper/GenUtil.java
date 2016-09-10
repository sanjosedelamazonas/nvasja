package org.sanjose.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GenUtil {
	
	public static String getCurYear() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(new Date());        
	}


	public static boolean strNullOrEmpty(String s) {
		if (s==null || "".equals(s))
			return true;
		else
			return false;
	}

	public static boolean objNullOrEmpty(Object s) {
		if (s==null)
			return true;
		else
			return strNullOrEmpty(s.toString());
	}

}
