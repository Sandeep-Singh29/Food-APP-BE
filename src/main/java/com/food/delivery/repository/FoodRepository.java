package com.food.delivery.repository;

import com.food.delivery.entity.FoodEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Author: Sandeep Singh
 * Date: 21/03/25
 */

@Repository
public interface FoodRepository extends MongoRepository<FoodEntity,String> {
}
