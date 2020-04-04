package com.covitrack.david.covitrack.api.models.requests;

public class StatusRequestModel extends BaseRequestModel {
    private boolean contacted;
    private boolean infected;

    public boolean isContacted() {
        return contacted;
    }

    public void setContacted(boolean contacted) {
        this.contacted = contacted;
    }

    public boolean isInfected() {
        return infected;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }
}
