/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sivut;

import JFUtils.point.Point3D;
import codenameprojection.drawables.vertexGroup;
import codenameprojection.driver;
import codenameprojection.modelParser;
import codenameprojection.models.Model;
import codenameprojection.models.ModelFrame;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author elias
 */
public class Sivut {

    //driver d;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Sivut();
    }
    driver D;
    public Sivut() {
        
        driver d = new driver();
        D = d;
        d.models.clear();
        
        Thread t = new Thread(){
            @Override
            public void run() {
                super.run(); //To change body of generated methods, choose Tools | Templates.
                d.run();
            }
            
        };
        d.an_pause = true;
        t.start();
        //Wait until the driver has finished initialaizing
        System.out.println("Waiting for driver to init...");
        while(!d.running){
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
        System.out.println("Friver init succesfull? Continuing custom program init...");
        //Customize render settings
        System.out.println("Setting custom render parameters...");
        d.s.r.drawPoints = true;
        d.s.r.drawLines = true;
        d.s.r.drawFaces = true;
        d.s.r.usePixelRendering = false;
        d.s.r.drawErrors = true;
        //Wipe model record
        System.out.println("Clearing driver model record...");
        d.models.clear();
        //Create the triangle
        System.out.println("Creating triangle");
        LinkedList<Point3D> points = new LinkedList<>();
        Point3D a = new Point3D(1, 1, 1);
        Point3D b = new Point3D(50, 0, 1);
        Point3D c = new Point3D(50, 20, 1);
        points.add(a);
        points.add(b);
        points.add(c);
        
        
        LinkedList<Integer[]> lines = new LinkedList<>();
        
        
        lines.add(new Integer[]{a.identifier, b.identifier});
        lines.add(new Integer[]{a.identifier, c.identifier});
        lines.add(new Integer[]{b.identifier, c.identifier});
        
        LinkedList<Point3D[]> faces = new LinkedList<>();
        faces.add(new Point3D[]{a, b, c});
        
        ModelFrame frame = new ModelFrame(points, lines, faces, new LinkedList<vertexGroup>());
        
        
        LinkedList<ModelFrame> frames = new LinkedList<>();
        frames.add(frame);
        frames.add(frame);
        Model m = new Model(frames, true);
        
        System.out.println("Pushing triangle to driver memory...");
        int hash = m.hashCode();
        //d.models.put(d.defaultModelKey, m);
        d.models.put(hash, m);
        d.defaultModelKey = hash;
        System.out.println("Triangle pushed to driver model table.");
        printModelTable(d);
        modelParser.filename = new JFUtils.dirs().root + "assets/models/Viper5";
        //loadModel();
        printModelTable(d);
    }
    
    
    
    static void printModelTable(driver d2){
        ConcurrentHashMap<Integer, Model> copy = d2.models;
        System.out.println("Model table:");
        System.out.println("\tKEY\t\t\tVALUE");
        for(Object k : copy.keySet()){
            System.out.println("\t" + k + "\t\t\t" + copy.get(k));
        }
    }
    void loadModel(){
        D.models.clear();
            try {
                LinkedList<LinkedList<Point3D>> p = new modelParser().parse();
                LinkedList<Integer[]> l = new modelParser().parseLines(p.getFirst());
                LinkedList<Point3D[]> f = new modelParser().parseFaces(p.getFirst());
                LinkedList<vertexGroup> c = new modelParser().parseColor(p.getFirst());
                LinkedList<ModelFrame> frames = new LinkedList<>();
                for(LinkedList<Point3D> i : p){
                    frames.add(new ModelFrame(i, l, f, c));
                }
                int maxF = frames.size();
                int maxP = p.getFirst().size();
                System.out.println("Model has " + maxP + " points in " + maxF + " frames");
                Model m = new Model(frames, frames.size() < 3);
                m.hidePoints = false;
                m.ignoreRootNode = true;
                m.ignoreRootNodeThreshold = 2;
                int hash = m.hashCode();
                D.models.put(hash, m);
                D.defaultModelKey = hash;
            } catch (Exception ex) {
                JFUtils.quickTools.alert("Failed to load model", ex+"");
            }
    }
    
}
