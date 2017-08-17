/*
 * Copyright (C) 2017 FaVdB
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package storybook.toolkit;

import java.util.Iterator;
import java.util.Objects;

/**
 *
 * @author FaVdB
 */
public class StringUtils {

	private static final String[][] repJava = {
		{"\"", "\\\""},
		{"\\", "\\\\"},
		{"\b", "\\b"},
		{"\n", "\\n"},
		{"\t", "\\t"},
		{"\f", "\\f"},
		{"\r", "\\r"}
	};
	private static final String[][] repHtml = {
		{"\"", "&quot;"},
		{"&", "&amp;"},
		{"<", "&lt;"},
		{">", "&gt;"}
	};
	private static final String EMPTY="";

    public static String capitalize(final String str) {
        return capitalize(str, null);
    }

    public static String capitalize(final String str, final char... delimiters) {
        final int delimLen = delimiters == null ? -1 : delimiters.length;
        if (str==null || str.isEmpty() || delimLen == 0) {
            return str;
        }
        int strLen = str.length();
        int [] newCodePoints = new int[strLen];
        int outOffset = 0;

        boolean capitalizeNext = true;
        for (int index = 0; index < strLen;) {
            final int codePoint = str.codePointAt(index);

            if (isDelimiter(codePoint, delimiters)) {
                capitalizeNext = true;
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            } else if (capitalizeNext) {
                int titleCaseCodePoint = Character.toTitleCase(codePoint);
                newCodePoints[outOffset++] = titleCaseCodePoint;
                index += Character.charCount(titleCaseCodePoint);
                capitalizeNext = false;
            } else {
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            }
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static boolean isDelimiter(final int codePoint, final char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(codePoint);
        }
        for (int index = 0; index < delimiters.length; index++) {
            int delimiterCodePoint = Character.codePointAt(delimiters, index);
            if (delimiterCodePoint == codePoint) {
                return true;
            }
        }
        return false;
    }

	private static String escape(String[][] rep, String inStr) {
		String ret = inStr;
		for (String[] r : rep) {
			ret = ret.replaceAll(r[0], r[1]);
		}
		return (ret);
	}

	public static String escapeJava(final String inStr) {
		return (escape(repJava, inStr));
	}

	public static String escapeHtml(String inStr) {
		return (escape(repHtml, inStr));
	}

	private static String unescape(String[][] rep, String inStr) {
		String ret = inStr;
		for (String[] r : rep) {
			ret = ret.replaceAll(r[1], r[0]);
		}
		return (ret);
	}

	public static String unescapeJava(final String inStr) {
		return (unescape(repJava, inStr));
	}

	public static String unescapeHtml(String inStr) {
		return (unescape(repHtml, inStr));
	}

	public static String repeat(String str, int repeat) {
		if (str == null) {
			return null;
		}
		if (repeat <= 0) {
			return EMPTY;
		}
		String ret=EMPTY;
		for (int i=0; i < repeat; i++) {
			ret+=str;
		}
		return(ret);
	}

    public static String join(final Iterable<?> iterable, final String separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }

    public static String join(final Iterator<?> iterator, final String separator) {

        // handle null, zero and one elements before building a buffer
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        final Object first = iterator.next();
        if (!iterator.hasNext()) {
            final String result = Objects.toString(first, EMPTY);
            return result;
        }

        // two or more elements
        final StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

}
