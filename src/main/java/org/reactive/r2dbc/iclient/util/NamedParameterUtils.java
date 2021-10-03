package org.reactive.r2dbc.iclient.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.lang.Nullable;
import org.springframework.r2dbc.core.binding.BindMarker;
import org.springframework.r2dbc.core.binding.BindMarkers;
import org.springframework.r2dbc.core.binding.BindMarkersFactory;
import org.springframework.util.Assert;

/**
 * 
 * Helper methods for named parameter parsing.
 *
 * <p>Only intended for internal use within Spring's R2DBC
 * iClient.
 *
 * @author Bhautik Bhanani
 */
public class NamedParameterUtils {

	/**
	 * Set of characters that qualify as comment or quotes starting characters.
	 */
	private static final String[] START_SKIP = new String[] { "'", "\"", "--", "/*" };

	/**
	 * Set of characters that at are the corresponding comment or quotes ending
	 * characters.
	 */
	private static final String[] STOP_SKIP = new String[] { "'", "\"", "\n", "*/" };

	/**
	 * Set of characters that qualify as parameter separators, indicating that a
	 * parameter name in an SQL String has ended.
	 */
	private static final String PARAMETER_SEPARATORS = "\"':&,;()|=+-*%/\\<>^";

	/**
	 * An index with separator flags per character code. Technically only needed
	 * between 34 and 124 at this point.
	 */
	private static final boolean[] separatorIndex = new boolean[128];

	static {
		for (char c : PARAMETER_SEPARATORS.toCharArray()) {
			separatorIndex[c] = true;
		}
	}

	public static ParsedSql parseSqlStatement(String sql) {
		Assert.notNull(sql, "SQL must not be null");

		Set<String> namedParameters = new HashSet<>();
		StringBuilder sqlToUse = new StringBuilder(sql);
		List<ParameterHolder> parameterList = new ArrayList<>();

		char[] statement = sql.toCharArray();
		int namedParameterCount = 0;
		int unnamedParameterCount = 0;
		int totalParameterCount = 0;

		int escapes = 0;
		int i = 0;
		while (i < statement.length) {
			int skipToPosition = i;
			while (i < statement.length) {
				skipToPosition = skipCommentsAndQuotes(statement, i);
				if (i == skipToPosition) {
					break;
				} else {
					i = skipToPosition;
				}
			}
			if (i >= statement.length) {
				break;
			}
			char c = statement[i];
			if (c == ':' || c == '&') {
				int j = i + 1;
				if (c == ':' && j < statement.length && statement[j] == ':') {
					// Postgres-style "::" casting operator should be skipped
					i = i + 2;
					continue;
				}
				String parameter = null;
				if (c == ':' && j < statement.length && statement[j] == '{') {
					// :{x} style parameter
					while (statement[j] != '}') {
						j++;
						if (j >= statement.length) {
							throw new InvalidDataAccessApiUsageException(
									"Non-terminated named parameter declaration at position " + i + " in statement: "
											+ sql);
						}
						if (statement[j] == ':' || statement[j] == '{') {
							throw new InvalidDataAccessApiUsageException("Parameter name contains invalid character '"
									+ statement[j] + "' at position " + i + " in statement: " + sql);
						}
					}
					if (j - i > 2) {
						parameter = sql.substring(i + 2, j);
						namedParameterCount = addNewNamedParameter(namedParameters, namedParameterCount, parameter);
						totalParameterCount = addNamedParameter(parameterList, totalParameterCount, escapes, i, j + 1,
								parameter);
					}
					j++;
				} else {
					while (j < statement.length && !isParameterSeparator(statement[j])) {
						j++;
					}
					if (j - i > 1) {
						parameter = sql.substring(i + 1, j);
						namedParameterCount = addNewNamedParameter(namedParameters, namedParameterCount, parameter);
						totalParameterCount = addNamedParameter(parameterList, totalParameterCount, escapes, i, j,
								parameter);
					}
				}
				i = j - 1;
			} else {
				if (c == '\\') {
					int j = i + 1;
					if (j < statement.length && statement[j] == ':') {
						// escaped ":" should be skipped
						sqlToUse.deleteCharAt(i - escapes);
						escapes++;
						i = i + 2;
						continue;
					}
				}
			}
			i++;
		}
		ParsedSql parsedSql = new ParsedSql(sqlToUse.toString());
		for (ParameterHolder ph : parameterList) {
			parsedSql.addNamedParameter(ph.getParameterName(), ph.getStartIndex(), ph.getEndIndex());
		}
		parsedSql.setNamedParameterCount(namedParameterCount);
		parsedSql.setUnnamedParameterCount(unnamedParameterCount);
		parsedSql.setTotalParameterCount(totalParameterCount);
		return parsedSql;
	}

	private static int skipCommentsAndQuotes(char[] statement, int position) {
		for (int i = 0; i < START_SKIP.length; i++) {
			if (statement[position] == START_SKIP[i].charAt(0)) {
				boolean match = true;
				for (int j = 1; j < START_SKIP[i].length(); j++) {
					if (statement[position + j] != START_SKIP[i].charAt(j)) {
						match = false;
						break;
					}
				}
				if (match) {
					int offset = START_SKIP[i].length();
					for (int m = position + offset; m < statement.length; m++) {
						if (statement[m] == STOP_SKIP[i].charAt(0)) {
							boolean endMatch = true;
							int endPos = m;
							for (int n = 1; n < STOP_SKIP[i].length(); n++) {
								if (m + n >= statement.length) {
									// last comment not closed properly
									return statement.length;
								}
								if (statement[m + n] != STOP_SKIP[i].charAt(n)) {
									endMatch = false;
									break;
								}
								endPos = m + n;
							}
							if (endMatch) {
								// found character sequence ending comment or quote
								return endPos + 1;
							}
						}
					}
					// character sequence ending comment or quote not found
					return statement.length;
				}
			}
		}
		return position;
	}

	private static int addNewNamedParameter(Set<String> namedParameters, int namedParameterCount, String parameter) {
		if (!namedParameters.contains(parameter)) {
			namedParameters.add(parameter);
			namedParameterCount++;
		}
		return namedParameterCount;
	}

	private static int addNamedParameter(List<ParameterHolder> parameterList, int totalParameterCount, int escapes,
			int i, int j, String parameter) {

		parameterList.add(new ParameterHolder(parameter, i - escapes, j - escapes));
		totalParameterCount++;
		return totalParameterCount;
	}

	private static boolean isParameterSeparator(char c) {
		return (c < 128 && separatorIndex[c]) || Character.isWhitespace(c);
	}

	private static class ParameterHolder {

		private final String parameterName;

		private final int startIndex;

		private final int endIndex;

		ParameterHolder(String parameterName, int startIndex, int endIndex) {
			this.parameterName = parameterName;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		String getParameterName() {
			return this.parameterName;
		}

		int getStartIndex() {
			return this.startIndex;
		}

		int getEndIndex() {
			return this.endIndex;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof ParameterHolder)) {
				return false;
			}
			ParameterHolder that = (ParameterHolder) o;
			return this.startIndex == that.startIndex && this.endIndex == that.endIndex
					&& Objects.equals(this.parameterName, that.parameterName);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.parameterName, this.startIndex, this.endIndex);
		}
	}

	/**
	 * Holder for bind markers progress.
	 */
	static class NamedParameters {

		private final BindMarkers bindMarkers;

		private final boolean identifiable;

		private final Map<String, List<NamedParameter>> references = new TreeMap<>();

		NamedParameters(BindMarkersFactory factory) {
			this.bindMarkers = factory.create();
			this.identifiable = factory.identifiablePlaceholders();
		}

		/**
		 * Get the {@link NamedParameter} identified by {@code namedParameter}.
		 * Parameter objects get created if they do not yet exist.
		 * 
		 * @param namedParameter the parameter name
		 * @return the named parameter
		 */
		NamedParameter getOrCreate(String namedParameter) {
			List<NamedParameter> reference = this.references.computeIfAbsent(namedParameter, key -> new ArrayList<>());
			if (reference.isEmpty()) {
				NamedParameter param = new NamedParameter(namedParameter);
				reference.add(param);
				return param;
			}
			if (this.identifiable) {
				return reference.get(0);
			}
			NamedParameter param = new NamedParameter(namedParameter);
			reference.add(param);
			return param;
		}

		@Nullable
		List<NamedParameter> getMarker(String name) {
			return this.references.get(name);
		}

		class NamedParameter {

			private final String namedParameter;

			private final List<BindMarker> placeholders = new ArrayList<>();

			NamedParameter(String namedParameter) {
				this.namedParameter = namedParameter;
			}

			/**
			 * Create a placeholder to translate a single value into a bindable parameter.
			 * <p>
			 * Can be called multiple times to create placeholders for array/collections.
			 * 
			 * @return the placeholder to be used in the SQL statement
			 */
			String addPlaceholder() {
				BindMarker bindMarker = NamedParameters.this.bindMarkers.next(this.namedParameter);
				this.placeholders.add(bindMarker);
				return bindMarker.getPlaceholder();
			}

			String getPlaceholder() {
				return getPlaceholder(0);
			}

			String getPlaceholder(int counter) {
				while (counter + 1 > this.placeholders.size()) {
					addPlaceholder();
				}
				return this.placeholders.get(counter).getPlaceholder();
			}
		}
	}
}
