import java.util.ArrayList;
import java.util.HashMap;

public class Fugitive extends Entity {

    private HashMap<String, Double> genes;
    private int life;
    private int timeReflect;
    private boolean caught;

    public Fugitive(double x, double y, int velocity, int size, double dir, HashMap<String, Double> genes) {
        super(x, y, velocity, size, dir);
        this.genes = genes;
        this.life = 2;
        this.caught = false;
    }

    public void chooseDirection(Evolution ev){
        //on regarde si il peut changer de direction en fonction de ses gènes en smart
        if(ev.getNbTime() % Math.round(150 / (this.genes.get("smart")+ 10)) == 0){

            int[] coord = Evolution.theNearestBonus(this, ev.getBonusLife());
            double angleDist;

            if(this.getLife() < 5 && Evolution.distance2point(this.getX(), coord[1], coord[0], this.getY()) < this.genes.get("explorer") * 6 + 10){
                angleDist = Math.atan2(coord[1] - this.getY(), this.getX() - coord[0]) / Math.PI;

                if(angleDist < 0){
                    angleDist += 2;
                }

                this.setDir((angleDist+1)%2);
            }

            Catcher catcher = Evolution.theNearestCatcher(this, ev.getListCatchers());
            if(Evolution.distance2point(catcher.getX(), catcher.getY(), this.getX(), this.getY()) < this.genes.get("fugitive") * 5 + 20){
                angleDist = Math.atan2(catcher.getY() - this.getY(), this.getX() - catcher.getX()) / Math.PI;
                if(angleDist < 0){
                    angleDist += 2;
                }
                this.setDir(Evolution.theFarthestAngle((angleDist+1)%2, catcher.getDir()));
            }

        }
    }

    public HashMap<String, Double> getGenes() {
        return genes;
    }

    public void setGenes(HashMap<String, Double> genes) {
        this.genes = genes;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getTimeReflect() {
        return timeReflect;
    }

    public void setTimeReflect(int timeReflect) {
        this.timeReflect = timeReflect;
    }

    public boolean isCaught() {
        return caught;
    }

    public void setCaught(boolean caught) {
        this.caught = caught;
    }
}