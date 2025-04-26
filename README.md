# Document Summarizer

A full-stack web application that allows users to upload documents and generate AI-powered summaries. The application provides functionality to download summaries in both TXT and DOCX formats.

## Features

- Document upload and processing
- AI-powered document summarization using OpenRouter API
- Modern, responsive user interface
- Download summaries in TXT or DOCX format
- Document history and management
- Cross-platform compatibility

## Tech Stack

### Backend
- Java 17
- Spring Boot
- PostgreSQL
- Maven
- OpenRouter API for AI summarization
- Apache POI for DOCX handling

### Frontend
- React
- Material-UI (MUI)
- Axios for API calls
- React Router for navigation

## Prerequisites

Before you begin, ensure you have the following installed:
- Java Development Kit (JDK) 17 or later
- Node.js (v14 or later) and npm
- PostgreSQL 12 or later
- Maven 3.6 or later
- OpenRouter API key (get it from https://openrouter.ai/)

## Installation & Setup

### 1. Database Setup
```bash
# Create PostgreSQL database
psql -U postgres
CREATE DATABASE document_summarizer;
```

### 2. Backend Setup
```bash
# Clone the repository
git clone [your-repository-url]
cd [repository-name]

# Navigate to backend directory
cd backend

# Copy the application.properties template
cp src/main/resources/application.properties.template src/main/resources/application.properties

# Edit application.properties and set your database and API credentials
# Update the following properties:
# - spring.datasource.password=your_database_password
# - openrouter.api.key=your_openrouter_api_key

# Install dependencies
mvn clean install
```

### 3. Frontend Setup
```bash
# Navigate to frontend directory
cd ../frontend

# Install dependencies
npm install

# Create .env file (if needed)
echo "REACT_APP_API_URL=http://localhost:8080" > .env
```

## Environment Setup

### Securing API Keys and Sensitive Data

This project uses environment-specific configuration files to protect sensitive data. Follow these steps to set up your environment:

1. Backend Configuration:
   - Copy `application.properties.template` to `application.properties`:
     ```bash
     cp backend/src/main/resources/application.properties.template backend/src/main/resources/application.properties
     ```
   - Edit `application.properties` with your actual values:
     - Database password
     - OpenRouter API key
     - Any other environment-specific settings

2. Never commit sensitive data:
   - The `.gitignore` file is configured to exclude sensitive files
   - Always use the template file as a reference
   - Keep your API keys and passwords secure

3. For deployment:
   - Use environment variables or secure secrets management
   - Consider using Spring Cloud Config Server for larger deployments
   - Follow your hosting provider's security best practices

## Running the Application

### 1. Start the Backend
```bash
# From the backend directory
mvn spring-boot:run
```
The backend will start on http://localhost:8080

### 2. Start the Frontend
```bash
# From the frontend directory
npm start
```
The frontend will start on http://localhost:3000

## Usage

1. Open your browser and navigate to http://localhost:3000
2. Click on "Upload Document" to submit a new document
3. Enter a title for your document and select the file to upload
4. Wait for the AI to generate the summary
5. View the generated summary
6. Download the summary in either TXT or DOCX format

## API Endpoints

- `POST /api/documents/upload` - Upload a new document
- `GET /api/documents` - Get all documents
- `GET /api/documents/{id}` - Get a specific document
- `GET /api/documents/{id}/download/txt` - Download summary as TXT
- `GET /api/documents/{id}/download/docx` - Download summary as DOCX

## Configuration

### Backend Configuration Options
- `spring.servlet.multipart.max-file-size`: Maximum file size for uploads (default: 10MB)
- `spring.servlet.multipart.max-request-size`: Maximum request size (default: 10MB)
- `openrouter.api.key`: Your OpenRouter API key for AI summarization

### Frontend Configuration
- `REACT_APP_API_URL`: Backend API URL (default: http://localhost:8080)

## Troubleshooting

1. **Backend won't start**
   - Check if PostgreSQL is running
   - Verify database credentials in application.properties
   - Ensure port 8080 is not in use

2. **Frontend won't start**
   - Check if Node.js is installed correctly
   - Clear npm cache: `npm cache clean --force`
   - Delete node_modules and run `npm install` again

3. **Download not working**
   - Check browser console for errors
   - Verify CORS settings in backend
   - Ensure backend is running and accessible

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

![Screenshot from 2025-04-24 15-09-38](https://github.com/user-attachments/assets/0dd2bbc3-2866-4647-9e60-296ac206d6da)

![Screenshot from 2025-04-24 15-11-55](https://github.com/user-attachments/assets/2c99aa57-b06c-4b07-a2e9-c416dfe4d789)

![Screenshot from 2025-04-24 15-12-48](https://github.com/user-attachments/assets/19ceeea9-06e3-4107-9b05-eebfa624c600)

![Screenshot from 2025-04-24 15-13-27](https://github.com/user-attachments/assets/7c337a8d-3a28-4c57-a718-7f553ec0ac65)



