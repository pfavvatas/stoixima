/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stoixima;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.FileOutputStream;
import java.io.IOException;
 



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 *
 * @author takis
 */
public class Stoixima {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Connection condb = null;
        Statement statdb = null;
        ResultSet resdb = null;
        
        System.out.println("Scraping https://www.soccerbase.com/");
        ArrayList<RefereeLeague> listRefereeLeague = new ArrayList<>();
        ArrayList<Referee> listReferee = new ArrayList<>();
        getRefereesCompetitions(listRefereeLeague);
        //showRefereesCompetitions(listRefereeLeague);        
        System.out.println("Looking for referees");
        for(int i = 0; i < listRefereeLeague.size(); i++)
            getAllReferees(listRefereeLeague.get(i).getID(),listReferee);
//        //showReferees(listReferee);
        //File(listReferee);
        
        
        try{
            condb = DriverManager.getConnection("jdbc:derby://localhost:1527/stoixima","root","root");
            statdb = condb.createStatement();
            insertReferees(listReferee,statdb);
        } catch (SQLException e){
            e.printStackTrace();
        }
        

    }

    private static void showRefereesCompetitions(ArrayList<RefereeLeague> listRefereeLeague) {
        System.out.println(listRefereeLeague.toString());
    }

    private static void getAllReferees(int id, ArrayList<Referee> listReferee) {
        try {
            // TODO code application logic here
            Document doc = Jsoup.connect("https://www.soccerbase.com/referees/home.sd?comp_id="+id).get();
            //Elements newsHeadlines = doc.select("#referee").select("td");
            Elements newsHeadlines = doc.select("table.referee tbody tr");
            for (Element headline : newsHeadlines) {
                String name,nationality;
                int idref,games,yellow,red;
                name = headline.select("td").first().text();
                idref = 0;
                String stridtemp = headline.select("a").attr("href");
                String stridref = stridtemp.replace("/referees/referee.sd?referee_id=", "");
                idref = Integer.parseInt(stridref);
                nationality = headline.select("td").first().nextElementSibling().text();
                games = Integer.parseInt(headline.select("td").first().nextElementSibling().nextElementSibling().text());
                yellow = Integer.parseInt(headline.select("td").first().nextElementSibling().nextElementSibling().nextElementSibling().text());
                red = Integer.parseInt(headline.select("td").first().nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling().text());
                Referee rf = new Referee(idref,name,nationality,games,yellow,red);
                listReferee.add(rf);
            }
        } catch (IOException ex) {
            //Logger.getLogger(Stoixima.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    private static void showReferees(ArrayList<Referee> listReferee) {
        System.out.println(listReferee.toString());
    }

    private static void File(ArrayList<Referee> listReferee) {
         try {
            FileWriter myWriter = new FileWriter("referees.html");
            String html = "<style>\n" +
                        "table {\n" +
                        "  font-family: arial, sans-serif;\n" +
                        "  border-collapse: collapse;\n" +
                        "  width: 100%;\n" +
                        "}\n" +
                        "\n" +
                        "td, th {\n" +
                        "  border: 1px solid #dddddd;\n" +
                        "  text-align: left;\n" +
                        "  padding: 8px;\n" +
                        "}\n" +
                        "\n" +
                        "tr:nth-child(even) {\n" +
                        "  background-color: #dddddd;\n" +
                        "}\n" +
                        "</style><table>" +
                    "<tr>" +
                        "<th>Id</th>" +
                        "<th>Name</th>" +
                        "<th>Nationality</th>" +
                        "<th>Games</th>" +
                        "<th>Yellow</th>" +
                        "<th>Red</th>" +
                    "</tr>" +
                    listReferee.toString() +
                    "</table>";
            myWriter.write(html);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }

    private static void insertReferees(ArrayList<Referee> listReferee, Statement statdb) throws SQLException {
        for (Referee ref : listReferee){
            try{
                System.out.println(ref.Id + " " + ref.Name.replace("'", "''"));
                statdb.execute(String.format("INSERT INTO root.REFEREE(REFID,REFNAME) VALUES(%d,'%s')", ref.Id, ref.Name.replace("'", "''")));
            } catch (SQLException S){
                System.out.println(S);
                continue;
            }
        }
    }

//    private static void XLS(ArrayList<Referee> listReferee) throws FileNotFoundException, IOException {
//
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet sheet = workbook.createSheet("Java Books");
//         
//        Object[][] bookData = {
//                {"Head First Java", "Kathy Serria", 79},
//                {"Effective Java", "Joshua Bloch", 36},
//                {"Clean Code", "Robert martin", 42},
//                {"Thinking in Java", "Bruce Eckel", 35},
//        };
// 
//        int rowCount = 0;
//         
//        for (Object[] aBook : bookData) {
//            Row row = sheet.createRow(++rowCount);
//             
//            int columnCount = 0;
//             
//            for (Object field : aBook) {
//                Cell cell = row.createCell(++columnCount);
//                if (field instanceof String) {
//                    cell.setCellValue((String) field);
//                } else if (field instanceof Integer) {
//                    cell.setCellValue((Integer) field);
//                }
//            }
//             
//        }
//         
//         
//        try (FileOutputStream outputStream = new FileOutputStream("JavaBooks.xlsx")) {
//            workbook.write(outputStream);
//        }
//    }

    static class Referee {
        public int Id;
        public String Name;
        public String Nationality;
        public int Games;
        public int YellowCards;
        public int RedCards;
        
        Referee(int id,String name, String nationality, int games, int yellow, int red){
            this.Id = id;
            this.Name = name;
            this.Nationality = nationality;
            this.Games = games;
            this.YellowCards = yellow;
            this.RedCards = red;
        }
        
        @Override
        public String toString(){
            return "<tr><td>" + this.Id + "</td><td>" + this.Name +"</td><td>" + this.Nationality + "</td><td>"+"" + this.Games +"</td><td>" + this.YellowCards + "</td><td>"+"" + this.RedCards +"</td></tr>";
        }
        
        
        
        
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
        
        public int getID(){
            return this.Id;
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
            //Logger.getLogger(Stoixima.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
}
