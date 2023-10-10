package cz.aloisseckar;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.*;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class WikiImageCrawler {

    public static void main(String[] args) {

        // take category as user's input
        System.out.println("Enter the name of the category:");
        var in = new Scanner(System.in);
        var category = in.nextLine();

        // depending on this variable the image URL will be either treated as "flag" or as "coat_of_arms"
        var flags = category.contains("flags");

        try (HttpClient client = HttpClient.newHttpClient()) {
            // fetch Wikipedia category page
            var categoryRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://commons.wikimedia.org/wiki/Category:" + category))
                    .build();
            var categoryResponse = client.send(categoryRequest, HttpResponse.BodyHandlers.ofString());

            // look for all relevant image entries on the category page
            var files = new ArrayList<String>();
            categoryResponse.body().lines().forEach(line -> {
                if (line.contains("galleryfilename galleryfilename-truncate")) {
                    var file = line.substring(line.indexOf(">") + 1, line.indexOf("</"));
                    files.add(file);
                }
            });

            System.out.println("Fetched " + files.size() + " files");

            System.out.println("Getting image paths...");
            JSONArray jsonDataArray = new JSONArray();
            files.forEach(file -> {
                try {
                    // sanitize input
                    // - whitespaces have to be transformed to "_"
                    // - apostrophes (') need to be transformed to HTML's "%27"
                    var normalizedFile = file.replaceAll("\s", "_").replaceAll("&#039;", "%27");

                    // fetch Wikimedia API
                    var fileRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://commons.wikimedia.org/w/api.php?action=query&titles=File:" + normalizedFile + "&prop=imageinfo&iiprop=url&format=json"))
                            .build();
                    var fileResponse = client.send(fileRequest, HttpResponse.BodyHandlers.ofString());

                    // extract original image URL from GET response
                    fileResponse.body().lines().forEach(line -> {
                        // uncomment this when error is returned to see what is wrong
                        // System.out.println(normalizedFile); System.out.println(line);

                        // target URL is presented inside "url" attribute of returned JSON
                        var url = line.substring(line.indexOf("\"url\":\"") + 7, line.indexOf("\",\"descriptionurl\""));

                        // create JSON data
                        JSONObject jsonData = new JSONObject();
                        jsonData.put("name", file);
                        if (flags) {
                            jsonData.put("flag", url);
                        } else {
                            jsonData.put("coat_of_arms", url);
                        }
                        jsonDataArray.put(jsonData);
                    });
                } catch (Exception ex) {
                    System.err.println(file);
                    System.err.println(ex.getMessage());
                }
            });
            System.out.println("Image data retrieved");

            // write output into JSON file
            System.out.println("Creating JSON output...");
            try (FileWriter jsonFile = new FileWriter("output.json")) {
                jsonDataArray.write(jsonFile, 2, 0);
                jsonFile.flush();
                System.out.println("File 'output.json' created");
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

}
