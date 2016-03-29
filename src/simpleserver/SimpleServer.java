/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleserver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Random;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.net.URL;
//import java.net.URLConnection;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.io.IOUtils;
/**
 *
 * @author liuqi
 */
public class SimpleServer {

    //the static info of the server:
    //port: portal #;
    //maxConnection: maximum connection of the server @ the same time
    //dimention: the dimention of KDTree Structure
    private static int DataSetupPort=4444, SearchPort = 4400, dimention = 26,
            maxConnections=10; //need to sync them.
    private static  boolean data_convert_lock = true;
    private static int counter = 0;
    
    //the thread lock try to prevent search b/4 data structure is setted
    private static boolean tree_lock = false;
   
    //the data structure that should be use by the server.
    //however, I am not sure if we should make it0 static when concurrency happened.
    private static KDTree tree ;
    private static ArrayList<KDTree> treeList;
    private static ArrayList<campaignList> cll;
    
    /*
     * This class is a templete object 
     * of price and name of campain object
     */
    public static class priceNameObject{
        String name;double price;
        public priceNameObject(String name,  double price)
        {this.price = price;this.name = name;}
        public void setPrice(double price){this.price = price;}
        public void setName(String name){this.name = name;}
        public double getPrice(){return price;}
        public String getName(){return name;}
    }
    
    /*
     * For the purpose of inserting into KDTREE to perform range search
     * This local class describe campaign into pure numbers.
     * Putting related alphabet into index: A-Z to 0-25
     * attr_A => 0; A08 =>8
     * name, price, attribute list  
     */
    private static class campaign{
        String name;
        double price;
        //a list of attributes of this campain as index
        // with their maximum number as content
        double[] attr;
        public campaign (String name, double[] attr, double price){
            this.price = price;
            this.name = name;
            this.attr = attr;
        }
        public double[] getAttr(){
            return attr;
        }
        public String getName(){
            return name;
        }
        public double getPrice(){
            return price;
        }
        public Object getObject(){
            Object a;
            a = new priceNameObject(name, price);
            return a;
        }
    }
    
    private static class campaignList{
        public ArrayList<campaign> campaignList = new ArrayList<>();
        public void  addCampaign(campaign camp){campaignList.add(camp);}
        public ArrayList<campaign>  getCampaignList(){return campaignList;}
    }
    
   /*
    * doComms class implements Runnable 
    *       which handle the runtime tasks 
    *       initiate with a Socket server. 
    */
    private static class doComms implements Runnable {
        private Socket server;
        private String line,input;
        private InputStream stream;
        private  PrintStream out;

        doComms(Socket server) {this.server=server;input="";}
        
        public void run () {
            //if the tree_lock is set already, perform search.
            //if the tree_lock is faulse means the tree list is not set yet.
            //setup the tree.
            try{
                stream = server.getInputStream();
                out = new PrintStream(server.getOutputStream(),true);
                byte[] datat = new byte[1];
                stream.read(datat);
                String identifier = new String(datat, "UTF-8"); 
                System.out.println("diu  = " +identifier);
                if (identifier.equals("i") && !tree_lock)
                    setup_treeList();
                else if(identifier.equals("s"))
                    search_treeList();
            } catch (IOException ioe) {
                System.out.println("IOException on socket listen: " + ioe);
                ioe.printStackTrace();
                out.println("ERROR: " +ioe ); 
                try {
                    //server communication done.
                    server.close();
                } catch (IOException ex) {
                    System.out.println("IOException on socket listen: " + ioe);
                    Logger.getLogger(SimpleServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if(tree_lock){
                System.out.println("data insertion done");
                tree_lock= false;
            }
            else{
                //System.out.println("data insertion fail pls restart server");
                //System.exit(1);
            }
        }    
        
        private void search_treeList(){
            try
            {
                //consumming the input user json./
                stream = server.getInputStream();
                out = new PrintStream(server.getOutputStream(),true);
                byte[] data = new byte[200];
                StringBuilder sb = new StringBuilder();
                int count = 0;
                while ( (count = stream.read(data))> 0){
                    String decoded = new String(data, "UTF-8");
                    sb.append(decoded);
                    if(count<200){
                        break;
                    }
                    //to erase the old data.
                    data = new byte[200];
                }
                //System.out.println("sb = " + sb.toString() );
                
                //Convert json into user class.
                final GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(User.class, new UserDeserializer());
                final Gson gson = gsonBuilder.create();
                final User user = gson.fromJson(sb.toString().trim(), User.class);
                //String temp_p = gson.toJson(user);
                
                //System.out.println("temp_p = " + temp_p );
                int counter = Integer.parseInt(user.getName().substring(1));
                int num_attr = user.getTarget().length;
                //System.out.println("num_attr num_attr= " + num_attr);
                //construct useable search range for KDTREE
                double [] upper = new double[num_attr];
                Arrays.fill(upper, 100);
                double [] lower = new double[num_attr];
                Arrays.fill(lower, 0);
                int index = 0;
                
                for(int temp : user.getTarget()){
                    if (temp>upper[index]){
                        priceNameObject winner = new priceNameObject("none", 0.0);
                        //when user target has attr greater than 100, none will match
                        
                        String answer = "{\n\"winner\": \"";
                        answer +=winner.getName();
                        answer += "\",\n\"counter\": ";
                        answer +=counter;
                        answer +="\n}";
                        out.println(answer); //+   recieved.length());
                        server.close();
                        System.out.println("object name= " + winner.getName());
                        System.out.println("user attribute number exceed 100, end search"); 
                        break;
                    }
                    lower[index] = temp;
                    index ++;
                }
                /*
                System.out.println("lower  length = " +  lower.length);
                for(int kk = 0; kk<lower.length; kk++){
                    System.out.println("lower = " +  lower[kk] + " @ index = " +kk); 
                }*/
                priceNameObject winner = new priceNameObject("none", 0.0);
                if(!server.isClosed()){//tree_lock && (!server.isClosed())){
                    
                    //System.out.println("lower  length = " +  lower.length);
                    priceNameObject temp = treeList.get(num_attr-1).range(lower, upper, 0);
                    //System.out.println("lower  length = " +  lower.length);
                    if (temp!=null){
                        if (temp.getPrice() > winner.getPrice()){
                                winner.setName(temp.getName());
                                winner.setPrice(temp.getPrice());
                            }
                    }
                    //search all corresponding tree that has less target than the user
                    /*
                    for(int kk = 0; kk<num_attr; kk++){
                        priceNameObject temp = treeList.get(kk).range(lower, upper, 0);
                        if (temp!=null){
                            //System.out.println("priceNameObject temp name=" +temp.getName() +"  temp price=" +temp.getPrice());
                            if (temp.getPrice() > winner.getPrice()){
                                winner.setName(temp.getName());
                                winner.setPrice(temp.getPrice());
                            }
                        }
                    }*/
                    System.out.println("object name= " + winner.getName());
                    String answer = "{\n\"winner\": \"";
                    answer +=winner.getName();
                    answer += "\",\n\"counter\": ";
                    answer +=counter;
                    answer +="\n}";
                    //out.println("Overall message length:" +sb.toString() ); //+   recieved.length());
                    out.println(answer);
                    server.close();
                }
            }
            catch(Exception e)
            {
                //out.println("Error: " + );
                System.out.println("Error" + e);
            }
        }
        
        //this method creates the treelist from socket inpustream
        //use byte[] as buffer and trim() before casting into json
        private void setup_treeList(){
            try {
                System.out.println("setting up tree list");
                //stream = server.getInputStream();
                //out = new PrintStream(server.getOutputStream(),true);
                byte[] data = new byte[200];
                StringBuilder sb = new StringBuilder();
                int count = 0;
                               
                
                while ( (count = stream.read(data))> 0){
                    String decoded = new String(data, "UTF-8");
                    sb.append(decoded);
                    if(count<200){break;}
                    //to erase the old data.
                    data = new byte[200];
                }
                //System.out.println("sb = " + sb.length() );
                out.println("Overall message recieved length: " +sb.length() ); 
                //server communication done.
                //server.close();

                /*
                 *Using Gson builder with deserializer
                 */
                //System.out.println("sb = " +sb );
                final GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(target.class, new TargetDeserializer());
                gsonBuilder.registerTypeAdapter(Campaign.class, new CampaignDeserializer());
                gsonBuilder.registerTypeAdapter(Campaigns.class, new CampaignsDeserializer());
                final Gson gson = gsonBuilder.create();
                final Campaigns campaigns = gson.fromJson(sb.toString().trim(), Campaigns.class);
                String temp_p = gson.toJson(campaigns);
                Campaign[] camp = campaigns.getCampaign();

                //re-initiallize tree list
                ArrayList<campaign> campaignList = new ArrayList<>();
                //cll= new ArrayList<>();
                treeList = new ArrayList<>();
                
                //campaign list of 0 - 25
                // each for campaign that has equal or less attributes than corresponding index.
                // eg. cll.get(5).getCampaignList() includes all campaigns that has 
                //      5 or less target attributes. 
                //      Therefore, a target user of (A,B,C,D,E) can use this list. 
                //      
                for(int i=0; i<26; i++){
                    KDTree tree_temp = new KDTree(i+1);
                    campaignList temp = new campaignList();
                    //cll.add(i,temp);
                    treeList.add(i,tree_temp);
                }

                for(Campaign camp_temp : camp){
                    target[] t = camp_temp.getTarget();
                    //tempory array of the attributes of each existing alphabet
                    double[] temp_array = new double[26];
                    Arrays.fill(temp_array, 100);
                    int index = 0;
                    //
                    
                    for(target t_temp : t){
                        String [] s = t_temp.getAttr_list();
                        if(s.length>0){
                            String a =s[s.length - 1];
                            String b = a.substring(1);
                            temp_array[index] = Double.parseDouble(b);
                        }
                        else{
                            //an empty attribute list.
                            temp_array[index] = 0;
                            //System.out.println( "camp_temp.getName() = " + camp_temp.getName() +" which "+t_temp.getName());
                        }
                        index++;
                    }
                    //System.out.println( "index = " + index +" which "+camp_temp.getName());
                    campaign temp = new campaign(camp_temp.getName(),temp_array,camp_temp.getPrice());
                    //cll.get(index).addCampaign(temp);
                    //if index=0, which no attr in campain, discard it.
                    //since the only match will be empty user, which make no sence.
                    if (index !=0){
                        //a tree will include campaigns with same or less attributes
                        //compare to its dimension.
                        for (int kk = index-1; kk<26; kk++)
                            treeList.get(kk).insertToTree(temp.getObject(),temp.getAttr());
                    }
                    campaignList.add(temp);
                }
                int totle_nodes = 0;
                 for (int kk =0; kk<26; kk++)
                    totle_nodes += treeList.get(kk).getCount();
                out.println(" <br> Data insertion finished! total # of nodes insert = " + totle_nodes ); 
                out.println("<br> (execpt for those empty campaigns, which I think can only target empty user. )" ); 
                //server communication done.
                server.close();
                
                if(treeList.get(10).getCount() !=0)
                    tree_lock = true;     
            } catch (IOException ioe) {
                System.out.println("IOException on socket listen: " + ioe);
                ioe.printStackTrace();
            }catch(Exception e){
                System.out.println("Exception on socket listen: " + e);
                e.printStackTrace();
            }
        }
    }

    //not in use anymore, combined with docomms
    /*
    private static class Socket2 implements Runnable
    {
        private Socket server;
        
        Socket2(Socket server) {this.server=server;}

        public void run()
        {
            try
            {
                //consumming the input user json./
                InputStream stream = server.getInputStream();
                PrintStream out = new PrintStream(server.getOutputStream(),true);
                byte[] data = new byte[200];
                StringBuilder sb = new StringBuilder();
                int count = 0;
                while ( (count = stream.read(data))> 0){
                    String decoded = new String(data, "UTF-8");
                    sb.append(decoded);
                    if(count<200){
                        break;
                    }
                    //to erase the old data.
                    data = new byte[200];
                }
                //System.out.println("sb = " + sb.toString() );
                
                //Convert json into user class.
                final GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(User.class, new UserDeserializer());
                final Gson gson = gsonBuilder.create();
                final User user = gson.fromJson(sb.toString().trim(), User.class);
                String temp_p = gson.toJson(user);
                
                int counter = Integer.parseInt(user.getName().substring(1));
                int num_attr = user.getTarget().length;
                //System.out.println("num_attr num_attr= " + num_attr);
                //construct useable search range for KDTREE
                double [] upper = new double[num_attr];
                Arrays.fill(upper, 100);
                double [] lower = new double[num_attr];
                Arrays.fill(lower, 0);
                int index = 0;
                
                for(int temp : user.getTarget()){
                    if (temp>upper[index]){
                        priceNameObject winner = new priceNameObject("none", 0.0);
                        //when user target has attr greater than 100, none will match
                        
                        String answer = "{\n\"winner\": \"";
                        answer +=winner.getName();
                        answer += "\",\n\"counter\": ";
                        answer +=counter;
                        answer +="\n}";
                        out.println(answer); //+   recieved.length());
                        server.close();
                        System.out.println("object name= " + winner.getName());
                        System.out.println("user attribute number exceed 100, end search"); 
                        break;
                    }
                    lower[index] = temp;
                    index ++;
                }
                /*
                System.out.println("lower  length = " +  lower.length);
                for(int kk = 0; kk<lower.length; kk++){
                    System.out.println("lower = " +  lower[kk] + " @ index = " +kk); 
                }*/
                /*
                priceNameObject winner = new priceNameObject("none", 0.0);
                if(tree_lock && (!server.isClosed())){
                    
                    //System.out.println("lower  length = " +  lower.length);
                    priceNameObject temp = treeList.get(num_attr-1).range(lower, upper, 0);
                    //System.out.println("lower  length = " +  lower.length);
                    if (temp!=null){
                        if (temp.getPrice() > winner.getPrice()){
                                winner.setName(temp.getName());
                                winner.setPrice(temp.getPrice());
                            }
                    }
                    //search all corresponding tree that has less target than the user
                    /*
                    for(int kk = 0; kk<num_attr; kk++){
                        priceNameObject temp = treeList.get(kk).range(lower, upper, 0);
                        if (temp!=null){
                            //System.out.println("priceNameObject temp name=" +temp.getName() +"  temp price=" +temp.getPrice());
                            if (temp.getPrice() > winner.getPrice()){
                                winner.setName(temp.getName());
                                winner.setPrice(temp.getPrice());
                            }
                        }
                    }*/
                    /*
                    System.out.println("object name= " + winner.getName());
                    String answer = "{\n\"winner\": \"";
                    answer +=winner.getName();
                    answer += "\",\n\"counter\": ";
                    answer +=counter;
                    answer +="\n}";
                    //out.println("Overall message length:" +sb.toString() ); //+   recieved.length());
                    out.println(answer);
                    server.close();
                }
            }
            catch(Exception e)
            {
                //out.println("Error: " + );
                System.out.println("Error" + e);
            }
        }
    }
    */
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        /*
         * Use server Socket to listen on the portal for user data
         */        
      
        int i=0;
        try{

            
            //ServerSocket SetupListener = new ServerSocket(DataSetupPort);
            ServerSocket SearchListener = new ServerSocket(SearchPort);
            Socket serverSetup,serverSearch;
            System.out.println("waiting to set up server data structure");
            /*
                doComms conn_c;
                serverSetup = SearchListener.accept();
                conn_c = new doComms(serverSetup);
                Thread setup = new Thread(conn_c);
                setup.start();*/
            
            while(true){//(i++ < maxConnections) || (maxConnections == 0)){
                System.out.println("fcking up server");
                counter++;
                /*
                Socket2 searchSocket;
                serverSearch = SearchListener.accept();
                searchSocket = new Socket2(serverSearch);
                
                //start setup && search server. listen to port 4444 & 4400
                Thread search = new Thread(searchSocket);
                search.start();*/
                doComms conn_c;
                serverSetup = SearchListener.accept();
                conn_c = new doComms(serverSetup);
                Thread setup = new Thread(conn_c);
                setup.start();
            }
            
        } catch (IOException ioe) {
            System.out.println("IOException on socket listen: " + ioe);
            ioe.printStackTrace();
        }
        
    }
}


