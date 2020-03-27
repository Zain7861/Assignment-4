
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

public class Main {

	static class Movie {
		String name;
		int year;

		public Movie(String name, int year) {
			this.name = name;
			this.year = year;
		}

	}

	public static void main(String[] args) {
		String filename = "C:/Users/ouji7/Documents/movies.csv";
		Scanner sc;
		try {
			sc = new Scanner(new File(filename));
			sc.nextLine(); // take out the headers
			HashMap<String, ArrayList<Movie>> map = new HashMap<>();
			TreeMap<Integer, ArrayList<Movie>> treemap = new TreeMap<>();
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				// certain movies have commas within the movie title, thereby
				// causing the title to be split across tokens

				String[] tokens = line.split(",");
				if (tokens.length > 3) {
					int q1 = line.indexOf("\"");
					int q2 = line.indexOf("\"", q1 + 1);

					tokens[1] = line.substring(q1 + 1, q2); // title between
															// quotes
					tokens[2] = line.substring(q2 + 2); // skip quote and comma
					// debugging code to determine irregular token length lines
					// System.out.println(line);
				}

				// pipe character may be interpreted as or in a regular
				// expression
				// so it ought to be escaped
				String[] genres = tokens[2].split("\\|");
				String title = tokens[1];
				int r = title.lastIndexOf(")");
				int l = title.lastIndexOf("(", r - 1);
				// certain movies do not have the year in the title
				// so the right index, r, being positive will indicate whether a
				// year is there
				int year;
				if (r > 0) {
					// For whatever reason, some movies specify a range of
					// years, of which
					// just the lower end of the range will be taken
					String yearString = title.substring(l + 1, r);
					if (yearString.contains("–")) {
						year = Integer.parseInt(yearString.substring(0, yearString.indexOf("–")));
					} else {
						year = Integer.parseInt(yearString);
					}
					// title needs to be truncated if year is specified
					title = title.substring(0, l - 1);
				} else {
					year = 0;
				}
				
				
				// add movies to data structures organized by genre
				// and by year
				Movie m = new Movie(title, year);
				for (String genre : genres) {
					if (!map.containsKey(genre)) {
						map.put(genre, new ArrayList<>());
					}
					map.get(genre).add(m);
					if (r > 0) {
						if (!treemap.containsKey(year)) {
							treemap.put(year, new ArrayList<>());
						}
						treemap.get(year).add(m);
					}
				}
			}

			System.out.println("Movies under each genre for whole data set: ");
			for (String genre : map.keySet()) {
				System.out.printf("(%s,%d) ", genre, map.get(genre).size());
			}
			System.out.println();

			System.out.println("Movies under each genre for most recent 5 years: ");
			for (String genre : map.keySet()) {
				int count = 0;
				for (Movie m : map.get(genre)) {
					if (m.year >= 2015) {
						// only count most recent 5 years
						count++;
					}
				}
				System.out.printf("(%s,%d) ", genre, count);
			}
			System.out.println();

			TreeSet<String> genres = new TreeSet<>(map.keySet());
			for (int year : treemap.keySet()) {
				System.out.print(year + ": ");
				for (String genre : genres) {
					ArrayList<Movie> moviesOfYear = new ArrayList<>(treemap.get(year));
					moviesOfYear.retainAll(map.get(genre)); // keep all the
															// movies of this
															// year that are in
															// the current genre
					System.out.printf("(%s,%d) ", genre, moviesOfYear.size());
				}
				System.out.println();
			}

			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
