package org.sanjose.vasja.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GenUtil {
	
	public static String getCurYear() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(new Date());        
	}
	
}
