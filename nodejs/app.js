const express = require('express');
const passport = require('passport');
const LocalStrategy = require('passport-local').Strategy;
const mongoose = require('mongoose');
const bcrypt = require('bcrypt');
const port = process.env.PORT || 3000;

const indexRouter = require('./routes/index');
const usersRouter = require('./routes/users');
const productsRouter = require('./routes/products');

const app = express();

app.use('/', indexRouter);
app.use('/users', usersRouter);
app.use('/products', productsRouter);

// Connect to the MongoDB database
mongoose.connect('mongodb://localhost:27017/mydb', { useNewUrlParser: true });

// User Schema
const userSchema = new mongoose.Schema({
  username: String,
  password: String,
  role: String
});

const User = mongoose.model('User', userSchema);

// Passport Configuration
passport.use(new LocalStrategy(
  function(username, password, done) {
    User.findOne({ username: username }, function (err, user) {
      if (err) { return done(err); }
      if (!user) { return done(null, false); }

      bcrypt.compare(password, user.password, function(err, result) {
        if (result) {
          return done(null, user);
        } else {
          return done(null, false);
        }
      });
    });
  }
));

passport.serializeUser(function(user, done) {
  done(null, user.id);
});

passport.deserializeUser(function(id, done) {
  User.findById(id, function(err, user) {
    done(err, user);
  });
});

// Login Route
app.post('/login', 
  passport.authenticate('local', { failureRedirect: '/login' }),
  function(req, res) {
    // On successful login, redirect to the protected API endpoint
    res.redirect('/api');
  }
);

// API Endpoint (Protected)
app.get('/api', 
  function(req, res) {
    // Check if the user is authenticated
    if (!req.user) {
      return res.status(401).send({ error: 'Unauthorized' });
    }

    // Check if the user has the correct permissions
    if (req.user.role !== 'admin') {
      return res.status(403).send({ error: 'Forbidden' });
    }

    // Example of an API endpoint that requires authentication and admin permissions
    res.send({ data: 'secret information' });
  }
);

// Start the server
app.listen(port, () => {
  console.log(`Server is listening on port ${port}`);
});
