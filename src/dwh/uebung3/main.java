package dwh.uebung3; 

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class main {
	
	private static boolean debug = true;

	private static File input;
	private static File flights_output;
	private static File segments_output;
	
	private static int stats_flightscount = 0;
	private static long stats_extract_time = 0;
	
	
	
	//String[] args:{[0] file_input_path ; [1] file_output_path}
	public static void main(String[] args) {
		long starttime = System.currentTimeMillis();
		
		try{
			input = new File(args[0]);
			System.out.println("Input file: "+input.getAbsolutePath());			
		}
		catch(Exception e){
			System.out.println("Error with input file: "+args[0]);
			e.printStackTrace();
			return;
		}
		
		try{
			flights_output = new File(args[1]+".flights");
			System.out.println("Flights file: "+flights_output.getAbsolutePath());
			segments_output = new File(args[1]+".segments");
			System.out.println("Segments file: "+segments_output.getAbsolutePath());
		}
		catch(Exception e){
			System.out.println("Error with output file: "+args[1]);
			e.printStackTrace();
			return;
		}
		
		if(input != null && flights_output != null && segments_output != null){
			etl(input, flights_output, segments_output);
		}
		
		System.out.println("\n-----------------\nTotal:\t"+(System.currentTimeMillis()-starttime)+"millisecounds\nExtract:\t"+stats_extract_time+"millisecounds\nFlights:\t"+stats_flightscount);
	}
	
	private static void etl(File input, File flights_output, File segments_output){
		
		InputStreamReader inputStreamReader;
		BufferedReader bufferedReader;
		try {
			inputStreamReader = new InputStreamReader(new FileInputStream (input));
			bufferedReader = new BufferedReader (inputStreamReader);
		} catch (FileNotFoundException e) {
			System.out.println("Error in reading input file");
			e.printStackTrace();
			return;
		}
		
		OutputStreamWriter flights_outputStreamWriter;
		BufferedWriter flights_bufferedWriter;
		try {
			flights_outputStreamWriter = new OutputStreamWriter(new FileOutputStream (flights_output));
			flights_bufferedWriter = new BufferedWriter (flights_outputStreamWriter);
		} catch (FileNotFoundException e) {
			System.out.println("Error in opening flights output file");
			e.printStackTrace();
			return;
		}
		
		OutputStreamWriter segments_outputStreamWriter;
		BufferedWriter segments_bufferedWriter;
		try {
			segments_outputStreamWriter = new OutputStreamWriter(new FileOutputStream (segments_output));
			segments_bufferedWriter = new BufferedWriter (segments_outputStreamWriter);
		} catch (FileNotFoundException e) {
			System.out.println("Error in opening segments output file");
			e.printStackTrace();
			return;
		}
		
		extract(bufferedReader, flights_bufferedWriter, segments_bufferedWriter);
		
	}
	
	private static void extract(BufferedReader file_reader, BufferedWriter flights_writer, BufferedWriter segments_writer){
		
		//init
		
		//String output = " ('"+flight_identifier+"','"+origin_of_flight+"','"+destination_of_flight+"','"+aircraft_type+"','"+callsign+"')";
		try {
			flights_writer.write("INSERT INTO 'flights' (flight_identifier, origin_of_flight, destination_of_flight, aircraft_type, callsign) VALUES ");
			flights_writer.newLine();
			flights_writer.flush();
			//String output = " ('"+flight_identifier+"','"+segment_identifier+"','"+latitude+"','"+longitude+"','"+date+"','"+time+"','"+height+"')";
			segments_writer.write("INSERT INTO 'segments' (flight_identifier, segment_identifier, longitude, latitude, date, time, height) VALUES ");
			segments_writer.newLine();
			segments_writer.flush();
		} catch (IOException e1) {
			System.out.println("Could not initialize flights and segment files");
			e1.printStackTrace();
		}
		
		
		long starttime = System.currentTimeMillis();
		
		String line;
		String[] fields;
		
		Flight current_flight = new Flight();
		ArrayList<String> segment_IDs = new ArrayList();
			
		
		
		int flight_id = -1;
		
		try {
			while ((line = file_reader.readLine()) != null){
				fields = line.split(" ");
				if(flight_id != Integer.parseInt(fields[16])){
					flight_id = Integer.parseInt(fields[16]);
					current_flight.save(flights_writer, segments_writer);
					current_flight = new Flight();
					segment_IDs.clear();
					
					stats_flightscount++;
					
					current_flight.setFlight_identifier(flight_id);
					current_flight.setAircraft_type(fields[3]);
					current_flight.setCallsign(fields[9]);
					current_flight.setOrigin_of_flight(fields[1]);
					current_flight.setDestination_of_flight(fields[2]);
					
					if(debug){
						segments_writer.newLine();
					}
				}
				
				//String segment_identifier, String longitute, String latitude, String date, String time, String height				
				if(!segment_IDs.contains(fields[0].split("_")[0])){
					current_flight.addSegment(fields[0].split("_")[1], fields[12], fields[13], fields[10], fields[4], fields[6]);
					segment_IDs.add(fields[0].split("_")[0]);
				}
				if(!segment_IDs.contains(fields[0].split("_")[1])){
					current_flight.addSegment(fields[0].split("_")[1], fields[14], fields[15], fields[11], fields[5], fields[7]);
					segment_IDs.add(fields[0].split("_")[1]);
				}
				
				
				
				if(debug){
					System.out.println(stats_flightscount+"\t"+line);
					if(stats_flightscount == 300){
						break;
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Error in reading line form input file");
			e.printStackTrace();
		}
		
		try {
			file_reader.close();
			flights_writer.write(";");
			flights_writer.close();
			segments_writer.write(";");
			segments_writer.close();
		} catch (IOException e) {
			System.out.println("Could not close file streams");
			e.printStackTrace();
		}
		
		stats_extract_time = System.currentTimeMillis()-starttime;
	}

}
