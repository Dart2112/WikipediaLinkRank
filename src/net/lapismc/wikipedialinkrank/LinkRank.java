package net.lapismc.wikipedialinkrank;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

class LinkRank {

    //This HashMap will store the titles of links scraped and the number of times that title occurred
    private HashMap<String, Integer> pageOccurrence = new HashMap<>();
    private List<String> urls = new ArrayList<>();

    LinkRank() {
        //create a list of the urls to index
        //These URLs are from https://en.wikipedia.org/wiki/Wikipedia:Annual_Top_50_Report
        urls.add("https://en.wikipedia.org/wiki/2017");
        urls.add("https://en.wikipedia.org/wiki/Donald_Trump");
        urls.add("https://en.wikipedia.org/wiki/Elizabeth_II");
        urls.add("https://en.wikipedia.org/wiki/Game_of_Thrones_(season_7)");
        urls.add("https://en.wikipedia.org/wiki/Meghan_Markle");
        urls.add("https://en.wikipedia.org/wiki/Game_of_Thrones");
        urls.add("https://en.wikipedia.org/wiki/List_of_Bollywood_films_of_2017");
        urls.add("https://en.wikipedia.org/wiki/United_States");
        urls.add("https://en.wikipedia.org/wiki/Bitcoin");
        urls.add("https://en.wikipedia.org/wiki/13_Reasons_Why");
        urls.add("https://en.wikipedia.org/wiki/Baahubali_2:_The_Conclusion");
        urls.add("https://en.wikipedia.org/wiki/It_(2017_film)");
        urls.add("https://en.wikipedia.org/wiki/Queen_Victoria");
        urls.add("https://en.wikipedia.org/wiki/List_of_highest-grossing_Indian_films");
        urls.add("https://en.wikipedia.org/wiki/Gal_Gadot");
        urls.add("https://en.wikipedia.org/wiki/Logan_(film)");
        urls.add("https://en.wikipedia.org/wiki/Riverdale_(2017_TV_series)");
        urls.add("https://en.wikipedia.org/wiki/2017_in_film");
        urls.add("https://en.wikipedia.org/wiki/Stranger_Things");
        urls.add("https://en.wikipedia.org/wiki/Wonder_Woman_(2017_film)");
        urls.add("https://en.wikipedia.org/wiki/Dwayne_Johnson");
        urls.add("https://en.wikipedia.org/wiki/Star_Wars:_The_Last_Jedi");
        urls.add("https://en.wikipedia.org/wiki/Justice_League_(film)");
        urls.add("https://en.wikipedia.org/wiki/Elon_Musk");
        urls.add("https://en.wikipedia.org/wiki/Facebook");
        urls.add("https://en.wikipedia.org/wiki/Cristiano_Ronaldo");
        urls.add("https://en.wikipedia.org/wiki/Get_Out");
        urls.add("https://en.wikipedia.org/wiki/India");
        urls.add("https://en.wikipedia.org/wiki/Millennials");
        urls.add("https://en.wikipedia.org/wiki/Barack_Obama");
        urls.add("https://en.wikipedia.org/wiki/YouTube");
        urls.add("https://en.wikipedia.org/wiki/O._J._Simpson");
        urls.add("https://en.wikipedia.org/wiki/Conor_McGregor");
        urls.add("https://en.wikipedia.org/wiki/Charles_Manson");
        urls.add("https://en.wikipedia.org/wiki/Ed_Sheeran");
        urls.add("https://en.wikipedia.org/wiki/Melania_Trump");
        urls.add("https://en.wikipedia.org/wiki/Princess_Margaret,_Countess_of_Snowdon");
        urls.add("https://en.wikipedia.org/wiki/Split_(2016_American_film)");
        urls.add("https://en.wikipedia.org/wiki/Thor:_Ragnarok");
        urls.add("https://en.wikipedia.org/wiki/Floyd_Mayweather_Jr.");
        urls.add("https://en.wikipedia.org/wiki/Pablo_Escobar");
        urls.add("https://en.wikipedia.org/wiki/World_War_II");
        urls.add("https://en.wikipedia.org/wiki/Spider-Man:_Homecoming");
        urls.add("https://en.wikipedia.org/wiki/Prince_Philip,_Duke_of_Edinburgh");
        urls.add("https://en.wikipedia.org/wiki/Star_Wars");
        urls.add("https://en.wikipedia.org/wiki/Ariana_Grande");
        urls.add("https://en.wikipedia.org/wiki/Dunkirk_(2017_film)");
        urls.add("https://en.wikipedia.org/wiki/Adolf_Hitler");
        urls.add("https://en.wikipedia.org/wiki/Google");
        urls.add("https://en.wikipedia.org/wiki/Guardians_of_the_Galaxy_Vol._2");
        generateData(5, "Top 5");
        generateData(10, "Top 10");
        generateData(25, "Top 25");
        generateData(51, "All 50");
    }

    private void generateData(int limit, String title) {
        int i = limit;
        for (String url : urls) {
            //stop loading pages if we have reached the limit
            if (i <= 0)
                break;
            //loop through the URLs and add the links from them to the list
            addLinksForUrl(url);
            i--;
        }
        //print the entire list to console for raw values and debugging
        printList(title);
        //generate the pie chart JPEG
        generateChart(title);
        //clear the list to stop duplication
        pageOccurrence.clear();
    }

    private void addLinksForUrl(String url) {
        try {
            //Use Jsoup to load the URLs document
            Document doc = Jsoup.connect(url).get();
            //Find all the links in the page
            Elements links = doc.select("a[href]");
            //Loop through all these links
            for (Element link : links) {
                //get the url of the link
                String linkURL = link.attr("abs:href");
                //ignore pages that aren't on wikipedia, this is to limit the number of sites we have to load to get the title
                if (!linkURL.contains("en.wikipedia.org/wiki") || linkURL.startsWith(url)) {
                    continue;
                }
                //get the title of the link by loading the target page
                String title = Jsoup.connect(linkURL).get().title();
                //ensure its a wikipedia article by ignoring the link if it doesn't
                //end with " - Wikipedia" or is in fact a category or file
                if (!title.endsWith(" - Wikipedia") || title.startsWith("Category:") || title.startsWith("File:")) {
                    continue;
                }
                //remove the " - Wikipedia" from the end of the title as its no longer required
                title = title.replace(" - Wikipedia", "");
                //If the link is already in the list just add to the integer, otherwise add it to the list with a value of 1
                if (pageOccurrence.containsKey(title)) {
                    pageOccurrence.put(title, pageOccurrence.get(title) + 1);
                } else {
                    pageOccurrence.put(title, 1);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void printList(String title) {
        //Sort the pageOccurrence by the value
        //https://stackoverflow.com/questions/21054415/how-to-sort-a-hashmap-by-the-integer-value
        Object[] a = pageOccurrence.entrySet().toArray();
        Arrays.sort(a, (o1, o2) -> ((Map.Entry<String, Integer>) o2).getValue()
                .compareTo(((Map.Entry<String, Integer>) o1).getValue()));
        System.out.println("Raw list for " + title);
        for (Object e : a) {
            if (((Map.Entry<String, Integer>) e).getValue() > 1) {
                System.out.println(((Map.Entry<String, Integer>) e).getValue() +
                        " : " + ((Map.Entry<String, Integer>) e).getKey());
            }
        }
    }

    //A significant part of the graphing code is from these URLs
    //https://robbamforth.wordpress.com/2008/10/30/java-jfreechart-graphs-and-charts-in-java/
    //https://robbamforth.wordpress.com/2008/11/05/java-jfreechart-how-to-save-a-jcfreechart-to-jpeg-file/

    @SuppressWarnings("unchecked")
    private void generateChart(String title) {
        DefaultPieDataset data = new DefaultPieDataset();
        //sort the data by the value in descending order
        Object[] a = pageOccurrence.entrySet().toArray();
        Arrays.sort(a, (o1, o2) -> ((Map.Entry<String, Integer>) o2).getValue()
                .compareTo(((Map.Entry<String, Integer>) o1).getValue()));
        int i = 0;
        for (Object e : a) {
            //get the top 15 items and add them to the chart
            if (i >= 15)
                break;
            if (((Map.Entry<String, Integer>) e).getValue() > 1) {
                Integer amount = ((Map.Entry<String, Integer>) e).getValue();
                String name = ((Map.Entry<String, Integer>) e).getKey();
                data.setValue(name, amount);
                i++;
            }
        }
        JFreeChart chart = ChartFactory.createPieChart(title, data, false, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionOutlinesVisible(false);
        plot.setNoDataMessage("No data available");
        //save the chart as a JPEG in the current directory
        try {
            String fileName = title + ".jpg";
            saveToFile(chart, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToFile(JFreeChart chart, String fileName) throws IOException {
        BufferedImage img = draw(chart);
        fileName = "images" + File.separator + fileName;
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        if (file.exists()) {
            file.delete();
        }
        ImageIO.write(img, "jpg", file);
    }

    private BufferedImage draw(JFreeChart chart) {
        BufferedImage img = new BufferedImage(1080, 1080, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        chart.draw(g2, new Rectangle2D.Double(0, 0, 1080, 1080));
        g2.dispose();
        return img;
    }

}
