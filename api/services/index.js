//Data providers classes
const LocationDataProviderClass = require('../data/locations.data');

//Services classes
const InfectionLocatorServiceClass = require('../services/infection-locator.service');

module.exports = {
  getInfectionLocatorService: () => {
    return new InfectionLocatorServiceClass(new LocationDataProviderClass());
  }
}