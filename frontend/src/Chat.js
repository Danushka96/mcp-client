import React, { useState, useEffect } from 'react';

function Chat({ onLogout }) {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [token, setToken] = useState(localStorage.getItem('jwtToken')); // Get token from local storage

  useEffect(() => {
    if (!token) {
      // If token is null, it means user logged out or token expired
      onLogout();
      return;
    }

    const eventSource = new EventSource('http://localhost:8080/sse'); // Assuming backend runs on 8080

    eventSource.onmessage = (event) => {
      const data = JSON.parse(event.data);
      if (data.tool_calls && data.tool_calls.length > 0) {
        // Handle tool calls
        console.log("Tool calls received:", data.tool_calls);
        // Send tool calls back to the backend for execution
        fetch('http://localhost:8080/chat/tool', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({ message: input, toolInvocations: data.tool_calls })
        }).then(response => {
          if (!response.ok) {
            console.error('Error sending tool calls:', response.statusText);
          }
        }).catch(error => {
          console.error('Error sending tool calls:', error);
        });
      } else if (data.content) {
        setMessages(prevMessages => [...prevMessages, { text: data.content, sender: 'ai' }]);
      }
    };

    eventSource.onerror = (error) => {
      console.error('EventSource failed:', error);
      eventSource.close();
      // Optionally, handle token expiration or invalid token here
      if (error.status === 401) { // Example: if server sends 401 on SSE error
        onLogout();
      }
    };

    return () => {
      eventSource.close();
    };
  }, [token, onLogout]);

  const handleSendMessage = async () => {
    if (input.trim() && token) {
      const userMessage = { text: input, sender: 'user' };
      setMessages(prevMessages => [...prevMessages, userMessage]);
      setInput('');

      try {
        const response = await fetch('http://localhost:8080/chat', { // Assuming /chat endpoint for sending messages
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({ message: input })
        });

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        // No need to process response here, as SSE will handle AI responses
      } catch (error) {
        console.error('Error sending message:', error);
        setMessages(prevMessages => [...prevMessages, { text: "Error sending message.", sender: 'ai' }]);
      }
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100vh', padding: '20px' }}>
      <button onClick={onLogout} style={{ position: 'absolute', top: '10px', right: '10px', padding: '8px 12px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}>Logout</button>
      <div style={{ flexGrow: 1, overflowY: 'auto', border: '1px solid #ccc', padding: '10px', marginBottom: '10px' }}>
        {messages.map((msg, index) => (
          <div key={index} style={{ textAlign: msg.sender === 'user' ? 'right' : 'left', margin: '5px 0' }}>
            <span style={{
              backgroundColor: msg.sender === 'user' ? '#dcf8c6' : '#f1f0f0',
              padding: '8px 12px',
              borderRadius: '15px',
              display: 'inline-block'
            }}>
              {msg.text}
            </span>
          </div>
        ))}
      </div>
      <div style={{ display: 'flex' }}>
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
          style={{ flexGrow: 1, padding: '10px', border: '1px solid #ccc', borderRadius: '5px' }}
          placeholder="Type your message..."
        />
        <button
          onClick={handleSendMessage}
          style={{ marginLeft: '10px', padding: '10px 15px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
        >
          Send
        </button>
      </div>
    </div>
  );
}

export default Chat;
