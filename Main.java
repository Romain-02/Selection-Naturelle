import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Evolution");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(820, 840);

        // Crée un JPanel personnalisé pour dessiner les cercles
        EntityPanel entityPanel = new EntityPanel();
        frame.add(entityPanel);

        frame.setVisible(true);

        // Lance le jeu en lui-même
        Evolution evolution = new Evolution();
        int generation = 1;
        long time = 0;

        //boolean qui dit si l'entrainement continu
        boolean train = true;
        //temps du début de l'entraineur
        long begWave = 0;
        //temps d'une vague en itération
        final int TIME_WAVE = 400;
        //nombre de vagues maximal
        final int WAVE_MAX = 30;


        do{

            // Boucle principale
            while (!evolution.getListFugitives().isEmpty()) {

                evolution.moveEntities();

                // Met à jour les entités dans le JPanel
                entityPanel.setEntities(evolution.getListFugitives(), evolution.getListCatchers());
                // Met à jour les bonus dans le JPanel
                entityPanel.setBonus(evolution.getBonusLife());
                // Met à jour la génération dans le JPanel
                entityPanel.setGeneration(generation);
                // Met à jour la vague dans le JPanel
                entityPanel.setWave(evolution.getWave());

                // Actualise le dessin des entités
                entityPanel.repaint();

                // Ajoute un court délai pour visualiser les changements
                try {
                    //si on peux encore passer une vague
                    if(evolution.getWave() < WAVE_MAX){
                        //si les fugitifs ont survécu 15 seconds à la vague
                        if((!train && begWave + TIME_WAVE < time) || (begWave + TIME_WAVE < time)){
                            evolution.incWave();
                            begWave = time;
                            evolution.updateWave();
                        }
                    }
                    //regarde si il est encore en entrainement
                    if(train){
                        if(evolution.getWave() == 30 && begWave + 10 * TIME_WAVE < time){
                            train = false;
                        }
                    } else{
                        Thread.sleep(4);
                    }
                    time++;
                    evolution.incNbTime();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            evolution = new Evolution(evolution.createNewFugitives());
            generation++;
            try {
                if(generation % 50 == 0){
                    Thread.sleep(1);
                    System.out.println(generation + " : gen");
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

        }while(true);
    }

    // JPanel personnalisé pour dessiner les entités (cercles)
    static class EntityPanel extends JPanel {
        private List<Entity> entities;
        private List<int[]> listBonus;
        private int generation;
        private int wave;

        // Méthode pour mettre à jour la liste des entités
        public void setEntities(ArrayList<Fugitive> arrayListF, ArrayList<Catcher> arrayListC) {
            this.entities = new ArrayList<Entity>();
            this.entities.addAll(arrayListF);
            this.entities.addAll(arrayListC);
        }

        // Méthode pour mettre à jour la liste des bonus
        public void setBonus(ArrayList<int[]> listBonus) {
            this.listBonus = listBonus;
        }        

        // Méthode pour mettre à jour la génération
        public void setGeneration(int generation) {
            this.generation = generation;
        }  
        
        // Méthode pour mettre à jour la vague
        public void setWave(int wave) {
            this.wave = wave;
        }      

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Dessine chaque entité (cercle) et bonus
            if (entities != null) {
                int[] size;
                for (Entity entity : entities) {
                    size = new int[] {entity.getSize(), entity.getSize()};
                    if(entity instanceof Fugitive){
                        Fugitive fug = (Fugitive) entity;
                        if(fug.isCaught()){
                            g.setColor(Color.black);
                        } else{
                            g.setColor(Color.GREEN);  // Couleur du cercle
                        }
                        g.drawString(fug.getLife() + "", (int) entity.getX() - 5, (int) entity.getY() - size[1]);
                    } else {
                      g.setColor(Color.RED);  // Couleur du cercle
                    }
                    g.fillOval((int) entity.getX() - size[0], (int) entity.getY() - size[1], size[0] * 2, size[1] * 2);  // Position et taille du cercle
                }
                for (int[] coordsB : listBonus) {
                    g.setColor(Color.CYAN);
                    g.fillOval(coordsB[0], coordsB[1], 10, 10);  // Position et taille du cercle
                }
            }
            this.writeInformation(g);

        }

        public void writeInformation(Graphics g){
            //écris la génération
            g.setColor(Color.BLACK);
            g.setFont(new Font("impact", Font.BOLD, 20)); 
            g.drawString("Generation : " + this.generation, 30, 770);
            g.setFont(new Font("impact", Font.BOLD, 16)); 
            g.drawString("Wave : " + this.wave, 30, 788);

            //écris les gènes
            g.setColor(Color.BLACK);
            g.setFont(new Font("impact", Font.BOLD, 8)); 
            int y = 30;
            for(Entity e : this.entities){
                if(e instanceof Fugitive){
                    Fugitive f = (Fugitive) e;
                    g.drawString((y-30)/20+1 + " : " + f.getGenes(), 10, y);
                    y += 20;
                }
            }
        }
    }
}