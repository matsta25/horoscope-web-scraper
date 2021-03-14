package com.matsta25.horoscopewebscraper.controller;

import com.matsta25.horoscopewebscraper.service.HoroscopeService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;

import static com.matsta25.horoscopewebscraper.util.ZipDirectory.zipDirectory;

@RestController
@RequestMapping("/api/v1/")
public class HoroscopeController {

    HoroscopeService horoscopeService;

    public HoroscopeController(HoroscopeService horoscopeService) {
        this.horoscopeService = horoscopeService;
    }

    @GetMapping(
            value = "/start-horoscope-scrapping",
            produces = "application/zip")
    public ResponseEntity<Resource> startHoroscopeScrapping(
            @RequestParam(name = "endDate", defaultValue = "2021-3-1") String endDate,
            @RequestParam(name = "startDate", defaultValue = "2021-3-16") String startDate
    ) {
        this.horoscopeService.startHoroscopeScrapping(startDate, endDate);

        try {
            zipDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStreamResource resource = null;
        try {
            resource = new InputStreamResource(new FileInputStream("scrapedData.zip"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=\"scrapedData.zip\"")
                .body(resource);
    }
}
