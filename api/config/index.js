const path = require('path');

module.exports = {
  getConfig: () => {
    const pathToConfig = path.resolve(`./config/config.${process.env.NODE_ENV || 'production'}.json`);
    return require(pathToConfig);
  }
};