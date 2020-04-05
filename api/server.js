const express = require('express');
const app = express();
const routers = require('./routers');
const port = 3000;

app.listen(port, () => {
  console.log(`[API] CoviTrace API starts running on port ${port}...`);
});

routers.use(app);