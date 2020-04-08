const express = require('express');
const router  = express.Router();
const services = require('../services');

//Services instances
const InfectionLocatorService = services.getInfectionLocatorService();

router.use((req, res, next) => {
  console.log(`[${Date.now()} ${req.method} ${req.path}]`);
  next();
});

router
  .get('/status', (req, res) => {
    InfectionLocatorService.getContactedPersons();
    // TODO get the user status
    res.send({
      success: true
    });
  })
  .post('/status', (req, res) => {
    console.log(req.body);
    // TODO save the user status
    res.send(req.body);
  })
  .post('/location', (req, res) => {
    // TODO save the user location
    res.send(req.body);
  });

module.exports = {
  use: (app) => {
    app.use('/api', router);
  }
};