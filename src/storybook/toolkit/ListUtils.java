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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author FaVdB
 */
public class ListUtils {
	
	public static String join(List array, String separator) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }
        final StringBuilder buf = new StringBuilder();

        for (int i = 0; i < array.size(); i++) {
            if (i > 0) {
                buf.append(separator);
            }
            if (array.get(i) != null) {
                buf.append(array.get(i));
            }
        }
        return buf.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static List setUnique(List array) {
		List list=new ArrayList();
		if (array!=null) {
			for (Object o:array.toArray()) {
				if (!list.contains(o)) {
					list.add(o);
				}
			}
		}
		return(list);
	}

}
