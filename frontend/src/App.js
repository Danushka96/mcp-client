import React, { useState, useEffect } from 'react';
import './App.css';
import Chat from './Chat';
import Login from './Login';

function App() {
  const [token, setToken] = useState(localStorage.getItem('jwtToken'));

  const handleLogin = (newToken) => {
    setToken(newToken);
  };

  const handleLogout = () => {
    localStorage.removeItem('jwtToken');
    setToken(null);
  };

  return (
    <div className="App">
      {token ? (
        <Chat onLogout={handleLogout} />
      ) : (
        <Login onLogin={handleLogin} />
      )}
    </div>
  );
}

export default App;