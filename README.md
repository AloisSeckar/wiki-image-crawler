# wiki-image-crawler

A tool for automated parsing of Wikipedia image category pages and retrieving the URLs of the original images. Based on simple Java 11 HttpClient.

Originally being used for help to fetch data for [https://github.com/ByMykel/spanish-cities](https://github.com/ByMykel/spanish-cities).

## Usage

On startup the program prompts for the name of the Wikimedia image category page - for example `GitHub_logos`. The exact URL being called is `https://commons.wikimedia.org/w/index.php?title=<<input>>`. The input gets encoded into Wiki-compatible URL, however with some terminals (like Windows `cmd`) it may fail to produce a valid Wikimedia category name with non-ASCII characters. Then the next prompt asks for JSON key for retrieved image URL.

Given the provided category name is valid `commons.wikimedia.org` category, the program will then retrieve the original image URL via Wikimedia API for every image entry located in this category. The entries are located via `galleryfilename galleryfilename-truncate` CSS class selectors. The API call being made is `https://commons.wikimedia.org/w/api.php?action=query&titles=File:<<image_name>>&prop=imageinfo&iiprop=url&format=json`

The retrieve results are turned into JSON objects, for example:

```json
{
  "name" : "GitHub Logo.png",
  "<<provided_key>>" : "https://upload.wikimedia.org/wikipedia/commons/5/54/GitHub_Logo.png"
}
```

The retrieved data array is written into `output.json` file, which is created in current working directory. If the category provided is empty or non-existent on Wikimedia, the file will contain an empty JSON array (`[]`).

## Release

You can use `/release/wiki-image-crawler-1.0.jar` to run the tool directly without building it yourself. On a machine with JRE installed run the file with `java -jar wiki-image-crawler-1.0.jar` command.

## Changelog
* 2023-10-30 - ability to crawl "next page" of larger categories
* 2023-10-17 - encode input as Wiki-compatible URL
* 2023-10-10 - JSON output with customizable key for image URL
* 2023-10-08 - basic wiki page crawling ability
