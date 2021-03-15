package com.matsta25.horoscopewebscraper.service;

import com.matsta25.horoscopewebscraper.model.ZodiacSign;
import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class HoroscopeService {

    public static final String SCRAPED_DATA_DIR = "scrapedData";
    Logger logger = LoggerFactory.getLogger(HoroscopeService.class);

    @Value("${horoscope.url}")
    private String horoscopeUrl;

    public String startHoroscopeScrapping(String startDate, String endDate) {
        int rowsSum = 0;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-M-d");

        for (ZodiacSign zodiacSign : ZodiacSign.values()) {
            Document doc = null;
            List<String[]> csvData = new ArrayList<>();
            LocalDate date = LocalDate.parse(startDate, dtf);
            boolean hadData = true;
            while (hadData) {
                if (date.isEqual(LocalDate.parse(endDate, dtf)) ||
                        date.isEqual(LocalDate.of(2016, 5, 31))) {
                    hadData = false;
                    continue;
                }

                doc = getDocumentHtml(doc, zodiacSign.getLabel(), date.toString());

                if (!isDataAvailable(doc)){
                    date = date.minusDays(1);
                    continue;
                }

                String datePlValue = getDatePlValue(doc);

                String horoscopePlValue = getHoroscopePlValue(doc);

                String[] row = {zodiacSign.getLabel(), date.toString(), datePlValue, horoscopePlValue};

                logger.info(String.format("%s\t%s\t%s\t%s", zodiacSign.getLabel(), date.toString(), datePlValue, horoscopePlValue));
                csvData.add(row);
                rowsSum++;
                date = date.minusDays(1);
            }

            saveDataToCsv(csvData, zodiacSign.getLabel());
        }

        return "Success. Scraped " + rowsSum + " elements.";
    }

    private boolean isDataAvailable(Document doc) {
        Elements datePl = doc.select(".date-container");
        return datePl.first() != null;
    }

    private void saveDataToCsv(List<String[]> csvData, String zodiacSign) {
        try (CSVWriter writer = new CSVWriter(new FileWriter("./" + SCRAPED_DATA_DIR + "/" + zodiacSign + ".csv"))) {
            writer.writeAll(csvData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Document getDocumentHtml(Document doc, String zodiacSign, String date) {
        try {
            doc = Jsoup.connect(String.format(horoscopeUrl, zodiacSign, date)).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    private String getHoroscopePlValue(Document doc) {
        return doc.select(".lead").first().childNodes().get(0).toString();
    }

    private String getDatePlValue(Document doc) {
        Elements datePl = doc.select(".date-container");
        String datePlValue = datePl.first().childNodes().get(0).toString();
        return datePlValue.replace("\n", "");
    }
}
