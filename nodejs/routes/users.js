const express = require('express');
const router = express.Router();

/* GET users listing. */
router.get('/', (req, res, next) => {
  res.send('respond with a resource');
});

/* GET user profile. */
router.get('/profile', (req, res, next) => {
  res.render('profile', { title: 'User Profile' });
});

module.exports = router;
