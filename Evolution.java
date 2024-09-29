import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Evolution {

    private static final int NB_CATCHERS = 18;
    private static final int NB_FUGITIVES = 10;
    private int nbBonus = 1;
    private static final int NB_GEN_TRAIN = 100;
    private ArrayList<Fugitive> listFugitives;
    private ArrayList<Fugitive> listFinisher;
    private ArrayList<Catcher> listCatchers;
    private int wave;
    private ArrayList<int[]> bonusLife;
    private int nbTime;

    Random rand = new Random();

    public Evolution(){
        this.wave = 0;
        this.listFugitives = createFirstFugitives(NB_FUGITIVES);
        this.listFinisher = new ArrayList<Fugitive>();
        this.listCatchers = createCatchers(NB_CATCHERS);
        this.bonusLife = createFirstBonus();
        this.nbTime = 0;
    }

    public Evolution(ArrayList<Fugitive> listFugitives){
        this.wave = 0;
        this.listFugitives = listFugitives;
        this.listFinisher = new ArrayList<Fugitive>();
        this.listCatchers = createCatchers(NB_CATCHERS);
        this.bonusLife = createFirstBonus();
        this.nbTime = 0;
    }

    //créer les fugitifs de départ ayant un gènes à 100 et le reste à 0
    public ArrayList<Fugitive> createFirstFugitives(int nbFugitives){
        //liste des différents fugitifs
        ArrayList<Fugitive> listFugitives = new ArrayList<Fugitive>();

        //liste des différents gènes
        String[] genes = new String[] {"fugitive", "explorer", "rapidity", "size", "smart"};

        //map avec tous les gènes à 0
        HashMap<String, Double> genesNull = new HashMap<String, Double>();
        HashMap<String, Double> copyGene;
        for(String g : genes){
            genesNull.put(g, 0.0);
        }

        for(int i = 0; i < nbFugitives; i++){
            //distribue les gènes pour chaque fugitif
            copyGene = new HashMap<>(genesNull);
            copyGene.replace(genes[i%genes.length], 100.0);

            //ajout du ième fugitif dans la liste
            listFugitives.add(new Fugitive(rand.nextDouble(50, 750), rand.nextDouble(50, 750), (int) (1+copyGene.get("rapidity")), (int) (22 - copyGene.get("size") / 6), rand.nextDouble(2), copyGene));
        }
        return listFugitives;
    }

    public ArrayList<Catcher> createCatchers(int nbCatchers){
        //liste des différents fugitifs
        ArrayList<Catcher> listCatchers = new ArrayList<Catcher>();

        for(int i = 0; i < nbCatchers; i++){
            listCatchers.add(new Catcher(rand.nextInt(50, 750), rand.nextInt(50, 750), 22, 6, rand.nextDouble(2)));
        }

        return listCatchers;
    }

    //créer une nouvelle génération en fonction desancêtres
    public ArrayList<Fugitive> createNewFugitives(){

        //ancienne population
        ArrayList<Fugitive> listFinisher = this.getListFinisher();
        Fugitive bestFinisher = listFinisher.get(listFinisher.size()-1);
        //liste des différents fugitifs
        ArrayList<Fugitive> newListFugitives = new ArrayList<Fugitive>();

        //liste des différents gènes
        String[] genes = new String[] {"fugitive", "explorer", "rapidity", "size", "smart"};

        //map avec tous les gènes à 0
        HashMap<String, Double> genesFinisher = bestFinisher.getGenes();
        HashMap<String, Double> copyGene;

        for(int i = 0; i < listFinisher.size(); i++){
            //distribue les gènes pour chaque fugitif
            copyGene = new HashMap<>(genesFinisher);

            for(String g : genes){
                copyGene.replace(g, (copyGene.get(g) / 2) + listFinisher.get(i).getGenes().get(g) / 2);
            }

            if(i < listFinisher.size() / 2){
                addMutation(copyGene, genes[i%listFinisher.size()]);
            }

            //ajout du ième fugitif dans la liste
            newListFugitives.add(new Fugitive(rand.nextDouble(50, 750), rand.nextDouble(50, 750), (int) (1+copyGene.get("rapidity")), (int) (22 - copyGene.get("size") / 6), rand.nextDouble(2), copyGene));
        }
        return newListFugitives;
    }

    //ajoute une mutation qui prend 10% de chaque gènes pour l'ajouter dans un gène en particulier
    public void addMutation(HashMap<String, Double> genes, String g){
        double totMutation = 0;
        double tmp;
        for(String gene : genes.keySet()){
            tmp = genes.get(gene) / 10;
            totMutation += tmp;
            genes.replace(gene, genes.get(gene) - tmp);
        }
        genes.replace(g, genes.get(g) + totMutation);
    }

    //fait bouger toutes les entités une fois 
    public void moveEntities(){
        for(Fugitive ent : listFugitives){
            ent.chooseDirection(this);
        }
        for(Entity ent : listFugitives){
            ent.moveEntity();
        }
        for(Entity ent : listCatchers){
            ent.moveEntity();
        }
        this.deleteCaughtFugitives();
    }

    //renvoie la distance entre deux points
    public static double distance2point(double x, double y, double x2, double y2){
        return Math.sqrt((x2 - x) * (x2 - x) + (y2 - y) * (y2 - y));
    }

    //élimine les fugitifs attrapés et attibue les bonus
    public void deleteCaughtFugitives(){
        ArrayList<Fugitive> newList = new ArrayList<Fugitive>();
        ArrayList<int[]> newBonus = new ArrayList<int[]>();
        int i;
        boolean caught;
        for(int[] coord : this.bonusLife){
            newBonus.add(coord);
        }
        for(Fugitive f : this.listFugitives){
            i = 0;
            caught = false;
            while(!caught && i < this.listCatchers.size()){
                Catcher c = this.listCatchers.get(i);
                //si la distance entre les deux centre est inférieur à la somme du rayon du 
                //fugitif et du rayon de l'attrapeur, alors il est attrapé
                if(distance2point(f.getX(), f.getY(), c.getX(), c.getY()) <= f.getSize() + c.getSize()){
                    caught = true;
                }
                i++;
            }
            if(caught){
                if(!f.isCaught()){
                    f.setLife(f.getLife() - 1);
                    f.setCaught(true);
                }
            } else {
                f.setCaught(false);
                for(int[] coord : this.bonusLife){
                    if(f.getLife() < 5 && distance2point(f.getX(), f.getY(), coord[0], coord[1]) <= f.getSize() + 5){
                        f.setLife(f.getLife() + 1);
                        newBonus.remove(coord);
                        newBonus.add(new int[] {rand.nextInt(700) + 50, rand.nextInt(700) + 50});
                        while (newBonus.size() < nbBonus) {
                            newBonus.add(new int[] {rand.nextInt(700) + 50, rand.nextInt(700) + 50});
                        }
                    }
                }
            }
            if(f.getLife() > 0){
                newList.add(f);
            } else {
                this.listFinisher.add(f);
            }
        }
        this.setListFugitives(newList);
        this.setBonusLife(newBonus);
    }

    public void updateWave(){
        nbBonus = this.wave/10 + 1;
        for(Catcher c : this.listCatchers){
            c.setSize(c.getSize() + (this.wave / 15));
            c.setVelocity(c.getVelocity() + (this.wave / 10));
        }
    }

    public ArrayList<int[]> createFirstBonus(){
        ArrayList<int[]> listBonus = new ArrayList<int[]>();
        for(int i = 0; i < nbBonus; i++){
            listBonus.add(new int[] {rand.nextInt(700) + 50, rand.nextInt(700) + 50});
        }
        return listBonus;
    }

    public static int[] theNearestBonus(Fugitive f, ArrayList<int[]> listBonus){
        double min = 100000;
        int[] bonus = {0, 0};
        double dist;
        for(int[] coord : listBonus){
            dist = distance2point(coord[0], coord[1], f.getX(), f.getY());
            if(dist < min){
                bonus = coord;
                min = dist;
            }
        }
        return bonus;
    }

    public static Catcher theNearestCatcher(Fugitive f, ArrayList<Catcher> listCatchers){
        double min = 100000;
        Catcher catcher = listCatchers.get(0);
        double dist;
        for(Catcher c : listCatchers){
            dist = distance2point(f.getX(), f.getY(), c.getX(), c.getY());
            if(dist < min){
                catcher = c;
                min = dist;
            }
        }
        return catcher;
    }

    public static double theFarthestAngle(double dir1, double dir2){
        double dir = (dir1 + dir2) / 2;
        return (dir + 1)%2;
    }

    public static int getNbCatchers() {
        return NB_CATCHERS;
    }

    public static int getNbFugitives() {
        return NB_FUGITIVES;
    }

    public ArrayList<Fugitive> getListFugitives() {
        return listFugitives;
    }

    public void setListFugitives(ArrayList<Fugitive> listFugitives) {
        this.listFugitives = listFugitives;
    }

    public ArrayList<Catcher> getListCatchers() {
        return listCatchers;
    }

    public void setListCatchers(ArrayList<Catcher> listCatchers) {
        this.listCatchers = listCatchers;
    }

    public int getWave() {
        return wave;
    }

    public void incWave() {
        this.wave++;
    }

    public ArrayList<int[]> getBonusLife() {
        return bonusLife;
    }

    public void setBonusLife(ArrayList<int[]> bonusLife) {
        this.bonusLife = bonusLife;
    }

    public int getNbTime() {
        return nbTime;
    }

    public void incNbTime() {
        this.nbTime++;
    }

    public ArrayList<Fugitive> getListFinisher() {
        return listFinisher;
    }

    public void setListFinisher(ArrayList<Fugitive> listFinisher) {
        this.listFinisher = listFinisher;
    }

}