# wiki-image-crawler

A tool for automated parsing of Wikipedia image category pages and retrieving the URLs of the original images. Based on simple Java 11 HttpClient.

Originally being used for help to fetch data for [https://github.com/ByMykel/spanish-cities](https://github.com/ByMykel/spanish-cities).

## Usage

On startup the program prompts for the name of the image category page - for example `SVG_flags_of_municipalities_of_Álava-Araba` (copy the name from URL, don't use the Wiki page title). 

Then the images from given category are the original image URL is retrieved via Wikimedia API for each entry. The results are turned into JSON structure, for example:

```json
{
  "name" : "Bandera de Elvillar.svg",
  "flag" : "https://upload.wikimedia.org/wikipedia/commons/f/f3/Bandera_de_Elvillar.svg"
}
```

The program automatically differs between `flag` and `coat_of_arms` key based on the category name.

The retrieved data are written into `output.json` file, which is created in current working directory.

## Release

You can use `/release/wiki-image-crawler-1.0.jar` to run the tool directly without building it yourself. On a machine with JRE installed run the file with `java -jar wiki-image-crawler-1.0.jar` command.
