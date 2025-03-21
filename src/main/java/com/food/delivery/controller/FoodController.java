package com.food.delivery.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.delivery.request.FoodRequest;
import com.food.delivery.response.FoodResponse;
import com.food.delivery.service.FoodService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Author: Sandeep Singh
 * Date: 21/03/25
 */

@RestController
@RequestMapping("/api/foods")
@AllArgsConstructor
@Slf4j
public class FoodController {

    private final FoodService foodService;

    @PostMapping
    public ResponseEntity<FoodResponse> addFood(@RequestPart(name = "food") String foodString,
                                                @RequestPart(name = "file") MultipartFile file) {
        log.info("Entry:: FoodController.java ::: addFood() ::: Request -> Data: {} {}", foodString, file);
        ObjectMapper objectMapper = new ObjectMapper();
        FoodRequest foodRequest = null;
        try {
            foodRequest = objectMapper.readValue(foodString, FoodRequest.class);
        } catch (JsonProcessingException ex) {
            log.info("Exception :: FoodController.java ::: addFood() :: Exception -> Data {}", ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Json Format");
        }
        FoodResponse foodResponse = foodService.addFood(foodRequest, file);
        log.info("Exit:: FoodController.java ::: addFood() ::: Response -> Data: {}", foodResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(foodResponse);
    }

    @GetMapping
    public ResponseEntity<List<FoodResponse>> getAllFoods() {
        log.info("Entry:: FoodController.java ::: getAllFoods() ::: Request");
        List<FoodResponse> allFoods = foodService.getAllFoods();
        log.info("Exit:: FoodController.java ::: getAllFoods() ::: Response -> Data: {}", allFoods);
        return ResponseEntity.ok(allFoods);
    }

    @GetMapping("/id")
    public ResponseEntity<FoodResponse> getFoodByID(@RequestParam(name = "id") String foodId) {
        log.info("Entry:: FoodController.java ::: getFoodByID() ::: Request -> Data {}", foodId);
        FoodResponse foodResponse = foodService.getFoodByID(foodId);
        log.info("Exit:: FoodController.java ::: getFoodByID() ::: Response -> Data: {}", foodResponse);
        return ResponseEntity.ok(foodResponse);
    }

    @DeleteMapping("/id")
    public ResponseEntity<String> deleteFoodByID(@RequestParam(name = "id") String foodId) {
        log.info("Entry:: FoodController.java ::: deleteFoodByID() ::: Request -> Data {}", foodId);
        Boolean foodResponse = foodService.deleteFoodByID(foodId);
        log.info("Exit:: FoodController.java ::: deleteFoodByID() ::: Response -> Data: {}", foodResponse);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Food Delete with id :: " + foodId);
    }


}
