package com.covitrack.david.covitrack.api.models.responses;

// TODO add more detailed status
public class StatusResponseModel extends BaseResponseModel {
    private boolean hasInfectionProbability;

    public StatusResponseModel(boolean hasInfectionProbability) {
        this.hasInfectionProbability = hasInfectionProbability;
    }

    public boolean isHasInfectionProbability() {
        return hasInfectionProbability;
    }

    public void setHasInfectionProbability(boolean hasInfectionProbability) {
        this.hasInfectionProbability = hasInfectionProbability;
    }
}
