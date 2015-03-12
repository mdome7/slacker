package com.labs2160.slacker.plugin.hipchat;

public enum Emoticon {

	DOH("doh"), SHRUG("shrug"), RUKM("areyoukiddingme");
	
	private String code;
	
	private Emoticon(String code) {
		this.code = code;
	}
	
	public String toString() {
		return "(" + code + ")";
	}
}
