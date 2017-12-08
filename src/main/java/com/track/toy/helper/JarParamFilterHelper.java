package com.track.toy.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JarParamFilterHelper {
	private List<String> index;

	public JarParamFilterHelper() {
		this.index = new ArrayList<>();
	}

	public void addIndex(String... index) {
		if (index != null) {
			for (String e : index) {
				this.index.add(e);
			}
		}
	}

	public void addIndex(List<String> index) {
		if (index != null) {
			this.index.addAll(index);
		}
	}

	public Map<String, Param> filter(String[] args) {
		Map<String, Param> result = new HashMap<>();
		for (String origin : args) {
			Param param = new Param(origin);
			if (index.contains(param.getKey())) {
				result.put(param.getKey(), param);
			}
		}
		return result;
	}

	public static class Param {
		private String key;
		private String value;
		private List<String> valueList;

		private boolean isList;

		public Param(String origin) {
			StringBuilder key = new StringBuilder();
			ArrayList<String> valueList = null;
			StringBuilder value = new StringBuilder();
			char[] arr = origin.toCharArray();
			boolean isValue = false;
			boolean isSkip = false;
			for (char ch : arr) {
				// key
				if (isValue == false && isSkip == true && ch == '/') {
					isSkip = false;
					key.append(ch);
					continue;
				}

				if (isValue == false && isSkip == true) {
					key.append(ch);
					isSkip = false;
					continue;
				}

				if (isValue == false && isSkip == false && ch == '/') {
					isSkip = true;
					continue;
				}

				if (isSkip == false && ch == '=' && isValue == true) {
					throw new JarParamFileterException("only exists '=' at once");
				}

				if (isValue == false && isSkip == false && ch == '=') {
					isValue = true;
					continue;
				}

				if (isValue == false) {
					key.append(ch);
					continue;
				}

				// value
				if (isValue == true && isSkip == true && ch == '/') {
					isSkip = false;
					value.append(ch);
					continue;
				}
				if (isValue == true && isSkip == true) {
					value.append(ch);
					isSkip = false;
					continue;
				}

				if (isValue == true && isSkip == false && ch == '/') {
					isSkip = true;
					continue;
				}

				if (isValue == true && isSkip == false && ch == ',') {
					if (valueList == null) {
						valueList = new ArrayList<>();
					}

					valueList.add(value.toString());
					value = new StringBuilder();
					continue;
				}

				if (isValue == true) {
					value.append(ch);
					continue;
				}
			}

			this.key = key.toString();

			if (valueList == null) {
				this.isList = false;
				this.value = value.toString();
			} else {
				this.isList = true;
				valueList.add(value.toString());
				this.valueList = valueList;
			}
		}

		public String getKey() {
			return this.key;
		}

		public String getValue() {
			return value;
		}

		public List<String> getValueList() {
			return valueList;
		}

		public boolean isList() {
			return isList;
		}

		@Override
		public String toString() {
			if (isList) {
				return valueList.toString();
			} else {
				return value;
			}
		}

	}

	public static class JarParamFileterException extends RuntimeException {

		private static final long serialVersionUID = -5132909810442540000L;

		public JarParamFileterException(String message) {
			super(message);
		}

	}

}
