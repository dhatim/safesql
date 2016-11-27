package org.dhatim.safesql.formatter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum FormatterState {
	STATE_0		("", false),
	WITH		("with", true),
	SELECT		("select", true),
	FROM		("from", true),
	WHERE		("where", false),
	GROUP_BY	("group", true),
	HAVING		("having", true),
	WINDOW		("window", true),
	UNION		("union", false),
	INTERSECT	("intersect", false),
	EXCEPT		("except", false),
	ORDER_BY	("order", true),
	LIMIT		("limit", false),
	OFFSET		("offset", false),
	FETCH		("fetch", false),
	FOR			("for", true);
	
	private static final Map<String, FormatterState> VALUES;
	
	static {
		VALUES = new HashMap<>();
		for (FormatterState state : values()) {
			VALUES.put(state.startToken, state);
		}
	}
	
	private final String startToken;
	private final boolean multiple;

	FormatterState(String startToken, boolean multiple) {
		this.startToken = startToken;
		this.multiple = multiple;
	}
	
	public static FormatterState recognize(String s) {
		return VALUES.get(s.toLowerCase(Locale.ROOT));
	}
}
