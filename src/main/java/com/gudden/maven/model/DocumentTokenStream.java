package com.gudden.maven.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class DocumentTokenStream implements TokenStream {

	private Scanner reader;

	// ------------------------------------------------------------------------------------------------------

	public DocumentTokenStream(File file) {
		this.reader = new Scanner(process(file));
	}

	// ------------------------------------------------------------------------------------------------------

	public DocumentTokenStream(String token) {
		this.reader = new Scanner(token);
	}

	// ------------------------------------------------------------------------------------------------------

	public boolean hasNextToken() {
		return this.reader.hasNext();
	}

	// ------------------------------------------------------------------------------------------------------

	public String nextToken() {
		if (!hasNextToken())
			return null;
		String type = reader.next().toLowerCase();
		return type.length() > 0 ? type : hasNextToken() ? nextToken() : null;
	}

	// ------------------------------------------------------------------------------------------------------

	private Document getDocument(File file) {
		Gson gson = new Gson();
		JsonObject json = null;

		try {
			json = parseToJsonObject(new FileInputStream(file));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return gson.fromJson(json, Document.class);
	}

	// ------------------------------------------------------------------------------------------------------

	private JsonObject parseToJsonObject(InputStream in) throws IOException {
		JsonParser parser = new JsonParser();
		return parser.parse(new JsonReader(new InputStreamReader(in, "UTF-8"))).getAsJsonObject();
	}

	// ------------------------------------------------------------------------------------------------------

	private String process(File file) {
		return getDocument(file).toString();
	}

}
