package com.gudden.maven.guddengine;

import java.util.List;
import java.util.Scanner;

import com.gudden.maven.model.Guddengine;
import com.gudden.maven.model.Normalizer;
import com.gudden.maven.model.PositionalPosting;
import com.gudden.maven.model.Query;

/**
 * Hello world!
 *
 */
public class App {
	private static final Guddengine GUDDEN = new Guddengine();
	private static List<String> FILE_NAMES = null;
	
	// ------------------------------------------------------------------------------------------------------
	
	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		while(true) {
			System.out.print("Menu:\n1. Build index \n2. Read and query index\n3. exit\nChoose a selection: ");
			int menuChoice = Integer.parseInt(sc.nextLine().trim());
			System.out.print("Please enter the directory path you wish to index: ");
			String path = sc.nextLine();
			switch(menuChoice) {
				case 1:
					FILE_NAMES = indexDirectory(GUDDEN, path);
					break;
				case 2:
					GUDDEN.setDiskIndexes(path);
					FILE_NAMES = GUDDEN.getFileNames(path);
					
					while(true) {
						System.out.println("Which mode do you want? \n1. Ranked Query\n2. Boolean Query");
						boolean ranked = sc.nextLine().trim().equals("1");
						printCommands();
						String queryString = sc.nextLine().trim();
						String[] specialQuery = queryString.split("\\s+");
						switch(specialQuery[0]) {
						case ":q":
							System.out.println("Have a good day. Thank you for using guddengine.");
							sc.close();
							System.exit(0);
							break;
						case ":stem":
							System.out.print("The stemmed token for \"" + specialQuery[1] + "\" is \"");
							System.out.println(Normalizer.stem(specialQuery[1]) + "\"");
							break;
						case ":index":
							FILE_NAMES = indexDirectory(GUDDEN,specialQuery[1]);
							break;
						case ":vocab":
							for (String each : GUDDEN.vocabulary()) System.out.println(each);
							System.out.println("There are total of " + GUDDEN.vocabulary().length + " vocabularies.");
							break;
						default:
							if (ranked) rankedSearch(queryString);
							else search(queryString);
							break;
						}
					}
				case 3:
					System.exit(0);
				default:
					System.out.println("Wrong choice");
			}
		}
	}
	
	private static void printCommands() {
		System.out.println(":q to quit");
		System.out.println(":stem 'word' to stem a given word");
		System.out.println(":index 'path' to index a new directory path");
		System.out.println(":vocab to print out the vocabularies in the entire corpus.");
		System.out.print("Enter a special command or a phrase you want search for: ");
	}

	// ------------------------------------------------------------------------------------------------------
	
	private static void search(String queryString) {
		Query query = new Query(queryString);
		List<PositionalPosting> results = GUDDEN.search(query);
		if (!results.isEmpty())
			for (PositionalPosting result : results) {
				System.out.printf("Filename: %s\n\n", FILE_NAMES.get(result.getId()));
				System.out.print("Id: " + result.getId() + "\tPositions:");
				for (int i : result.getPositions()) {
					System.out.print(" " + i);
				}
				System.out.println();
			}
		System.out.println("Size: " + results.size());
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private static void rankedSearch(String queryString) {
		Query query = new Query(queryString);
		List<PositionalPosting> results = GUDDEN.rankedSearch(query);
		if (!results.isEmpty())
			for (PositionalPosting result : results)
				System.out.printf("Filename: %s\tScore: %.2f\n\n", FILE_NAMES.get(result.getId()), result.getScore());
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private static List<String> indexDirectory(Guddengine engine, String path) {
		System.out.println("Indexing the new path at \"" + path + "\"");
		List<String> fileNames = null;
		engine.resetIndex();
		fileNames = engine.indexDirectory(path);
		System.out.println("Done Indexing");
		return fileNames;
	}
}
