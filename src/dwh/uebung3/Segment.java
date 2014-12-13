package dwh.uebung3;

import java.io.BufferedWriter;
import java.io.IOException;

public class Segment {
	private int flight_identifier=-1;
	private String segment_identifier;
	private String latitude;
	private String longitude;
	private int date;
	private int time;
	private String height;
	
	public Segment(int flight_identifier, String segment_identifier, String longitude, String latitude, int date, int time, String height){
		this.flight_identifier = flight_identifier;
		this.segment_identifier = segment_identifier;
		this.latitude = latitude;
		this.longitude = longitude;
		this.date = date;
		this.time = time;
		this.height = height;
	}
	
	public void save(BufferedWriter output_buffer){
		if(flight_identifier >0){
			String output = " ('"+flight_identifier+"','"+segment_identifier+"','"+longitude+"','"+latitude+"','"+date+"','"+time+"','"+height+"')";
			try {
				output_buffer.write(output+" ,");
				output_buffer.newLine();
				//output_buffer.flush();
			} catch (IOException e) {
				System.out.println("Error in writing into segments output file");
				e.printStackTrace();
			}
		}
		
	}
	
	public int getTime(){
		return this.time;
	}
}
