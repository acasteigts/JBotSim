package examples.basic.icons;

public class MovingNodeBlueOcean extends RandomMovingNode {
    @Override
    public void onStart() {
        super.onStart();
        setIcon("/io/jbotsim/ui/circle-blue-ocean-32x32.png");
    }

}
