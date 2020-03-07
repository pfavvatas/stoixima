/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stoixima;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author takis
 */
public class Stoixima {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Scraping https://www.soccerbase.com/");
        ArrayList<RefereeLeague> listRefereeLeague = new ArrayList<>();
        getRefereesCompetitions(listRefereeLeague);
        showRefereesCompetitions(listRefereeLeague);
        

    }

    private static void showRefereesCompetitions(ArrayList<RefereeLeague> listRefereeLeague) {
        System.out.println(listRefereeLeague.toString());
    }
    
    static class RefereeLeague {
        public String Name;
        public int Id;
        
        RefereeLeague(String name, int id){
            this.Name = name;
            this.Id = id;
        }
        
        @Override
        public String toString(){
            return "[" + this.Id +"][" + this.Name + "]";
        }
    }

    private static void getRefereesCompetitions(ArrayList<RefereeLeague> listRefereeLeague) {
        System.out.println("Looking for referees' Leagues");

        try {
            // TODO code application logic here
            Document doc = Jsoup.connect("https://www.soccerbase.com/referees/home.sd").get();
            Elements newsHeadlines = doc.select("#viewSelector option");
            for (Element headline : newsHeadlines) {
                String name;
                int id;
                
                name = headline.text();
                try {
                    id = Integer.parseInt(headline.val());
                } catch (NumberFormatException idnum) {
                    id = 0;
                    continue;
                }
                if(id == 0) continue;
                RefereeLeague rf = new RefereeLeague(name,id);
                listRefereeLeague.add(rf);
            }
        } catch (IOException ex) {
            Logger.getLogger(Stoixima.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
}
