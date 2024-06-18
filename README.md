# Table-Task-Core-Java
UniSoftTableTaskApplication

## Description
UniSoftTableTaskApplication is a Java Swing application that utilizes a `JTable` to manage and display data. 
The application allows users to perform various operations on the table, including adding, deleting, 
saving to a file, loading from a file, exporting to PDF, and printing the table. 
It is built using Java Swing for the GUI and integrates essential libraries for JSON processing and PDF generation.

## Features
- **Display a table with three columns**:
  - First column: Text data, left-aligned.
  - Second column: Text data, centered.
  - Third column: Floating-point numbers (rounded to two decimal places), right-aligned.
- **Add and delete rows** in the table.
- **Edit cells** directly in the table.
- **Alternating row colors** for better readability.
- **Save table data to a JSON file**.
- **Load table data from a JSON file**.
- **Export table data to a PDF file**.
- **Print the table** directly from the application.

## Prerequisites
- Java 17 or higher
- Maven

## Dependencies
- Jackson (for JSON processing)
- iText (for PDF generation)
- SLF4J (for logging)

## Getting Started

### Building the Project

Using Maven:
```bash
mvn clean install
```

### Running the Project

Using Maven:
```bash
mvn exec:java -Dexec.mainClass="com.example.Main"
```

## Usage

Upon running the application, a window will appear displaying the table. 
Users can interact with the table using the buttons provided at the bottom of the window:

- **Add Row**: Adds a new row to the table.
- **Delete Row**: Deletes the selected row from the table.
- **Save to File**: Saves the table data to a JSON file.
- **Load from File**: Loads table data from a JSON file.
- **Export to PDF**: Exports the table data to a PDF file.
- **Print**: Prints the table.
