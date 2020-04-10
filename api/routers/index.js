const express = require('express');
const router  = express.Router();
const services = require('../services');

//Services instances
const LocationSnapshotService = services.getLocationSnapshotService();

router.use((req, res, next) => {
  console.log(`[${Date.now()} ${req.method} ${req.path}]`);
  next();
});

router
  .get('/status', (req, res) => {
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
  .post('/location', async (req, res) => {
    // Create location model
    const model = {
      timestamp: new Date().getTime(),
      latitude: req.body.latitude,
      longitude: req.body.longitude,
      userId: req.body.uniqueId
    };
    await LocationSnapshotService.save(model)
    /*  .then(() => {
        res.send(req.body);
      }).catch(err => {
        res.status(500).send(req.body);
      });*/
  });

module.exports = {
  use: (app) => {
    app.use('/api', router);
  }
};