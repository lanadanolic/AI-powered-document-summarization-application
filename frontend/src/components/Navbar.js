import React from 'react';
import { AppBar, Toolbar, Typography, Tabs, Tab } from '@mui/material';
import { useNavigate, useLocation } from 'react-router-dom';

function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();

  const handleChange = (event, newValue) => {
    navigate(newValue);
  };

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 0, marginRight: 4 }}>
          AI Document Summarizer
        </Typography>
        <Tabs
          value={location.pathname}
          onChange={handleChange}
          textColor="inherit"
          indicatorColor="secondary"
        >
          <Tab label="Upload Document" value="/" />
          <Tab label="Summary" value="/summary" />
          <Tab label="History" value="/history" />
        </Tabs>
      </Toolbar>
    </AppBar>
  );
}

export default Navbar; 