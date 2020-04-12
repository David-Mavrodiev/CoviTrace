const User = require('../models/user.model');

// Status
const NO_STATUS = "none";
const INFECTED_STATUS = "infected";
const CONTACTED_STATUS = "contacted";

class UserStatusService {
  createUser(userId) {
    let user = new User({
      uniqueId: userId,
      status: NO_STATUS,
      contacts: []
    });
    Promise.resolve(user.save());
  }

  getUserById(userId) {
    let query = User.findOne().where({ uniqueId: userId });
    return Promise.resolve(query.exec());
  }

  async setStatus(model) {
    let user = await this.getUserById(model.userId);

    if (user == null) {
      user = await this.createUser(model.userId);
    }

    if (model.contacted) {
      user.status = CONTACTED_STATUS;
    }

    if (model.infected) {
      user.status = INFECTED_STATUS;
    }

    if (!model.contacted && !model.infected) {
      user.status = NO_STATUS;
    }

    return Promise.resolve(user.save());
  }

  async findStatus(userId, depth) {
    let user = await this.getUserById(userId);
    if (depth == 0 || user.status != NO_STATUS) {
      return user.status;
    }
    
    let status = NO_STATUS;
    for (let i = 0; i < user.contacts; i++) {
      const checkUserId = user.contacts[i].userId;
      if (checkUserId == null) {
        continue;
      }
      const checkUserStatus = this.findStatus(checkUserId, depth - 1);
      if (checkUserStatus != NO_STATUS) {
        if (checkUserStatus == INFECTED_STATUS) {
          return INFECTED_STATUS;
        }
        status = CONTACTED_STATUS;
      }
    }

    return status;
  }

  async getStatus(userId) {
    let user = await this.getUserById(userId);
    let recursiveStatus = await this.findStatus(user.uniqueId, 3);

    if (user.status != recursiveStatus) {
      user.status = recursiveStatus;
      user.save();
    }

    return {
      isInfected: user.status == INFECTED_STATUS,
      isContacted: user.status == CONTACTED_STATUS
    }
  }
}

module.exports = UserStatusService;