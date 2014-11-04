package org.jumbune.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.apache.hadoop.io.Text;
import org.jumbune.utils.PatternMatcher;
import org.junit.Test;

public class PatternMatcherTest {
	PatternMatcher patternMatcher;

	@Test
	public void matchTestAgainstNull() {
		boolean check = PatternMatcher.match(null);
		assertTrue(check);
	}

	@Test
	public void matchWithNullValue() {
		boolean check = PatternMatcher.match(null, UtilitiesConstantsTestInterface.REGEX);
		assertFalse(check);
	}

	@Test
	public void matchRegexTest() {
		Text value = new Text();
		value.set("09/03/1989");
		boolean check = PatternMatcher.match(value, UtilitiesConstantsTestInterface.REGEX);
		assertTrue(check);
	}

	@Test
	public void matchRegexZeroLengthTest() {
		Text value = new Text();
		value.set("");
		boolean check = PatternMatcher.match(value, UtilitiesConstantsTestInterface.REGEX);
		assertFalse(check);
	}

	@Test
	public void matchPatternTest() {
		Text value = new Text();
		value.set("09/03/1989");
		Pattern pattern = Pattern.compile(UtilitiesConstantsTestInterface.REGEX);
		boolean check = PatternMatcher.match(value, pattern);
		assertTrue(check);
	}

	@Test
	public void matchPatternZeroLengthTest() {

		Text value = new Text();
		value.set("");
		Pattern pattern = Pattern.compile(UtilitiesConstantsTestInterface.REGEX);
		boolean check = PatternMatcher.match(value, pattern);
		assertFalse(check);
	}

	@Test
	public void matchPatternNullTest() {
		Pattern pattern = Pattern.compile(UtilitiesConstantsTestInterface.REGEX);
		boolean check = PatternMatcher.match(null, pattern);
		assertFalse(check);
	}

	@Test
	public void matchTestAgainstStringNull() {
		Text value = new Text();
		value.set("null");
		boolean check = PatternMatcher.match(value);
		assertFalse(check);
	}
}
