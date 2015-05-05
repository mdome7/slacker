package com.labs2160.slacker.plugin.slackchat;

import com.labs2160.slacker.api.SlackerResponse;

public class OutputUtil {

	public final static String [][] REPLACE_PATTERNS = {
		{"\n", "<br/>"}, {"<", "&lt;"}, {">", "&gt;"}, {" ", "&#160;"}
	};

	public static String cleanResponse(SlackerResponse response) {
		String html = response.getMessage();//.replaceAll("  ", "&nbsp;&nbsp;");
		for (String [] pair : REPLACE_PATTERNS) {
			html = html.replaceAll(pair[0], pair[1]);
		}
		return "<html xmlns='http://jabber.org/protocol/xhtml-im'><body xmlns='http://www.w3.org/1999/xhtml'><p>" + html + "</p></body></html>";
	}
}
