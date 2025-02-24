package com.example.pocconsumetransaction.controller;

import com.example.pocconsumetransaction.service.SubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class SubmitController {

    @Autowired
    private SubmitService submitService;
    @PostMapping("/submit")
    public ResponseEntity<String> test(@RequestBody String transactionId) {
        System.out.println("input transaction id : " + transactionId);
        return ResponseEntity.ok(submitService.submit(transactionId));
    }

    @PostMapping("/test/batch/insert")
    public ResponseEntity<String> batch(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(submitService.batchInsert(file));
    }

    @PostMapping("/test/begin/insert")
    public ResponseEntity<String> single(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(submitService.singleInsert(file));
    }
}
