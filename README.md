# data-diver
Dive into files, surface all their data. This tool enables an user to provide a file (PDF, Audio, Video etc) and get all the information in the file ( image, text, metadata etc )

Note : Current itteration of the project works only on pdf files.

This tool is designed to to accept pdf files and dig through them to find data like images, links, metadata, removed data etc.

## Deployment
To deploy this Spring Boot application, follow these steps:

Clone the repository:

```bash
git clone https://github.com/Manas-Ranjan-Das/file-diver.git
cd file-diver
```

Ensure Java and Maven are installed:

Java: Version 17.0.3 or higher.

Maven: Version 3.9.9 or higher.

Install dependencies: Run the following command to download all required dependencies:


```bash
mvn clean install
```

Run the application: Execute the following command to start the application:

```bash
mvn spring-boot:run
```

Access the application: By default, the application will be available at http://localhost:8080.


## How to Use
On accessing the application at http://localhost:8080 you will see an html page with prompt to chose a pdf file and an "Extract Data" button .
After chosing the file from you local device and clicking the button, the extracted data will be arranged in sections such as Embedded Images, PDF Metadata, Extracted Text etc. 






