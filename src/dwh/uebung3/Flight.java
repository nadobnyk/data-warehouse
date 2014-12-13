package dwh.uebung3;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Flight {
	private int flight_identifier;
	private String origin_of_flight;
	private String destination_of_flight;
	private String aircraft_type;
	private String callsign;
	private ArrayList<Segment> flight_segments;
	
	
	
	public Flight(){
		this.flight_identifier = -1;
		this.origin_of_flight = "";
		this.destination_of_flight = "";
		this.aircraft_type = "";
		this.callsign = "";
		this.flight_segments = new ArrayList<Segment>();
	}
	
	public int getFlight_identifier() {
		return flight_identifier;
	}

	public void setFlight_identifier(int flight_identifier) {
		this.flight_identifier = flight_identifier;
	}

	public String getOrigin_of_flight() {
		return origin_of_flight;
	}

	public void setOrigin_of_flight(String origin_of_flight) {
		this.origin_of_flight = origin_of_flight;
	}

	public String getDestination_of_flight() {
		return destination_of_flight;
	}

	public void setDestination_of_flight(String destination_of_flight) {
		this.destination_of_flight = destination_of_flight;
	}

	public String getAircraft_type() {
		return aircraft_type;
	}

	public void setAircraft_type(String aircraft_type) {
		this.aircraft_type = aircraft_type;
	}

	public String getCallsign() {
		return callsign;
	}

	public void setCallsign(String callsign) {
		this.callsign = callsign;
	}

	public void addSegment(String segment_identifier, String longitute, String latitude, String date, String time, String height){
		flight_segments.add(new Segment(this.flight_identifier, segment_identifier, longitute, latitude, Integer.parseInt(date), Integer.parseInt(time), height));
	}
	
	public void save(BufferedWriter flight_buffer, BufferedWriter segments_buffer){
		if(flight_identifier >0){
			String output = " ('"+flight_identifier+"','"+origin_of_flight+"','"+destination_of_flight+"','"+aircraft_type+"','"+callsign+"')";
			try {
				flight_buffer.write(output+",");
				flight_buffer.newLine();
				//output_buffer.flush();
			} catch (IOException e) {
				System.out.println("Error in writing into output file");
				e.printStackTrace();
			}
			
			Collections.sort(flight_segments, new Comparator<Segment>() {
		        @Override
		        public int compare(Segment p1, Segment p2) {
		            return p1.getTime() - p2.getTime(); // Ascending
		        }

		    });
			
			for(Segment segment : flight_segments){
				segment.save(segments_buffer);
			}
		}
	}

}
