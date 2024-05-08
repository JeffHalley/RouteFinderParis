package models;

public class POI {

    private String name;
    private int xCoord;
    private int yCoord;
    private boolean isLandmark;

    public POI(String name, int xCoord, int yCoord, Boolean isLandmark) {
        this.name = name;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.isLandmark = isLandmark;

    }

    public String getname() {
        return name;
    }

    public int getXCoord() {
        return xCoord;
    }

    public int getYCoord(){
        return yCoord;
    }

    public void setname(String name) {
        this.name = name;
    }


    public int getxCoord() {
        return xCoord;
    }

    public void setxCoord(int xCoord) {
        this.xCoord = xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public void setyCoord(int yCoord) {
        this.yCoord = yCoord;
    }

    public boolean isLandmark() {
        return isLandmark;
    }

    public void setLandmark(boolean landmark) {
        isLandmark = landmark;
    }

    @Override
    public String toString() {
        return "POI{" +
                "name='" + name + '\'' +
                ", xCoord=" + xCoord +
                ", yCoord=" + yCoord +
                ", isLandmark=" + isLandmark +
                '}';
    }
}
