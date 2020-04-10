const mongoose = require('mongoose');
const LocationSnapshot = require('../models/location.model');
const User = require('../models/user.model');

const GPS_ERROR_RANGE = 0.0000001;
const TIME_ERROR_RANGE_MILLISECONDS = 100;
const NO_STATUS = "none";

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

    let currentUser;
    if (currentUser == null) {
      currentUser = new User({
        uniqueId: model.userId,
        status: NO_STATUS,
        contacts: []
      });
      currentUser.save();
      return Promise.resolve(locationSnapshot.save());
    }
    currentUser = User.findOne().where({ _id: locationSnapshot.userId }).exec();

    // Check if user meets some other user
    // In this case save it
    let snapshots = await this.getAll();
    for (let i = 0; i < snapshots.length; i++) {
      const snapshot = snapshots[i];
      if (snapshot.userId != locationSnapshot.userId && compareLocationSnapshots(locationSnapshot, snapshot)) {
        console.log("[LOG] Found location snapshots coincidence...");
        let user = await User.findOne({ _id: snapshot.userId });

        currentUser.contacts.push({
          locationSnapshotId: snapshot._id
        });
        user.contacts.push({
          locationSnapshotId: locationSnapshot._id
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