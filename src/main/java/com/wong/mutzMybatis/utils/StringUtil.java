package com.wong.mutzMybatis.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author 黄小天 wongtp@outlook.com
 * @date 2018年2月11日 下午6:33:18
 */
public class StringUtil {
	
	public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
		if (str == null) {
			return new String[0];
		}
		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = new ArrayList<>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0) {
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}
	
	public static String[] toStringArray(Collection<String> collection) {
		return collection.toArray(new String[collection.size()]);
	}

}
