package com.amazonaws.kevinlai;

/**
 * Kevin Lai
 * 008498282
 * SJSU CMPE 281 Project 2
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.TextDetection;
import com.amazonaws.regions.*;
import java.util.List;

public class LicensePlateParser {

	public static void main(String[] args) throws Exception {

		// Test Case. NOTE: Change this to actual deployed environment.
		String photo = "license/plate-19.jpg";
		String bucket = "cmpe281-project2-kevinlai";
		
		// Get the license plate number from the specified image.
		String finalOutput = detectLicensePlateNumber(bucket, photo);
		
		System.out.println(finalOutput);
		
	}
	
	public static String detectLicensePlateNumber(String bucketName, String filePath) {

		// This is the regular expression used to match license plate numbers.
		String licensePattern = "[A-Z0-9]+";

		// This matcher will be used later in the matching according to the specified regular expression pattern
		Matcher outputMatcher;
		
		// This is to compile the regular expression pattern
		Pattern outputPattern = Pattern.compile(licensePattern);
		
		// This variable will store the license plate number
		String capturedLicensePlateNumber = "";

		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
		
		DetectTextRequest textRequest = new DetectTextRequest()
				.withImage(new Image().withS3Object(new S3Object().withName(filePath).withBucket(bucketName)));

		try {
			DetectTextResult textResult = rekognitionClient.detectText(textRequest);
			List<TextDetection> textDetections = textResult.getTextDetections();
			TextDetection licensePlateNumberText = new TextDetection();
			
			Float textDetectionArea = 0.0f;
			
			// After the loop has completed, only text detection with the largest found bounding box in the entire image will be saved for license plate number processing
			for (TextDetection text : textDetections) {

				// Calculate the area of the bounding boxes for each text detection
				Float newTextArea = text.getGeometry().getBoundingBox().getWidth()
						* text.getGeometry().getBoundingBox().getHeight();

				// Look for the text detection with the largest bounding box
				if (newTextArea > textDetectionArea) {
					// Update each time a larger bounding box text is detected 
					textDetectionArea = newTextArea;
					// Store the text with the current largest found bounding box.
					licensePlateNumberText = text;
				}
			}
			
			if (licensePlateNumberText.getType().equals("LINE")) {
				
				// This is used to remove all whitespaces, including gaps, in each line
				String cleanedLicensePlateNumberText = licensePlateNumberText.getDetectedText().replaceAll(" ", "");
				
				// Apply the matching pattern to each line of text.
				outputMatcher = outputPattern.matcher(cleanedLicensePlateNumberText);
				
				// If the matcher finds a match, then it will enter into this condition.
				if(outputMatcher.find()){
					// Append the matched license plate number to the output string
					capturedLicensePlateNumber += outputMatcher.group(0);
				}
			}
			
			return capturedLicensePlateNumber;
			
		} catch (AmazonRekognitionException e) {
			e.printStackTrace();
		}
		
		// If it fails to catch the exception, then it will just print "ERROR";
		return "ERROR";
	}
	
}
