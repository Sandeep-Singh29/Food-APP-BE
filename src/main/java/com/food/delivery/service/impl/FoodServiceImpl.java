package com.food.delivery.service.impl;

import com.food.delivery.entity.FoodEntity;
import com.food.delivery.repository.FoodRepository;
import com.food.delivery.request.FoodRequest;
import com.food.delivery.response.FoodResponse;
import com.food.delivery.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Author: Sandeep Singh
 * Date: 21/03/25
 */

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final S3Client s3Client;
    private final FoodRepository foodRepository;

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Value("${aws.credential}")
    private boolean awsCredential;


    @Override
    public String uploadFile(MultipartFile file) {
        String fileExtension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String key = UUID.randomUUID().toString() + "." + fileExtension;
        // test only bcz i have now AWS credential
        if (!awsCredential) {
            return "https://" + bucketName + ".s3.amazonaws.com/" + key;
        }
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .acl("public-read")
                    .contentType(file.getContentType())
                    .build();
            PutObjectResponse response = S3Client.create().putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            if (response.sdkHttpResponse().isSuccessful()) {
                return "https://" + bucketName + ".s3.amazonaws.com/" + key;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed.");
            }
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occur while uploading file");
        } finally {
            s3Client.close();
        }
    }

    @Override
    public FoodResponse addFood(FoodRequest foodRequest, MultipartFile file) {
        FoodEntity foodEntity = convertToEntity(foodRequest);
        String fileUrl = uploadFile(file);
        foodEntity.setImageUrl(fileUrl);
        foodEntity = foodRepository.save(foodEntity);
        return convertToResponse(foodEntity);
    }

    @Override
    public List<FoodResponse> getAllFoods() {
        return foodRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public FoodResponse getFoodByID(String foodId) {
        return foodRepository.findById(foodId)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("Food not found with id: " + foodId));
    }

    //    https://my-food-app.s3.amazonaws.com/dfafaaff-e49f-4f46-ba99-d8522ea3d359.jpeg
    @Override
    public Boolean deleteFoodByID(String foodId) {
        FoodResponse foodResponse = getFoodByID(foodId);
        String imageUrl = foodResponse.getImageUrl();
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        if (deleteFile(fileName)) {
            foodRepository.deleteById(foodId);
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteFile(String filename) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build();
        if (awsCredential) {
            s3Client.deleteObject(deleteObjectRequest);
            return true;
        }
        return true;
    }

    private FoodResponse convertToResponse(FoodEntity foodEntity) {
        return FoodResponse.builder()
                .id(foodEntity.getId())
                .name(foodEntity.getName())
                .description(foodEntity.getDescription())
                .category(foodEntity.getCategory())
                .price(foodEntity.getPrice())
                .imageUrl(foodEntity.getImageUrl())
                .build();
    }


    private FoodEntity convertToEntity(FoodRequest request) {
        return FoodEntity.builder()
                .name(request.getName())
                .category(request.getCategory())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
    }

}
