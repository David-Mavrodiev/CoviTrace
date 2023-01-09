const mongoose = require("mongoose"),
  chalk = require("chalk");

module.exports = (ENV) => {
  mongoose.Promise = global.Promise;
  mongoose.set("debug", ENV.MONGO_DEBUG);

  let url = `mongodb://${ENV.DBHOST}:${ENV.DBPORT}/${ENV.DBNAME}`;
  mongoose.connection.db;
  /*create mongoDB connection*/
  mongoose.connect(url, { useNewUrlParser: true });

  /*if if connection established*/
  mongoose.connection.on("connected", () => {
    console.log(
      chalk`{green Successfully connected to mongoDB {green.bold ${ENV.DBNAME}}}`
    );
  });

  /*if unable to connect to DB*/
  mongoose.connection.on("error", (err) => {
    console.error(
      chalk`{red Failed to connect to mongoDB: {red.bold ${ENV.DBNAME}, ${err}}}`
    );
  });

  /*if connection has been break due to any reason*/
  mongoose.connection.on("disconnected", (err) => {
    console.log(
      chalk`{red Default connection to mongoDB: {red.bold ${ENV.DBNAME}} disconnected}`
    );
  });
};
