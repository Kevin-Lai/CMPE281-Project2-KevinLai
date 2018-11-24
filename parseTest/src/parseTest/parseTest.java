package parseTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Kevin Lai
 * 008498282
 * SJSU CMPE 281 Project 2
 */
public class parseTest {

	public static void main(String[] args) {

		// This is used to locate the line where the detected text is shown
		final String DETECTEDTEXT = "DetectedText";
		
		// This is the regular expression used to match license plate numbers.
		String licensePattern = "\"[A-Z0-9]{7}\"";
		
		// This is to compile the regular expression pattern
		Pattern outputPattern = Pattern.compile(licensePattern);
		
		// This matcher will be used later in the matching according to the specified regular expression pattern
		Matcher outputMatcher;
		
		// This variable will store the license plate number
		String capturedLicensePlateNumber = "";
		
		BufferedReader outputReader;
		try {
			
			/*
			 * NOTE: Currently, this uses a file as an input, but it can easily be changed to incorporate the output from the AWS Reko API output.
			 * This can be any input. It can also be from the AWS Reko API output
			 */
			outputReader = new BufferedReader(new FileReader("Detect_text.json"));
			
			// Variable used to read through each line of text
			String outputLine;
			
			// This will continue to run until there are no more lines to be read
			while( (outputLine = outputReader.readLine()) != null){
				
				// If a line with the label "DetectedText" is found, then we start checking here for the license plate number
				if(outputLine.contains(DETECTEDTEXT)){
					// Apply the matching pattern to each line of text.
					outputMatcher = outputPattern.matcher(outputLine);
					
					// If the matcher finds a match, then it will enter into this condition.
					if(outputMatcher.find()){
						
						// Next, the length of the value is checked to ensure that it fits the length of a license plate.
						if(outputMatcher.group(0).length() == 9){
							
							// Since I was only provided with a single sample, I could only determine that the length of the license plate is 7.
							// NOTE: There may be some licenses that have more or less characters, but that can easily be changed by accommodating those lengths in the condition.
							// Having more samples to test will allow me to refine this more.
							
							// Currently, each detected license plate number is appended to the final string
							// Which will result in a string containing a list of all detected license plate numbers
							capturedLicensePlateNumber += outputMatcher.group(0).replaceAll("\"", "") + "\n";
						}
					}
				}
			}
			// After collecting all valid license plate number matches, print the result to console. NOTE: This can also be changed to accommodate the AWS Reko API.
			System.out.println(capturedLicensePlateNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
