package dwh.uebung4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;

public class Index {
	private HashMap<String, HashSet<Integer>> cube;

	private Integer dimension_x; // time
	private Integer dimension_y; // Latitude
	private Integer dimension_z; // Longitude

	private Integer minTime;
	private Integer maxTime;

	private Integer minLatitude;
	private Integer maxLatitude;

	private Integer minLongitude;
	private Integer maxLongitude;

	private Integer size_Time;
	private Integer size_Latitude;
	private Integer size_Longitude;
	
	public Index(Integer dimension_x, Integer dimension_y, Integer dimension_z,
			Integer minTime, Integer maxTime, Integer minLongitude,
			Integer maxLongitude, Integer minLatitude, Integer maxLatitude) {

		cube = new HashMap<String, HashSet<Integer>>();
		this.dimension_x = dimension_x;
		this.dimension_y = dimension_y;
		this.dimension_z = dimension_z;

		this.minTime = minTime;
		this.maxTime = maxTime;

		this.minLatitude = minLatitude;
		this.maxLatitude = maxLatitude;

		this.minLongitude = minLongitude;
		this.maxLongitude = maxLongitude;

		this.size_Time = (maxTime - minTime) / dimension_x;
		this.size_Latitude = (maxLatitude - minLatitude) / dimension_y;
		this.size_Longitude = (maxLongitude - minLongitude) / dimension_z;

		System.out
				.println("----Initializing Index------------------------------------\n"
						+ "Size:\t\t\t"
						+ this.dimension_x
						+ " x "
						+ this.dimension_y
						+ " x "
						+ this.dimension_z
						+ "\n"
						+ "Time Range:\t\t"
						+ this.minTime
						+ " - "
						+ this.maxTime
						+ "\t~>bucket size "
						+ this.size_Time
						+ "\n"
						+ "Longitude Range:\t"
						+ this.minLongitude
						+ " - "
						+ this.maxLongitude
						+ "\t~>bucket size "
						+ this.size_Longitude
						+ "\n"
						+ "Latitude Range:\t\t"
						+ this.minLatitude
						+ " - "
						+ this.maxLatitude
						+ "\t~>bucket size " + this.size_Latitude + "\n");
	}

	public void put(Integer start_time, Integer end_time,
			Double start_latitude, Double end_latitude, Double start_longitude,
			Double end_longitude, Integer tupleIdentifier) {

		HashSet<String> idSet = new HashSet<String>();

		// doppelte auswhal, da die graphen nicht gerichtet sind
		for (int x = calculateTimeID(start_time); x <= calculateTimeID(end_time); x++) {
			for (int y = calculateLatitudeID(start_latitude); y <= calculateLatitudeID(end_latitude); y++) {
				for (int z = calculateLongitudeID(start_longitude); z <= calculateLongitudeID(end_longitude); z++) {
					idSet.add(buildID(x, y, z));
				}
			}
		}
		// doppelte auswhal, da die graphen nicht gerichtet sind
		for (int x = calculateTimeID(end_time); x <= calculateTimeID(start_time); x++) {
			for (int y = calculateLatitudeID(end_latitude); y <= calculateLatitudeID(start_latitude); y++) {
				for (int z = calculateLongitudeID(end_longitude); z <= calculateLongitudeID(start_longitude); z++) {
					idSet.add(buildID(x, y, z));
				}
			}
		}

		for (String id : idSet) {
			if (!this.cube.containsKey(id)) {
				this.cube.put(id, new HashSet<Integer>());
			}
			this.cube.get(id).add(tupleIdentifier);
		}
	}

	public HashSet<Integer> get(Integer minTime, Integer maxTime,
			Double minLongitude, Double maxLongitude, Double minLatitude,
			Double maxLatitude) {
		HashSet<Integer> resultSet = new HashSet<Integer>();

		Integer first_timeID = calculateTimeID(minTime);
		Integer first_longitudeID = calculateLongitudeID(minLongitude);
		Integer first_latitudeID = calculateLatitudeID(minLatitude);

		Integer last_timeID = calculateTimeID(maxTime);
		Integer last_longitudeID = calculateLongitudeID(maxLongitude);
		Integer last_latitudeID = calculateLatitudeID(maxLatitude);

		for (int x = first_timeID; x <= last_timeID; x++) {
			for (int y = first_longitudeID; y <= last_longitudeID; y++) {
				for (int z = first_latitudeID; z <= last_latitudeID; z++) {
					resultSet.addAll(this.cube.get(buildID(x, y, z)));
				}
			}
		}
		return resultSet;
	}

	private String buildID(Integer x, Integer y, Integer z) {
		return (x + "-" + y + "-" + z);
	}

	private String calculateID(Integer time, Double longitude, Double latitude) {

		Integer timeID = calculateTimeID(time);
		Integer longitudeID = calculateLongitudeID(longitude);
		Integer latitudeID = calculateLatitudeID(latitude);

		return buildID(timeID, longitudeID, latitudeID);
	}

	private Integer calculateTimeID(Integer value) {
		return ((int) (value - this.minTime) / this.size_Time);
	}

	private Integer calculateLongitudeID(Double value) {
		return ((int) (value - this.minLongitude) / this.size_Longitude);
	}

	private Integer calculateLatitudeID(Double value) {
		return ((int) (value - this.minLatitude) / this.size_Latitude);
	}

	public void printStats() {
		System.out.println("\nIndex Statistics\n");
		for (String key : this.cube.keySet()) {
			System.out.println(key + "\t~>\t" + this.cube.get(key).size()
					+ " entries");
		}
	}
	
	public boolean save(File output_path){
		boolean successfully_saved = false;
		try{
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output_path)));
			
			output.write(dimension_x+";"+dimension_y+";"+dimension_z+";"+minTime+";"+";"+maxTime+";"+minLongitude+";"+maxLongitude+";"+minLatitude+";"+maxLatitude);
			output.newLine();
			output.flush();
			
			for(String key : this.cube.keySet()){
				output.write(key+":"+this.cube.get(key));
				output.newLine();
				output.flush();
			}
			
			output.close();
			successfully_saved = true;
		}
		
		catch(IOException e){
			e.printStackTrace();
			System.err.println("Could not save index to "+output_path);
		}
		return successfully_saved;
	}
	
	public void bulk_insert(String key, HashSet<Integer> valueSet){
		if (!this.cube.containsKey(key)) {
			this.cube.put(key, new HashSet<Integer>());
		}
		this.cube.get(key).addAll(valueSet);
	}

}
