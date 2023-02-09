const express = require('express');
const router = express.Router();

/* GET product listing. */
router.get('/', (req, res, next) => {
  res.send('Product Listing');
});

/* GET product detail. */
router.get('/:id', (req, res, next) => {
  res.send(`Product Detail: ${req.params.id}`);
});

module.exports = router;
