package com.gudden.maven.guddengine;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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
			System.out.println("Menu:\n1. Build index \n2. Read and query index\n3. exit\nChoose a selection:");
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
					
					while(sc.hasNextLine()) {
						System.out.println("Which mode do you want? \n1. Ranked Query\n2. Boolean Query");
						boolean ranked = sc.nextLine().trim().equals("1");
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
							search(queryString, ranked);
							break;
						}
					}
					break;
				case 3:
					System.exit(0);
				default:
					System.out.println("Wrong choice");
			}
		}
	}

	// ------------------------------------------------------------------------------------------------------
	
	private static void search(String queryString, boolean ranked) {
		Query query = new Query(queryString);
		List<PositionalPosting> results = GUDDEN.search(query, ranked);
		Set<Integer> resultId = new HashSet<Integer>();
		if (results != null) {
			for (PositionalPosting result : results) {
				if (!resultId.contains(result.getId())) {
					resultId.add(result.getId());
					System.out.println(FILE_NAMES.get(result.getId()));
				}
			}
			System.out.println("Size: " + resultId.size());
		}
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
