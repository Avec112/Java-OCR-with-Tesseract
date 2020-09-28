# simple-ocr-microservice
Simple OCR microservice with Tesseract, PDFBox and Docker

Cloned from https://github.com/nassiesse/simple-ocr-microservice.git

Documented here https://medium.com/gft-engineering/creating-an-ocr-microservice-using-tesseract-pdfbox-and-docker-155beb7f2623

Add your prefered trained language data from here https://github.com/tesseract-ocr/tessdata 
Remember to add your trained data inside Dockerfile before building image

## Do this

* Install Java 8 or newer
* Install Maven 3
* Install Docker
* Build Microservice with: `mvn clean install`
* Build Docker image: `docker build -t nassiesse/simple-java-ocr .`
* Run Docker image: `docker run -t -i -p 8080:8080 nassiesse/simple-java-ocr`
* Access service: `http://localhost:8080/api/pdf/extractText`. Remember to use `POST`

**Tip!** Use Postman
* Select tab _Body_ 
* Click _form-data_
* In field _Key_ type `file` and choose dropdown value `File` 
* In field _Value_ click _Select Files_ button and add a PDF file 
* Now _Send_ it

### Returned Json
The returned result should contain something like this
```
{
    "fileName": "test.pdf",
    "text": "Your content from pdf\nSeparated with\nBackslash followed with the letter n\n"
}
```
