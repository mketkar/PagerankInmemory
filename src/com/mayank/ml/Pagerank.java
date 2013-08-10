package com.mayank.ml;

import java.util.*;
import java.io.*;

public class Pagerank {
        HashMap<Long,Long[]> adj = new HashMap<Long,Long[]>(); 
        HashMap<Long,Integer> w = new HashMap<Long,Integer>(); 
        HashMap<Long,Double> v = new HashMap<Long,Double>();
        HashMap<Long,Double> oldv = new HashMap<Long,Double>();
        long n = 0;
        private void extrapolation(HashMap<Long,Double> extraV){
                //extraV is k-2, oldv is k-1 v is k
                double g = 0;
                double h = 0;
                double newV = 0;
                for (long i=0; i<n; i++) {
                        g = (oldv.get(i)-extraV.get(i));
                        g = g*g;//compute g
                        h = v.get(i) - 2*oldv.get(i) + extraV.get(i);
                        newV = v.get(i) - g/h;
                        if(g >= 1e-8 && h >= 1e-8){
                                v.put(i, newV);
                        }
                }
        }
        public void compute(String filename, String outputFile, double c, double epsilon, int maxIteration){
                long counter = 0;
                n = 0;
                String str = "";
                try {
                        FileInputStream instream = new FileInputStream(filename);
                        DataInputStream in = new DataInputStream(instream);
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        // first line is number of nodes
                        try {
                                n = Long.parseLong(br.readLine());
                                for (long i=0; i<n; i++) {
                                        v.put(i,0d);
                                        oldv.put(i,1d);
                                }
                        } catch (IOException e1) {
                                e1.printStackTrace();
                        }
                        
                        try {
                                Long index = 0L;
                                while ((str = br.readLine())!=null) {
                                        // here the adjacent matrix will store the node number of one node's adjacents.
                                        //no outlink case                             
                                        if(str.equals("")){
                                            Long[] tmplong = new Long[0];
                                            adj.put(index,tmplong);
                                            w.put(index,0);
                                            index++;
                                            continue;
                                        }
                                        String[] strs = str.split("\\s+");
                                        Long[] tmplong = new Long[strs.length];
                                        for (int i=0;i<strs.length;i++)
                                                tmplong[i] = Long.parseLong(strs[i]);
                                        adj.put(index,tmplong);
                                        w.put(index,strs.length);
                                        index++;
                                }
                                br.close();
                        } catch (IOException e1) {
                                e1.printStackTrace();
                        }       
                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                }
                
                boolean flag = true;
                
                while (flag && counter < maxIteration) {
                        flag = false;
                        long convCounter = 0L;
                        // for each node
                        for (long i=0; i<n; i++) {
                                // add c
                                if (v.containsKey(i))
                                        v.put(i, v.get(i)+c);
                                else
                                        v.put(i,c);
                                Long[] outlinks = adj.get(i);
                                //use w instead
                                for (int j=0; j<w.get(i); j++) {
                                        // for each outlink, assign its pr/edgeNo
                                        // here we should include damping factor(1-c)
                                        if(v.containsKey(outlinks[j]))
                                                v.put(outlinks[j], v.get(outlinks[j])+(1-c)*oldv.get(i)/(double)w.get(i));
                                        else
                                                v.put(outlinks[j],(1-c)*oldv.get(i)/(double)w.get(i));
                                }
                        }
                        
                        for (long i=0; i<n; i++) {
                                if (Math.abs(v.get(i)-oldv.get(i))> epsilon) {
                                        flag = true;
                                        //continue;
                                        convCounter ++;
                                }
                        }
                        
                        System.out.println("Iteration "+counter+", Node Not Converge "+convCounter);
                        //clone is kind of slow, let's swap it and clear
                        HashMap<Long,Double> tmpV = oldv;
                        oldv = v;
                        v = tmpV;
                        v.clear();

                        System.gc();
                        counter ++;
                }
                //write all ranks to file
                File file = new File(outputFile);
                if (!file.exists()) {
                        try {
                                file.createNewFile();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
                FileWriter fw = null;
                try {
                        fw = new FileWriter(file.getAbsoluteFile());
                } catch (IOException e) {
                        e.printStackTrace();
                }
                BufferedWriter bw = new BufferedWriter(fw);
                for (long i=0;i<n;i++)
                        try {   //easy to sort let's keep format as node \t rank
                                bw.write(i+"\t"+oldv.get(i)+"\n");
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                try {
                        bw.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
        @SuppressWarnings("unchecked")
        public void compute(String filename, String outputFile, double c, double epsilon, int maxIteration, int interval){
                long counter = 0;
                n = 0;
                String str = "";
                HashMap<Long,Double> extraV = null;
                try {
                        FileInputStream instream = new FileInputStream(filename);
                        DataInputStream in = new DataInputStream(instream);
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        // first line is number of nodes
                        try {
                                n = Long.parseLong(br.readLine());
                                for (long i=0; i<n; i++) {
                                        v.put(i,0d);
                                        oldv.put(i,1d);
                                }
                        } catch (IOException e1) {
                                e1.printStackTrace();
                        }
                        
                        try {
                                Long index = 0L;
                                while ((str = br.readLine())!=null) {
                                        // here the adjacent matrix will store the node number of one node's adjacents.
                                        //no outlink case                             
                                        if(str.equals("")){
                                            Long[] tmplong = new Long[0];
                                            adj.put(index,tmplong);
                                            w.put(index,0);
                                            index++;
                                            continue;
                                        }
                                        String[] strs = str.split("\\s+");
                                        Long[] tmplong = new Long[strs.length];
                                        for (int i=0;i<strs.length;i++)
                                                tmplong[i] = Long.parseLong(strs[i]);
                                        adj.put(index,tmplong);
                                        w.put(index,strs.length);
                                        index++;
                                }
                                br.close();
                        } catch (IOException e1) {
                                e1.printStackTrace();
                        }       
                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                }
                
                boolean flag = true;
                
                while (flag && counter < maxIteration) {
                        flag = false;
                        long convCounter = 0L;
                        // for each node
                        for (long i=0; i<n; i++) {
                                // add c
                                if (v.containsKey(i))
                                        v.put(i, v.get(i)+c);
                                else
                                        v.put(i,c);
                                Long[] outlinks = adj.get(i);
                                //use w instead
                                for (int j=0; j<w.get(i); j++) {
                                        // for each outlink, assign its pr/edgeNo
                                        // here we should include damping factor(1-c)
                                        if(v.containsKey(outlinks[j]))
                                                v.put(outlinks[j], v.get(outlinks[j])+(1-c)*oldv.get(i)/(double)w.get(i));
                                        else
                                                v.put(outlinks[j],(1-c)*oldv.get(i)/(double)w.get(i));
                                }
                        }
                        
                        for (long i=0; i<n; i++) {
                                if (Math.abs(v.get(i)-oldv.get(i))> epsilon) {
                                        flag = true;
                                        //continue;
                                        convCounter ++;
                                }
                        }
                        if((counter+1)%interval == 0){
                                extraV = (HashMap<Long, Double>) oldv.clone();
                        }
                        if(counter != 0 && counter%interval == 0){
                                extrapolation(extraV);
                                extraV.clear();
                        }
                        System.out.println("Iteration "+counter+", Node Not Converge "+convCounter);
                        //clone is kind of slow, let's swap it and clear
                        HashMap<Long,Double> tmpV = oldv;
                        oldv = v;
                        v = tmpV;
                        v.clear();

                        System.gc();
                        counter ++;
                }
                //write all ranks to file
                File file = new File(outputFile);
                if (!file.exists()) {
                        try {
                                file.createNewFile();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
                FileWriter fw = null;
                try {
                        fw = new FileWriter(file.getAbsoluteFile());
                } catch (IOException e) {
                        e.printStackTrace();
                }
                BufferedWriter bw = new BufferedWriter(fw);
                for (long i=0;i<n;i++)
                        try {   //easy to sort let's keep format as node \t rank
                                bw.write(i+"\t"+oldv.get(i)+"\n");
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                try {
                        bw.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
        public static void main(String args[]) {
                if(args.length != 5 && args.length != 6){
                        System.out.println("Usage: java Pagerank inputfile outputfile c epsilon maxiter \n java Pagerank inputfile outputfile c epsilon maxiter extraInterval");
                        return;
                }
                if(args.length == 5){
                        Pagerank p = new Pagerank();
                        String filename = args[0];
                        String outputFile = args[1];
                        double c = Double.parseDouble(args[2]);
                        double epsilon = Double.parseDouble(args[3]);
                        int maxIteration = Integer.parseInt(args[4]);
                        p.compute(filename, outputFile, c, epsilon, maxIteration);
                        return;
                }
                if(args.length == 6){
                        Pagerank p = new Pagerank();
                        String filename = args[0];
                        String outputFile = args[1];
                        double c = Double.parseDouble(args[2]);
                        double epsilon = Double.parseDouble(args[3]);
                        int maxIteration = Integer.parseInt(args[4]);
                        int interval = Integer.parseInt(args[5]);
                        p.compute(filename, outputFile, c, epsilon, maxIteration, interval);
                        return;
                }
        }
}
