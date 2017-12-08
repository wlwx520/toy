package com.track.toy.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MDHelper {
	private ArrayList<Item> items = new ArrayList<>();

	public void addItem(Item item) {
		items.add(item);
	}

	public void addItem(Item item, int index) {
		items.add(index, item);
	}

	public void removeItem(int index) {
		items.remove(index);
	}

	public void toWrite(String path) {
		File file = new File(path);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try (final BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(path), "utf-8"))) {
			items.forEach(item -> {
				item.toWrite(writer);
				try {
					writer.newLine();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class Image extends Item {
		private String url;

		public Image(String url) {
			this.url = url;
		}

		@Override
		protected void toWrite(BufferedWriter writer) {
			try {
				writer.write("![](");
				writer.write(url);
				writer.write(")");
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static class Text extends Item {
		private List<String> contents;
		private StringBuilder tmpContent;

		public Text() {
			contents = new ArrayList<>();
		}

		public void addText(String content) {
			if (tmpContent == null) {
				tmpContent = new StringBuilder();
				tmpContent.append("* ");
			}
			tmpContent.append(content);
		}

		public void addTextLink(String content, String url) {
			if (tmpContent == null) {
				tmpContent = new StringBuilder();
				tmpContent.append("* ");
			}
			tmpContent.append("[");
			tmpContent.append(content);
			tmpContent.append("]");
			tmpContent.append("(");
			tmpContent.append(url);
			tmpContent.append(")");
		}

		public void newLine() {
			contents.add(tmpContent.toString());
			tmpContent = null;
		}

		@Override
		protected void toWrite(BufferedWriter writer) {
			if (tmpContent != null) {
				contents.add(tmpContent.toString());
			}
			contents.forEach(content -> {
				try {
					writer.write(content);
					writer.newLine();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}

	public static class Title extends Item {
		private String title;
		private int lev;

		public Title(String title, int lev) {
			if (lev < 1 || lev > 4) {
				throw new MDBuilderException("lev must >=1 & <=4");
			}
			this.title = title;
			this.lev = lev;
		}

		@Override
		protected void toWrite(BufferedWriter writer) {
			try {
				for (int i = 0; i < lev; i++) {
					writer.write("#");
				}
				writer.write(" " + title);
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static class Form extends Item {
		private ArrayList<String> title;
		private ArrayList<HashMap<String, String>> data;

		public Form(String... title) {
			this.title = new ArrayList<>();
			this.data = new ArrayList<>();
			for (String t : title) {
				this.title.add(t);
			}
		}

		public Form(ArrayList<String> title) {
			this.title = title;
			this.data = new ArrayList<>();
		}

		public void addRow(HashMap<String, String> row) throws MDBuilderException {
			if (row == null || !title.containsAll(row.keySet())) {
				throw new MDBuilderException("error...title = " + title);
			}
			data.add(row);
		}

		public void addRowByIndex(HashMap<Integer, String> row) throws MDBuilderException {
			HashMap<String, String> newRow = new HashMap<>();
			row.forEach((k, v) -> {
				newRow.put(title.get(k), v);
			});
			data.add(newRow);
		}

		@Override
		protected void toWrite(BufferedWriter writer) {
			if (title.isEmpty()) {
				return;
			}
			try {
				for (int i = 0; i < title.size(); i++) {
					if (i == 0) {
						writer.write("|");
					}
					writer.write(title.get(i));
					writer.write("|");
				}
				writer.newLine();
				for (int i = 0; i < title.size(); i++) {
					if (i == 0) {
						writer.write("|");
					}
					writer.write("--------");
					writer.write("|");
				}
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			data.forEach(row -> {
				try {
					for (int i = 0; i < title.size(); i++) {
						if (i == 0) {
							writer.write("|");
						}
						writer.write(row.get(title.get(i)) == null ? "" : row.get(title.get(i)));
						writer.write("|");
					}
					writer.newLine();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}

	}

	public static abstract class Item {
		protected abstract void toWrite(BufferedWriter writer);
	}

	public static class MDBuilderException extends RuntimeException {

		private static final long serialVersionUID = -3981620198709993918L;

		public MDBuilderException(String msg) {
			super(msg);
		}
	}

}
