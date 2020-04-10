const mongoose = require('mongoose');
const Schema = mongoose.Schema;

let user = new Schema({
  uniqueId: {
    type: String,
    trim: true
  },
  status: {
    type: String,
    trim: true
  },
  contacts: [{
    locationSnapshotId: {
      type: String,
      trim: true
    }
  }]
});

module.exports = mongoose.model("User", user);