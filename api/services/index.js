//Services classes
const LocationSnapshotService = require('../services/location-snapshot.service');
const UserStatusService = require('../services/user-status.service');

module.exports = {
  getLocationSnapshotService: () => {
    return new LocationSnapshotService();
  },
  getUserStatusService: () => {
    return new UserStatusService();
  }
}