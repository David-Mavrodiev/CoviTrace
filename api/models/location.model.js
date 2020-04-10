const mongoose = require('mongoose');
const Schema = mongoose.Schema;

let locationShapshot = new Schema({
  timestamp: {
    type: Number
  },
  longitude: {
    type: Number
  },
  latitude: {
    type: Number
  },
  userId: {
    type: String,
    trim: true
  }
});

module.exports = mongoose.model("LocationSnapshot", locationShapshot);
