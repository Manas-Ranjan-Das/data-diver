document.addEventListener("DOMContentLoaded", () => {
    const pdfFileInput = document.getElementById("pdfFileInput");
    const extractButton = document.getElementById("extractButton");
    const resultSection = document.getElementById("resultSection");
    const metadata = document.getElementById("metadata");
    const imageData = document.getElementById("imageData");

    // Event Listener for the Extract Button
    extractButton.addEventListener("click", async () => {
        const file = pdfFileInput.files[0];
        if (!file) {
            alert("Please select a PDF file.");
            return;
        }

        // Prepare the form data
        const formData = new FormData();
        formData.append("file", file);

        try {
            // Send the file to the backend
            const response = await fetch("http://localhost:8080/files/upload/52", {
                method: "POST",
                body: formData,
            });
            
            if (!response.ok) throw new Error("Failed to extract PDF data");

            const data = await response.json();
            responseData = data;

            console.log(responseData);

            loadAndDisplayData();
            // // Show results section and populate data
            // resultSection.style.display = "block";
            // metadata.innerHTML = data.pdfMetadata ? `<pre>${JSON.stringify(JSON.parse(data.pdfMetadata), null, 4)}</pre>` : "No text found";
            // imageData.innerHTML = data.images
            //     ? data.images.map(img => `<img src="data:image/png;base64,${img}" alt="Extracted Image" />`).join("")
            //     : "No images found";

        } catch (error) {
            console.error("Error:", error);
            alert("An error occurred while processing the PDF.");
        }
    });

    // example response format
    responseData = {
        pdfMetadata: "PDF metadata information goes here.",
        pdfPagesAsImages: [
            { fileName: "page_1.png", contentType: "image/png", content: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA..." },
            { fileName: "page_2.png", contentType: "image/png", content: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA..." }
        ],
        pdfEmbeddedImages: [
            { fileName: "image_1.png", contentType: "image/png", content: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA..." },
            { fileName: "image_2.png", contentType: "image/png", content: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA..." }
        ],
        graphicalElements: ["Line", "Circle", "Rectangle"],
        extractedText: "This is the extracted text from the PDF document."
    };
    
    // Function to render PDF metadata
    function renderPdfMetadata(metadata) {
        const metadataElement = document.getElementById("pdfMetadataContent");
        metadataElement.innerHTML = metadata ? `<pre>${JSON.stringify(JSON.parse(metadata), null, 4)}</pre>` : "No text found";
    }
    
    // Function to render PDF pages as images
    function renderPdfPagesAsImages(images) {
        const imagesContainer = document.getElementById("pagesImagesContent");
        images.forEach(image => {
            const img = document.createElement("img");
            img.src =  `data:${image.contentType};base64,${image.content}`;// Base64 encoded image content
            img.alt = image.fileName;
            imagesContainer.appendChild(img);
        });
    }
    
    // Function to render embedded images
    function renderPdfEmbeddedImages(images) {
        const embeddedImagesContainer = document.getElementById("embeddedImagesContent");
        images.forEach(image => {
            const img = document.createElement("img");
            img.src = `data:${image.contentType};base64,${image.content}`; // Base64 encoded image content
            img.alt = image.fileName;
            embeddedImagesContainer.appendChild(img);
        });
    }
    
    // Function to render graphical elements
    function renderGraphicalElements(elements) {
        const elementsList = document.getElementById("graphicalElementsList");
        elements.forEach(element => {
            const li = document.createElement("li");
            li.textContent = element;
            elementsList.appendChild(li);
        });
    }
    
    // Function to render extracted text
    function renderExtractedText(text) {
        const extractedTextElement = document.getElementById("extractedTextContent");
        extractedTextElement.textContent = text ;
    }
    
    // Main function to load and display the data
    function loadAndDisplayData() {
        renderPdfMetadata(responseData.pdfMetadata);
        renderPdfPagesAsImages(responseData.pdfPagesAsImages);
        renderPdfEmbeddedImages(responseData.pdfEmbeddedImages);
        renderGraphicalElements(responseData.graphicalElements);
        renderExtractedText(responseData.extractedText);
    }
    
    // Call the function to load and display data
    
});
