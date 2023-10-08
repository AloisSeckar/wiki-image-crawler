package cz.aloisseckar;

import java.net.URI;
import java.net.http.*;
import java.util.ArrayList;
import java.util.Scanner;

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

                        // print JSON-like structure to the output
                        // TODO directly modify cities.json data
                        System.out.println("{");
                        System.out.println("  \"name\" : \"" + file + "\",");
                        if (flags) {
                            System.out.println("  \"flag\" : \"" + url + "\"");
                        } else {
                            System.out.println("  \"coat_of_arms\" : \"" + url + "\"");
                        }
                        System.out.println("}");
                    });
                } catch (Exception ex) {
                    System.err.println(file);
                    System.err.println(ex.getMessage());
                }
            });

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

}
