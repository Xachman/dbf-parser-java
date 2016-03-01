/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbf.parser.pkg2;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.iryndin.jdbf.core.DbfField;
import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author ziron_000
 */
public class handler {
    private List<Integer> stoplist = new ArrayList<Integer>();
    public void toJson() {
        Charset stringCharset = Charset.forName("cp1252");

            InputStream dbf = getClass().getClassLoader().getResourceAsStream("data/CUSTOMER.DBF");
            InputStream memo = getClass().getClassLoader().getResourceAsStream("data/CUSTOMER.FPT");
            try (DbfReader reader = new DbfReader(dbf, memo)) {
                DbfMetadata meta = reader.getMetadata();
               // System.out.println("Read DBF Metadata: " + meta.getRecordsQty());
                int total = meta.getRecordsQty();
                DbfRecord rec = null;
                //Collection fields = <Collection>meta.getFields();
                //ystem.out.println(reader.read().getField("CUSNOTES"));
                Collection fields = meta.getFields();
                PrintWriter writer = new PrintWriter("test.json", "UTF-8");
               // System.out.println(System.getProperties());
                String json = "";
                writer.print("[");
                int count = 0;
                stoplist.addAll(Arrays.asList(1409, 2092, 2093, 2532, 5155, 5857, 5872, 6368, 6782, 7844, 8080, 8241, 8594, 8595, 8601, 9359));
                System.out.println(stoplist);
                while ((rec = reader.read()) != null) {
                   // System.out.println(Integer.parseInt(rec.getString("CUS_ID")));
                    if(!stoplist.contains(Integer.parseInt(rec.getString("CUS_ID")))){
                        continue;
                    }
                    json = "";
                    if(count > 0) json += "},";
                    count++;
                    json += "{";
                    for(Iterator<DbfField> i = fields.iterator(); i.hasNext(); ) {
                        DbfField field = i.next();
                        if(rec.getString(field.getName()) == null){
                             json += addFieldValJson(field.getName(), "");
                             if(i.hasNext()) json += ",";
                             continue;
                        }
                        //System.out.println(rec.getString(field.getName()).matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+"));
                        if(field.getType().toString() == "Memo" && rec.getString(field.getName()) != null  && rec.getString(field.getName()).matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")){
                            json += addFieldValJson(field.getName(), rec.getMemoAsString(field.getName()).replaceAll("\n", ", ").replaceAll("\r", ", "));
                            if(i.hasNext()) json += ",";
                                    
                            //System.out.println(field.getName() +": "+rec.getMemoAsString(field.getName()).replaceAll("\n", ", ").replaceAll("\r", ", "));
                            //System.out.println(rec.getMemoAsString(field.getName()));
                        }else if(field.getType().toString() == "Date"){
                            //try{
                                json += addFieldValJson(field.getName(), cleanData(rec.getString(field.getName())));
                                if(i.hasNext()) json += ",";
                                //System.out.println(rec.getDate(field.getName()));
                            //}catch(ParseException e){
                           //     e.printStackTrace();
                           // }
                            
                        }else{
                            //System.out.println(rec.getString(field.getName()));
                            json += addFieldValJson(field.getName(), cleanData(rec.getString(field.getName()).toString()));
                            if(i.hasNext()) json += ",";
                            //System.out.println(field.getName() +": "+rec.getString(field.getName()));
                            //System.getProperties();
                        }
                    }
                    
                    
                    writer.print(json);
                    
                         //System.out.println(json);
//                    rec.setStringCharset(stringCharset);
                    int dif = total - count;
                    System.out.println(dif);
           
                  
                }
                
                writer.print("}]");
                
                    
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
    }   
    
    public void toCsv() {
        Charset stringCharset = Charset.forName("UTF-8");

            File dbf = new File("C:\\Users\\ziron_000\\Desktop\\solw\\data\\invoice.Dbf");
            File memo = new File("C:\\Users\\ziron_000\\Desktop\\solw\\data\\invoice.FPT");
            try (DbfReader reader = new DbfReader(dbf, memo)) {
                DbfMetadata meta = reader.getMetadata();
               // System.out.println("Read DBF Metadata: " + meta.getRecordsQty());
                int total = meta.getRecordsQty();
                DbfRecord rec = null;
                //Collection fields = <Collection>meta.getFields();
                //ystem.out.println(reader.read().getField("CUSNOTES"));
                Collection fields = meta.getFields();
                PrintWriter writer = new PrintWriter("invoice2.csv", "UTF-8");
               // System.out.println(System.getProperties());
                String csv = "";
                writer.print("");
                int count = 0;
                
                for(Iterator<DbfField> i = fields.iterator(); i.hasNext(); ) {
                    DbfField field = i.next();
                    writer.print(field.getName());
                    if(i.hasNext()) {
                        writer.print(", ");
                    }else{
                        writer.print("\n");
                    }
                }
                 
                while ((rec = reader.read()) != null) {
                    System.out.println("ID: "+rec.getString("INVOICE"));
                    count++;
                    csv = "";
                    for(Iterator<DbfField> i = fields.iterator(); i.hasNext(); ) {
                        DbfField field = i.next();
                       // System.out.println(field.getName()+": "+rec.getString(field.getName()));
                       // System.out.println(rec.getString(field.getName()).matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+"));
                        if(field.getType().toString() == "Memo" && rec.getString(field.getName()) != null && rec.getString(field.getName()).matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")){
                            String memoData = addToCsv(rec.getMemoAsString(field.getName()).replaceAll("\r", "\n").replaceAll(" \" ", " ' "));
                            //rec.memoClose();
                           // System.out.println(memoData);
                            csv += memoData;
                            //System.out.println(field.getName() +": "+rec.getMemoAsString(field.getName()).replaceAll("\n", ", ").replaceAll("\r", ", "));
                            //System.out.println(rec.getMemoAsString(field.getName()));
                        }else if(field.getType().toString() == "Date"){
                            //try{
                                csv += addToCsv(rec.getString(field.getName()));
                                //System.out.println(rec.getDate(field.getName()));
                            //}catch(ParseException e){
                           //     e.printStackTrace();
                           // }
                            
                        }else{
                            csv += addToCsv(rec.getString(field.getName()));
                            //System.out.println(field.getName() +": "+rec.getString(field.getName()));
                            //System.getProperties();
                        }
                        if(i.hasNext()) {
                            csv += ", ";
                        }else{
                            csv += "\n";
                        }
                        
                       
                    }
                    
                     
                    writer.print(csv);
                    
                         //System.out.println(json);
//                    rec.setStringCharset(stringCharset);
                    int dif = total - count;
                    System.out.println(dif);
           
                  
                }
                System.out.print(csv);
                
                    
                writer.close();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            
    }  
    
    public void toJsonCouch() {
        Charset stringCharset = Charset.forName("cp1252");

            InputStream dbf = getClass().getClassLoader().getResourceAsStream("data/Invoice.DBF");
            InputStream memo = getClass().getClassLoader().getResourceAsStream("data/Invoice.FPT");
            try (DbfReader reader = new DbfReader(dbf, memo)) {
                DbfMetadata meta = reader.getMetadata();
               // System.out.println("Read DBF Metadata: " + meta.getRecordsQty());
                int total = meta.getRecordsQty();
                DbfRecord rec = null;
                //Collection fields = <Collection>meta.getFields();
                //ystem.out.println(reader.read().getField("CUSNOTES"));
                Collection fields = meta.getFields();   
               // System.out.println(System.getProperties());
               StringBuilder output = new StringBuilder();
                output.append("{ \"docs\": [");
                
                int count = 0;
                List<String> errorIds = new ArrayList<String>();
                while ((rec = reader.read()) != null) {
                    //System.out.println("INVOICE: "+rec.getString("INVOICE"));
                    if(count > 0) output.append("},");
                    count++;
                    output.append("{");
                    output.append(addFieldValJson("type", "customer"));
                    output.append(", ");
                    for(Iterator<DbfField> i = fields.iterator(); i.hasNext(); ) {
                        DbfField field = i.next();
                        
                        if(rec.getString(field.getName()) == null){
                            output.append(addFieldValJson(field.getName(), ""));
                            if(i.hasNext()) output.append(",");
                            continue;
                        }
                        //System.out.println(rec.getString(field.getName()).matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+"));
                        if(field.getType().toString() == "Memo" && rec.getString(field.getName()) != null  && rec.getString(field.getName()).matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")){
                            System.out.println("made memo");
                            output.append(addFieldValJson(field.getName(), cleanData(rec.getMemoAsString(field.getName()))));
                            if(i.hasNext()) output.append(",");
                                    
                            //System.out.println(field.getName() +": "+rec.getMemoAsString(field.getName()).replaceAll("\n", ", ").replaceAll("\r", ", "));
                            //System.out.println(rec.getMemoAsString(field.getName()));
                        }else if(field.getType().toString() == "Date"){
                            //try{
                                output.append(addFieldValJson(field.getName(), cleanData(rec.getString(field.getName()))));
                                if(i.hasNext()) output.append(",");
                                //System.out.println(rec.getDate(field.getName()));
                            //}catch(ParseException e){
                           //     e.printStackTrace();
                           // }
                            
                        }else{
                            output.append(addFieldValJson(field.getName(), cleanData(rec.getString(field.getName()).toString())));
                            if(i.hasNext()) output.append(",");
                            //System.out.println(field.getName() +": "+rec.getString(field.getName()));
                            //System.getProperties();
                        }
                        
                    }
                    
                   // output.append("}");
                    
                    
                   // writer.print(json);
//                   try{
//                     sendPost(json);  
//                   }catch(Exception e) {
//                       
//                       //first: [1409, 2092, 2093, 2532, 4072, 5155, 5239, 5857, 5872, 5973, 6257, 6257, 6257, 6257, 6257, 6257, 6368, 6782, 7844, 8080, 8241, 8594, 8595, 8601, 9359]
//                       //second: [1409, 2092, 2093, 2532, 5155, 5857, 5872, 6368, 6782, 7844, 8080, 8241, 8594, 8595, 8601, 9359]
//                       // jobs [1075, 1080, 2857, 15952, 18937, 20372]
//                       errorIds.add(rec.getString("INVOICE"));
//                       e.printStackTrace();
//                       
//                   }
                   
                    System.out.println(output);
                    
                         //System.out.println(json);
//                    rec.setStringCharset(stringCharset);
                        
                   // int dif = total - count;
                   // System.out.println(dif);
                    
                  if(count == 1000) {
                     output.append("}]}");
                    try{
                        System.out.println("sending ... ");
                       // System.out.println(output.toString());
                        sendPost(output.toString());
                        output.setLength(0);
                        output.append("{ \"docs\": [");
                        count = 0;
                      }catch(Exception e) {
                        PrintWriter writer = new PrintWriter("err.json", "UTF-8");
                           writer.print(output);
                           writer.close();
                           //first: [1409, 2092, 2093, 2532, 4072, 5155, 5239, 5857, 5872, 5973, 6257, 6257, 6257, 6257, 6257, 6257, 6368, 6782, 7844, 8080, 8241, 8594, 8595, 8601, 9359]
                           //second: [1409, 2092, 2093, 2532, 5155, 5857, 5872, 6368, 6782, 7844, 8080, 8241, 8594, 8595, 8601, 9359]
                           // jobs [1075, 1080, 2857, 15952, 18937, 20372]
                           //errorIds.add();
                           e.printStackTrace();

                       }
                  }
                }
                //System.out.println(errorIds);
               // System.out.println(output);
               output.append("}]}");
                try{
                    System.out.println("sending ... ");
                   // System.out.println(output.toString());
                    sendPost(output.toString());  
                  }catch(Exception e) {
                       
                       //first: [1409, 2092, 2093, 2532, 4072, 5155, 5239, 5857, 5872, 5973, 6257, 6257, 6257, 6257, 6257, 6257, 6368, 6782, 7844, 8080, 8241, 8594, 8595, 8601, 9359]
                       //second: [1409, 2092, 2093, 2532, 5155, 5857, 5872, 6368, 6782, 7844, 8080, 8241, 8594, 8595, 8601, 9359]
                       // jobs [1075, 1080, 2857, 15952, 18937, 20372]
                       //errorIds.add();
                       e.printStackTrace();
                       
                   }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            
    }
    public void readDBF() throws IOException, ParseException {
        Charset stringCharset = Charset.forName("Cp866");

        InputStream dbf = getClass().getClassLoader().getResourceAsStream("data/CUSTOMER.DBF");

        DbfRecord rec;
        try (DbfReader reader = new DbfReader(dbf)) {
            DbfMetadata meta = reader.getMetadata();

            System.out.println("Read DBF Metadata: " + meta);
            while ((rec = reader.read()) != null) {
                rec.setStringCharset(stringCharset);
                System.out.println("Record #" + rec.getRecordNumber() + ": " + rec.toMap());
            }
        }
    }
    
    private String addFieldValJson(String field, String val) {
        String str = "\""+field+"\" : \""+val+"\"";
        return str;
    }
    
    private String addToCsv(String val) {
        String str = "\""+val+"\"";
        return str;
    }
    // HTTP POST request
	private void sendPost(String json) throws Exception {
        System.out.println("Adding url ..");
		String url = "http://shorewindowcleaning:withwindows@ironside.ddns.net:5984/shorewindowcleaning/_bulk_docs";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		//add reuqest header
		con.setRequestMethod("POST");
		//con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        String basicAuth = "Basic " + new String(new Base64().encode(obj.getUserInfo().getBytes()));
        con.setRequestProperty("Authorization", basicAuth);

		//String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
		System.out.println("Adding output ..");
		// Send post request
		con.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		wr.write(json);
		wr.flush();
		wr.close();
        System.out.println("geting content ..");
       // System.out.println(con.getResponseMessage());
		int responseCode = con.getResponseCode();
        System.out.println("Adding checking response ..");
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + json);
		System.out.println("Response Code : " + responseCode);
        System.out.println(con.getResponseMessage());
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//print result
		System.out.println(response.toString());

	}
    
    
    private String cleanData(String str) {
        str = str.replaceAll("\\\\", "\\\\\\\\");
        str = str.replaceAll("\"", "\\\\\"");
        str = str.replaceAll("\r", "\\n");
        str = str.replaceAll("\n", "\\n");
        str = str.replaceAll("[^0-9a-zA-Z_\"\\-]", "");
        return str;
    }
    
    private String makeDate(String str) {
        //date stuff
        return str;
    }
    
}
