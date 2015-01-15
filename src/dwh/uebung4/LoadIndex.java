package dwh.uebung4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class LoadIndex {
	Index index;
	
	public boolean load(File input_path){
		boolean successfully_loaded = false;
		try{
			BufferedReader input = new BufferedReader(new InputStreamReader(
					new FileInputStream(input_path)));
			
			
			String[] header = input.readLine().split(";");
			index = new Index(Integer.parseInt(header[0]),Integer.parseInt(header[1]), Integer.parseInt(header[2]),Integer.parseInt(header[3]),Integer.parseInt(header[4]),Integer.parseInt(header[5]),Integer.parseInt(header[6]),Integer.parseInt(header[7]),Integer.parseInt(header[8]));
			
			String key = "";
			HashSet <Integer> valueSet;
			String line;
			while((line = input.readLine()) != null){
				try{
					key = line.split(":")[0];
					valueSet = new HashSet<Integer>();
					
					for(String id : line.split(":")[1].replace("[", "").replace("]","").split(", ")){
						valueSet.add(Integer.parseInt(id));
					}
					index.bulk_insert(key, valueSet);
				}catch(Exception e){
					//ignore
				}
			}
			
			input.close();
			successfully_loaded = true;
		}
		
		catch(IOException e){
			e.printStackTrace();
			System.err.println("Could not load index from "+input_path);
		}
		return successfully_loaded;
	}

}
