import React, { useState, useEffect } from 'react';
import {
  Container,
  Paper,
  Typography,
  List,
  ListItem,
  ListItemText,
  ListItemButton,
  Divider,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

function History() {
  const [documents, setDocuments] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchDocuments = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/documents');
        setDocuments(response.data);
      } catch (error) {
        console.error('Error fetching documents:', error);
      }
    };

    fetchDocuments();
  }, []);

  const handleDocumentClick = (document) => {
    navigate('/summary', { state: { document } });
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Paper sx={{ p: 4 }}>
        <Typography variant="h5" gutterBottom>
          Summary History
        </Typography>
        <Typography variant="body2" color="textSecondary" gutterBottom>
          View and access your previously generated summaries
        </Typography>
        <List sx={{ mt: 2 }}>
          {documents.map((document, index) => (
            <React.Fragment key={document.id}>
              <ListItem disablePadding>
                <ListItemButton onClick={() => handleDocumentClick(document)}>
                  <ListItemText
                    primary={document.title}
                    secondary={`Generated on: ${new Date(document.summaryDate).toLocaleString()}`}
                  />
                </ListItemButton>
              </ListItem>
              {index < documents.length - 1 && <Divider />}
            </React.Fragment>
          ))}
          {documents.length === 0 && (
            <Typography variant="body1" sx={{ mt: 2 }}>
              No summaries available yet.
            </Typography>
          )}
        </List>
      </Paper>
    </Container>
  );
}

export default History; 