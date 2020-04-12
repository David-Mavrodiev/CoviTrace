const mongoose = require('mongoose');
const LocationSnapshot = require('../models/location.model');
const UserStatusService = new (require('./user-status.service'))();

const GPS_ERROR_RANGE = 0.001;
const TIME_ERROR_RANGE_MILLISECONDS = 10000;

/**
 * Checks if the the location snapshots are done is simalar time in approximately place
 * @param {*} locA 
 * @param {*} locB 
 */
function compareLocationSnapshots(locA, locB) {
  let isTimeSimilar = false, isLocationSimilar = false;
  if (locA.timestamp - TIME_ERROR_RANGE_MILLISECONDS < locB.timestamp &&
      locA.timestamp + TIME_ERROR_RANGE_MILLISECONDS > locB.timestamp) {
        isTimeSimilar = true;
  }

  if (locA.latitude - GPS_ERROR_RANGE < locB.latitude &&
      locA.latitude + GPS_ERROR_RANGE > locB.latitude &&
      locA.longitude - GPS_ERROR_RANGE < locB.longitude &&
      locA.longitude + GPS_ERROR_RANGE > locB.longitude) {
        isLocationSimilar = true;
  }

  return isTimeSimilar && isLocationSimilar;
}

class LocationSnapshotService {
  async save(model) {
    let locationSnapshot = new LocationSnapshot({
      timestamp: model.timestamp,
      longitude: model.longitude,
      latitude: model.latitude,
      userId: model.userId
    });

    let currentUser = await UserStatusService.getUserById(locationSnapshot.userId);
    if (currentUser == null) {
      UserStatusService.createUser(locationSnapshot.userId);
      return Promise.resolve(locationSnapshot.save());
    }

    // Check if user meets some other user
    // In this case save it
    let snapshots = await this.getAll();
    for (let i = 0; i < snapshots.length; i++) {
      const snapshot = snapshots[i];
      if (snapshot.userId != locationSnapshot.userId && compareLocationSnapshots(locationSnapshot, snapshot)) {
        let user = await UserStatusService.getUserById(snapshot.userId);

        currentUser.contacts.push({
          locationSnapshotId: snapshot._id,
          userId: user.uniqueId
        });
        user.contacts.push({
          locationSnapshotId: locationSnapshot._id,
          userId: currentUser.uniqueId
        });

        currentUser.save();
        user.save();
      }
    }

    return Promise.resolve(locationSnapshot.save());
  }

  /**
   * Get all locations
   */
  getAll() {
    let query = LocationSnapshot.find({});
    return Promise.resolve(query.exec());
  }
}

module.exports = LocationSnapshotService;