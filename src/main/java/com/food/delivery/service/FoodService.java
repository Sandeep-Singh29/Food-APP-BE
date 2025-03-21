package com.food.delivery.service;

import com.food.delivery.request.FoodRequest;
import com.food.delivery.response.FoodResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Author: Sandeep Singh
 * Date: 21/03/25
 */

public interface FoodService {

    String uploadFile(MultipartFile file);

    FoodResponse addFood(FoodRequest foodRequest, MultipartFile file);

    List<FoodResponse> getAllFoods();

    FoodResponse getFoodByID(String foodId);

    Boolean deleteFoodByID(String foodId);

    Boolean deleteFile(String filename);

}

