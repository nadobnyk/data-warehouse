package dwh.uebung4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BuildIndex {

	private static Index index;
	private static Integer index_dimension_x = 10;
	private static Integer index_dimension_y = 10;
	private static Integer index_dimension_z = 10;

	/**
	 * args[0]: path of index input file args[1]: path of index output file
	 * 
	 * args[2]: number of hash buckets for Time (index_dimension_x) args[3]:
	 * number of hash buckets for Longitude (index_dimension_y) args[4]: number
	 * of hash buckets for Latitude (index_dimension_z)
	 * */
	public static void main(String[] args) {
		if (args.length >= 2) {
			try {
				File input = new File(args[0]);
				File output = new File(args[1]);

				if (args.length == 5) {
					try {
						index_dimension_x = Integer.parseInt(args[2]);
						index_dimension_y = Integer.parseInt(args[3]);
						index_dimension_z = Integer.parseInt(args[4]);
					} catch (Exception e) {
						index_dimension_x = 10;
						index_dimension_y = 10;
						index_dimension_z = 10;
					}
				}

				run(input, output);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			System.err
					.println("Please enter input and output path correctly\n BuildIndex.java [path of index input file] [path of index output file]");
		}

	}

	public static void run(File input, File output) {

			initIndex(input);
			fillIndex(input);
			saveIndex(output);
	}

	private static void fillIndex(File input) {
		System.out.println("Starting to fill index");

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(input)));

			String line = "";
			String[] fields;

			Integer lineCount = 0;

			Integer flightID = 0;

			Integer tupleIdentifier = 0;
			Integer start_time = 0;
			Integer end_time = 0;
			Double start_longitude = 0.0;
			Double end_longitude = 0.0;
			Double start_latitude = 0.0;
			Double end_latitude = 0.0;

			Integer stat_flights_found = 0;

			try {
				while ((line = reader.readLine()) != null) {
					lineCount++;

					fields = line.split(" ");

					if (fields.length == 20) {
						// check if in this line is the beginning of a new
						// flight
						if (flightID != Integer.parseInt(fields[16])) {
						}

						// check if in this line is the beginning of a new
						// flight
						if (flightID != Integer.parseInt(fields[16])) {
							// if this is not the beginning of the algorithm,
							// save
							// the flight
							if (stat_flights_found > 0) {
								tupleIdentifier = lineCount;
							}
							stat_flights_found++;
						}

						// parse Information
						flightID = Integer.parseInt(fields[16]);
						start_time = Integer.parseInt(fields[4]);
						end_time = Integer.parseInt(fields[5]);
						start_longitude = Double.parseDouble(fields[14]);
						end_longitude = Double.parseDouble(fields[15]);
						start_latitude = Double.parseDouble(fields[12]);
						end_latitude = Double.parseDouble(fields[13]);

						index.put(start_time, end_time, start_latitude,
								end_latitude, start_longitude, end_longitude,
								tupleIdentifier);
					}
				}
				reader.close();
			} catch (IOException e) {
				System.err.println("Could not read line " + lineCount
						+ " for flight " + flightID);
				e.printStackTrace();
			}
			System.out.println("Index filled successfully");
			index.printStats();
			
			
		} catch (IOException e) {
			System.err.println("Could not read input file");
			e.printStackTrace();
		}
	}

	private static void initIndex(File input) {

		System.out.println("Starting Index Initialization");

		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(input)));

			Integer lineCount;
			String line;
			String[] fields;

			Map<String, ArrayList<Integer>> longitudes = new HashMap<String, ArrayList<Integer>>();
			Map<String, ArrayList<Integer>> latitudes = new HashMap<String, ArrayList<Integer>>();
			Map<String, ArrayList<Integer>> times = new HashMap<String, ArrayList<Integer>>();

			Integer min_times = 0;
			Double min_latitude = 0.0;
			Double min_longitude = 0.0;
			Integer max_times = 0;
			Double max_latitude = 0.0;
			Double max_longitude = 0.0;

			Integer stats_counter = 0;
			do {
				stats_counter++;

				// clear sets for new iteration
				longitudes.clear();
				latitudes.clear();
				times.clear();

				lineCount = 0;
				try {
					while ((line = reader.readLine()) != null) {
						lineCount++;
						fields = line.split(" ");

						// put initial min/max data from first line
						if (lineCount == 1) {

							// time
							if (Integer.parseInt(fields[4]) < Integer
									.parseInt(fields[5])) {
								min_times = Integer.parseInt(fields[4]);
								max_times = Integer.parseInt(fields[5]);

							} else {
								min_times = Integer.parseInt(fields[5]);
								max_times = Integer.parseInt(fields[4]);
							}

							// latitude
							if (Double.parseDouble(fields[12]) < Double
									.parseDouble(fields[13])) {
								min_latitude = Double.parseDouble(fields[12]);
								max_latitude = Double.parseDouble(fields[13]);

							} else {
								min_latitude = Double.parseDouble(fields[13]);
								max_latitude = Double.parseDouble(fields[12]);
							}

							// longitude
							if (Double.parseDouble(fields[14]) < Double
									.parseDouble(fields[15])) {
								min_longitude = Double.parseDouble(fields[14]);
								max_longitude = Double.parseDouble(fields[15]);

							} else {
								min_longitude = Double.parseDouble(fields[15]);
								max_longitude = Double.parseDouble(fields[14]);
							}
							// else put only if bigger/smaller
						} else {
							// time
							if (Integer.parseInt(fields[4]) < Integer
									.parseInt(fields[5])) {

								if (min_times > Integer.parseInt(fields[4]))
									min_times = Integer.parseInt(fields[4]);
								if (max_times < Integer.parseInt(fields[5]))
									max_times = Integer.parseInt(fields[5]);
							} else {

								if (min_times > Integer.parseInt(fields[5]))
									min_times = Integer.parseInt(fields[5]);
								if (max_times < Integer.parseInt(fields[4]))
									max_times = Integer.parseInt(fields[4]);
							}

							// latitude
							if (Double.parseDouble(fields[12]) < Double
									.parseDouble(fields[13])) {

								if (min_latitude > Double
										.parseDouble(fields[12]))
									min_latitude = Double
											.parseDouble(fields[12]);
								if (max_latitude < Double
										.parseDouble(fields[13]))
									max_latitude = Double
											.parseDouble(fields[13]);

							} else {

								if (min_latitude > Double
										.parseDouble(fields[13]))
									min_latitude = Double
											.parseDouble(fields[13]);
								if (max_latitude < Double
										.parseDouble(fields[12]))
									max_latitude = Double
											.parseDouble(fields[12]);
							}

							// longitude
							if (Double.parseDouble(fields[14]) < Double
									.parseDouble(fields[15])) {

								if (min_longitude > Double
										.parseDouble(fields[14]))
									min_longitude = Double
											.parseDouble(fields[14]);
								if (max_longitude < Double
										.parseDouble(fields[15]))
									max_longitude = Double
											.parseDouble(fields[15]);

							} else {

								if (min_longitude > Double
										.parseDouble(fields[15]))
									min_longitude = Double
											.parseDouble(fields[15]);
								if (max_longitude < Double
										.parseDouble(fields[14]))
									max_longitude = Double
											.parseDouble(fields[14]);

							}
						}
					}
				} catch (IOException e) {
					System.err.println("Could not read line " + lineCount
							+ " to gather statistics about data");
					e.printStackTrace();
				}

				Integer min_x = 0;
				Integer max_x = 0;
				Integer count_x = 0;
				Integer x = 0;
				while (x < times.size()) {
					x++;
					if (min_x > times.get(x).size())
						min_x = times.get(x).size();
					if (max_x < times.get(x).size())
						max_x = times.get(x).size();
					count_x = count_x + times.get(x).size();
				}

				Integer min_y = 0;
				Integer max_y = 0;
				Integer count_y = 0;
				Integer y = 0;
				while (y < longitudes.size()) {
					y++;
					if (min_y > longitudes.get(y).size())
						min_y = longitudes.get(y).size();
					if (max_y < longitudes.get(y).size())
						max_y = longitudes.get(y).size();
					count_y = count_y + times.get(y).size();
				}

				Integer min_z = 0;
				Integer max_z = 0;
				Integer count_z = 0;
				Integer z = 0;
				while (z < latitudes.size()) {
					z++;
					if (min_z > latitudes.get(z).size())
						min_z = latitudes.get(z).size();
					if (max_z < latitudes.get(z).size())
						max_z = latitudes.get(z).size();
					count_z = count_z + latitudes.get(z).size();
				}

			} while (longitudes.size() > index_dimension_x
					&& latitudes.size() > index_dimension_y
					&& times.size() > index_dimension_z);

			System.out
					.println("----Statistics for Index Initialization-----------------------------\n"
							+ "Iterations:\t\t\t"
							+ stats_counter
							+ "\n"
							+ "Tuples:\t\t\t"
							+ lineCount
							+ "\n\n"
							+ "[1] min_times\t\t\t"
							+ min_times
							+ "\n"
							+ "[2] min_latitude\t\t"
							+ min_latitude
							+ "\n"
							+ "[3] min_longitude\t\t"
							+ min_longitude
							+ "\n"
							+ "[4] max_times\t\t\t"
							+ max_times
							+ "\n"
							+ "[5] max_latitude\t\t"
							+ max_latitude
							+ "\n"
							+ "[6] max_longitude\t\t"
							+ max_longitude + "\n");

			reader.close();
			
			index = new Index(index_dimension_x, index_dimension_y,
					index_dimension_z, min_times, max_times,
					(int) (min_longitude + 0), (int) (max_longitude + 1),
					(int) (min_latitude + 0), (int) (max_latitude + 1));
			
		} catch (IOException e) {
			System.err.println("Could not read input file");
			e.printStackTrace();
		}
	}

	private static void saveIndex(File output){
		if(index.save(output)){
			System.out.println("Index saved successfully to "+output);
		}
		else{
			System.err.println("Failed to save index to "+output);	
		}
	}
}
