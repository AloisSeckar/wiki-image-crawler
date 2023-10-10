# wiki-image-crawler

A tool for automated parsing of Wikipedia image category pages and retrieving the URLs of the original images. Based on simple Java 11 HttpClient.

Originally being used for help to fetch data for [https://github.com/ByMykel/spanish-cities](https://github.com/ByMykel/spanish-cities).

## Usage

On startup the program prompts for the name of the Wikimedia image category page - for example `SVG_flags_of_municipalities_of_Álava-Araba` (copy the name from URL, don't use the Wiki page title). Then the next prompt asks for JSON key for retrieved image URL.

Given the provided category name is valid `commons.wikimedia.org` category, the program will then retrieve the original image URL via Wikimedia API for every image entry located in this category. The entries are located via `galleryfilename galleryfilename-truncate` CSS class selectors. The API call being made is `https://commons.wikimedia.org/w/api.php?action=query&titles=File:<<image_name>>&prop=imageinfo&iiprop=url&format=json`

The retrieve results are turned into JSON objects, for example:

```json
{
  "name" : "Bandera de Elvillar.svg",
  "<<provided_key>>" : "https://upload.wikimedia.org/wikipedia/commons/f/f3/Bandera_de_Elvillar.svg"
}
```

The retrieved data array is written into `output.json` file, which is created in current working directory. If the category provided is empty or non-existent on Wikimedia, the file will contain an empty JSON array (`[]`).

## Release

You can use `/release/wiki-image-crawler-1.0.jar` to run the tool directly without building it yourself. On a machine with JRE installed run the file with `java -jar wiki-image-crawler-1.0.jar` command.
