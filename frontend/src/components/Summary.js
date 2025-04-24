import React, { useState, useEffect } from 'react';
import { Container, Paper, Typography, Box, Button, Stack, Snackbar, Alert } from '@mui/material';
import { useLocation, Navigate } from 'react-router-dom';
import DownloadIcon from '@mui/icons-material/Download';
import axios from 'axios';

function Summary() {
  const location = useLocation();
  const documentData = location.state?.document;
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // Log document data when component mounts
    if (documentData) {
      console.log('Document data:', {
        id: documentData.id,
        title: documentData.title,
        summaryDate: documentData.summaryDate
      });
    }
  }, [documentData]);

  if (!documentData) {
    return <Navigate to="/" replace />;
  }

  const handleDownload = async (format) => {
    try {
      setLoading(true);
      setError(null);

      console.log('Starting download...', {
        documentId: documentData.id,
        format: format,
        documentTitle: documentData.title
      });

      const url = `http://localhost:8080/api/documents/${documentData.id}/download/${format}`;
      console.log('Requesting URL:', url);

      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Accept': format === 'txt' ? 'text/plain' : 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const blob = await response.blob();
      console.log('Response blob:', {
        size: blob.size,
        type: blob.type
      });

      // Create download
      const downloadUrl = window.URL.createObjectURL(blob);
      const filename = `${documentData.title}_summary.${format}`;
      
      const link = window.document.createElement('a');
      link.style.display = 'none';
      link.href = downloadUrl;
      link.download = filename;
      
      window.document.body.appendChild(link);
      link.click();
      
      // Cleanup
      window.URL.revokeObjectURL(downloadUrl);
      window.document.body.removeChild(link);
      
      setLoading(false);
      console.log('Download completed successfully');
      
    } catch (error) {
      console.error('Download error:', error);
      setError(`Error downloading file: ${error.message}`);
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Paper sx={{ p: 4 }}>
        <Typography variant="h5" gutterBottom>
          Summary
        </Typography>
        <Typography variant="body2" color="textSecondary" gutterBottom>
          Generated summary of your document
        </Typography>
        {documentData && (
          <Box sx={{ mt: 3 }}>
            <Typography variant="h6" gutterBottom>
              {documentData.title}
            </Typography>
            <Typography variant="body1" paragraph>
              {documentData.summary}
            </Typography>
            <Typography variant="body2" color="textSecondary" gutterBottom>
              Generated on: {new Date(documentData.summaryDate).toLocaleString()}
            </Typography>
            <Typography variant="body2" color="textSecondary" gutterBottom>
              Document ID: {documentData.id}
            </Typography>
            
            <Stack direction="row" spacing={2} sx={{ mt: 3 }}>
              <Button
                variant="contained"
                startIcon={<DownloadIcon />}
                onClick={() => handleDownload('txt')}
                color="primary"
                disabled={loading}
              >
                {loading ? 'Downloading...' : 'Download as TXT'}
              </Button>
              <Button
                variant="contained"
                startIcon={<DownloadIcon />}
                onClick={() => handleDownload('docx')}
                color="secondary"
                disabled={loading}
              >
                {loading ? 'Downloading...' : 'Download as DOCX'}
              </Button>
            </Stack>
          </Box>
        )}
        {!documentData && (
          <Typography variant="body1" sx={{ mt: 2 }}>
            No summary available. Please upload a document first.
          </Typography>
        )}
      </Paper>

      <Snackbar 
        open={!!error} 
        autoHideDuration={6000} 
        onClose={() => setError(null)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert onClose={() => setError(null)} severity="error" sx={{ width: '100%' }}>
          {error}
        </Alert>
      </Snackbar>
    </Container>
  );
}

export default Summary; 