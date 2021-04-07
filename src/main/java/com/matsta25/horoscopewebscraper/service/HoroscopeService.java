package com.matsta25.horoscopewebscraper.service;

import com.matsta25.horoscopewebscraper.model.ZodiacSign;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HoroscopeService {

    public static final String SCRAPED_DATA_DIR = "scrapedData";
    public static final char DELIMITER = ';';

    Logger LOGGER = LoggerFactory.getLogger(HoroscopeService.class);

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
                if (date.isBefore(LocalDate.parse(endDate, dtf))
                        || date.isEqual(LocalDate.of(2016, 5, 31))) {
                    hadData = false;
                    continue;
                }

                doc = getDocumentHtml(doc, zodiacSign.getLabel(), date.toString());

                if (!isDataAvailable(doc)) {
                    date = date.minusDays(1);
                    continue;
                }

                String horoscopePlValue = getHoroscopePlValue(doc);

                String[] row = {zodiacSign.getLabel(), date.toString(), horoscopePlValue};

                LOGGER.info(
                        String.format(
                                "%s\t%s\t%s",
                                zodiacSign.getLabel(), date.toString(), horoscopePlValue));
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
        LOGGER.info(getPathWithCsvFile(zodiacSign));
        new File(getPathWithCsvFile(zodiacSign)).getParentFile().mkdirs();
        try (CSVWriter writer =
                new CSVWriter(
                        new FileWriter(getPathWithCsvFile(zodiacSign)),
                        DELIMITER,
                        CSVWriter.DEFAULT_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END)) {
            writer.writeAll(csvData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPathWithCsvFile(String zodiacSign) {
        return System.getProperty("user.dir") + File.separator + SCRAPED_DATA_DIR + File.separator + zodiacSign + ".csv";
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
}
