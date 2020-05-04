package com.ashkan.jira.util;

import java.time.Instant;
import java.time.format.DateTimeParseException;

public class JiraTime {
	public static Instant getInstant(String givenTime) {
		try {
			return Instant.parse(givenTime);
		} catch (DateTimeParseException exception) {
			// Noticed that some date strings coming from Jira is not a
			// valid UTC format, they are usually like this:
			// 2017-08-16T14:21:00.000-0500
			// When we get one, we convert it to this:
			// 2017-08-16T14:21:00.000Z
			long count = givenTime.chars().filter(ch -> ch == '-').count();
			if (count > 2) {
				givenTime = givenTime.substring(0, givenTime.lastIndexOf("-")) + "Z";
				return Instant.parse(givenTime);
			}
		}
		return Instant.now(); // TODO: throw exception here
	}
}
