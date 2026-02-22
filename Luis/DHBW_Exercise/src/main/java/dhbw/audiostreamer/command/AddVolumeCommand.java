package dhbw.audiostreamer.command;

public class AddVolumeCommand {

    private double decibel;

    public AddVolumeCommand() {}

    public double getDecibel() {
        return decibel;
    }

    public void setDecibel(double decibel) {
        if (decibel < 0) {
            throw new IllegalArgumentException("Decibel ist zu niedrig >:( : " + decibel);
        }
        if (decibel > 194) {
            throw new IllegalArgumentException("Deine Ohren explodieren!: " + decibel);
        }
        this.decibel = decibel;
    }
}