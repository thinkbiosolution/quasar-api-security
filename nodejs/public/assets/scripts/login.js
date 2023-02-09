const express = require('express');
const app = express();

app.get('/login', (req, res) => {
  res.sendFile(__dirname + '/public/login.html');
});

...
