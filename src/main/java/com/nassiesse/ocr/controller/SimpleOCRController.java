package com.nassiesse.ocr.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;


@Slf4j
@RestController
public class SimpleOCRController {

	@PostMapping("/api/pdf/extractText")
    public @ResponseBody ResponseEntity<String> 
					extractTextFromPDFFile(@RequestParam("file") MultipartFile file) {
		// Load file into PDFBox class
		try (PDDocument document = PDDocument.load(file.getBytes())){
			log.debug("Name:\t\t\t{}", file.getName());
			log.debug("OriginalFilename:\t{}", file.getOriginalFilename());
			log.debug("ContentType:\t\t{}", file.getContentType());
			log.debug("Size:\t\t\t{}KB", file.getSize()/1000);


			PDFTextStripper stripper = new PDFTextStripper();
			String strippedText = stripper.getText(document);
			
			// Check text exists into the file
			if (strippedText.trim().isEmpty()){
				log.debug("No text found need to extract (OCR)");
				strippedText = extractTextFromScannedDocument(document);
			}


			JSONObject obj = new JSONObject();
	        obj.put("fileName", file.getOriginalFilename());
	        obj.put("text", strippedText);

	        log.debug("\n---------------\n{}\n---------------\n", strippedText);

	        log.debug("Returning stripped text.");
			return new ResponseEntity<>(obj.toString(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("/api/pdf/ping")
    public ResponseEntity<String> get()
    {
		return new ResponseEntity<>("PONG", HttpStatus.OK);
    }

	private String extractTextFromScannedDocument(PDDocument document) throws IOException, TesseractException {
		
		// Extract images from file
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		StringBuilder out = new StringBuilder();
		
		ITesseract tesseract = new Tesseract();
		tesseract.setDatapath("/usr/share/tessdata/");
		tesseract.setLanguage("nor"); // choose your language
				
		for (int page = 0; page < document.getNumberOfPages(); page++)
		{ 
		    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
		    
		    // Create a temp image file
    	    File temp = File.createTempFile("tempfile_" + page, ".png"); 
    	    ImageIO.write(bim, "png", temp);
	        
    	    String result = tesseract.doOCR(temp);
		    out.append(result);
		
		    // Delete temp file
			//noinspection ResultOfMethodCallIgnored
			temp.delete();
		    
		}
		log.debug("Found {} characters.", out.length());
		return out.toString();

	}
	
	
	
}

