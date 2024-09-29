import java.lang.Math;

public class Entity{

    private double x;
    private double y;
    //vitesse de l'entité
    private int velocity;
    private int size;
    //vecteur qui définit sa direction
    private double dir;

    public Entity(double x, double y, int velocity, int size, double dir){
        this.x = x;
        this.y = y;
        this.velocity = velocity;
        this.size = size;
        this.dir = dir;
    }

    public void moveEntity(){

        //contrôle qu'il ne touche pas les côtés
        if((this.x - this.size < 1 && (this.dir >= 0.5 && this.dir<=1.5)) || (this.x + this.size > 799 && (this.dir <= 0.5 || this.dir>=1.5))){
            this.dir = (3 - this.dir)%2;
        } 
        if((this.y - this.size < 1 && this.dir <= 1) || (this.y + this.size > 799 && this.dir >= 1)){
            this.dir = 2 - this.dir;
        } 

        //fait avancer l'entité dans la direction qu'il contient en fonction de sa vélocité
        this.x += this.velocity * Math.cos(this.dir * Math.PI) / 24;
        this.y += this.velocity * -Math.sin(this.dir * Math.PI) / 24;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Double getDir() {
        return dir;
    }

    public void setDir(double dir) {
        this.dir = dir;
    }
    
}