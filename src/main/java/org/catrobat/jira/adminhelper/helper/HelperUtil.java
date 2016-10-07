/*
 * Copyright 2014 Stephan Fellhofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.catrobat.jira.adminhelper.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public class HelperUtil {

    public static String formatString(final String toFormat) {
        String formatted;
        if (toFormat != null && toFormat.trim().length() != 0) {
            formatted = escapeHtml4(toFormat.trim());
        } else {
            formatted = null;
        }

        return formatted;
    }

    public static Date clearTime(final Date date) {
        Calendar beginCalendar = new GregorianCalendar();
        beginCalendar.setTime(date);
        beginCalendar.set(Calendar.HOUR_OF_DAY, 0);
        beginCalendar.clear(Calendar.MINUTE);
        beginCalendar.clear(Calendar.SECOND);
        beginCalendar.clear(Calendar.MILLISECOND);

        return beginCalendar.getTime();
    }
}
