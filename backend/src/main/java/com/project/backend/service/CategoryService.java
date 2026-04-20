package com.project.backend.service;

import com.project.backend.entity.Category;
import com.project.backend.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public ResponseEntity<?> saveCategory(Category category){
//        if(category.getCatname() != null && categoryRepository.existsByCatname(category.getCatname())){
//            throw new RuntimeException("category already exists.");
//        }
        if(category.getCatname() != null && categoryRepository.existsByCatname(category.getCatname())){
            return new ResponseEntity<>("Category already exists",HttpStatus.FOUND);
        }
        Category save = categoryRepository.save(category);
        return new ResponseEntity<>(save,HttpStatus.OK);
    }

    public List<Category> getAll(){
        return categoryRepository.findAll();
    }

    public Category getById(Long id){
        return categoryRepository.findById(id).orElseThrow(()->
                new RuntimeException("Category not found"));
    }

    public ResponseEntity<?> update(Long id, Category category){
        Optional<Category> byId = categoryRepository.findById(id);
        if(byId.isPresent()){
            Category category1 = byId.get();
            category1.setCatname(category.getCatname());
            category1.setImgurl(category.getImgurl());

            Category save = categoryRepository.save(category1);
            return ResponseEntity.ok(save);
        }
        return new ResponseEntity<>("category not found...", HttpStatus.NOT_FOUND);
    }

//    public String delete(Long id){
//        Optional<Category> byId = categoryRepository.findById(id);
//        if(byId.isPresent()){
//            categoryRepository.deleteById(id);
//            return "record deleted successfully...";
//        }
//        return "record not found...";
//    }
public ResponseEntity<?> delete(Long id){
    Optional<Category> byId = categoryRepository.findById(id);
    if(byId.isPresent()) {
        categoryRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Record Deleted"));
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("message", "Category Not Found"));
}
}
