const express = require('express');
const router  = express.Router();
const services = require('../services');

//Services instances
const LocationSnapshotService = services.getLocationSnapshotService();
const UserStatusService = services.getUserStatusService();

router.use((req, res, next) => {
  console.log(`[${Date.now()} ${req.method} ${req.path}]`);
  next();
});

router
  .post('/get-status', async (req, res) => {
    try {
      let body = req.body;
      let status = await UserStatusService.getStatus(body.uniqueId);
      body.infected = status.isInfected;
      body.contacted = status.isContacted;
      res.send(body);
    } catch (err) {
      console.log(`[ERROR] ${err.message}`);
      res.status(500).send(req.body);
    }
  })
  .post('/status', async (req, res) => {
    try {
      await UserStatusService.setStatus({
        userId: req.body.uniqueId,
        contacted: req.body.contacted,
        infected: req.body.infected
      });
      res.send(req.body);
    } catch (err) {
      console.log(`[ERROR] ${err.message}`);
      res.status(500).send(req.body);
    }
  })
  .post('/location', async (req, res) => {
    const model = {
      timestamp: new Date().getTime(),
      latitude: req.body.latitude,
      longitude: req.body.longitude,
      userId: req.body.uniqueId
    };

    try {
      await LocationSnapshotService.save(model)
      res.send(req.body);
    } catch (err) {
      console.log(`[ERROR] ${err.message}`);
      res.status(500).send(req.body);
    }
  });

module.exports = {
  use: (app) => {
    app.use('/api', router);
  }
};