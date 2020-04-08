const express = require('express');
const bodyParser = require('body-parser');
const app = express();
const routers = require('./routers');
const port = process.env.PORT || 3000;

// Load app config and database connection provider
const config = require('./config').getConfig();
const db = require('./database');

app.use(bodyParser.json());

app.listen(port, () => {
  console.log(`[API] CoviTrace API starts running on port ${port}...`);
});

// Establish connection to the database
db.connectMongoDB(config);

routers.use(app);