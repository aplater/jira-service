package com.ashkan.jira.util;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class JiraTime {
	public static Instant getInstant(String givenTime) {
		try {
			return Instant.parse(givenTime);
		} catch (DateTimeParseException exception) {
			// Some date strings coming from Jira are not in UTC, they are usually like this:
			// 2017-08-16T14:21:00.000-0500
			// The last part (-0500) is the time zone adjustment.
			// We use a DateTimeFormatter to parse them and convert them to UTC:
			DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			return Instant.from(format.parse(givenTime));
		}
	}
}
