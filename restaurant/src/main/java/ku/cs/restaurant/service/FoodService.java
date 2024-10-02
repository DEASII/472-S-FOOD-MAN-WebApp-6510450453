package ku.cs.restaurant.service;

import ku.cs.restaurant.entity.Food;
import ku.cs.restaurant.entity.Status;
import ku.cs.restaurant.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FoodService {
    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public Food createFood(Food food) {
        return foodRepository.save(food);
    }

    public List<Food> getFoodsByStatus(Status status) {
        return foodRepository.findFoodsByStatus(status);
    }

    public Optional<Food> getFoodById(UUID id) {
        return foodRepository.findFoodById(id);
    }

    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    public void deleteFoodById(UUID id) {
        foodRepository.deleteById(id);
    }
}
