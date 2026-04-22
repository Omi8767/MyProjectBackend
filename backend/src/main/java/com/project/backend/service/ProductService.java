package com.project.backend.service;

import com.project.backend.dto.ProductDTO;
import com.project.backend.dto.SpecificationDTO;
import com.project.backend.entity.*;
import com.project.backend.repository.CategoryRepository;
import com.project.backend.repository.CustomerRepository;
import com.project.backend.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CustomerRepository customerRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, CustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.customerRepository = customerRepository;
    }

    public ResponseEntity<?> create(ProductDTO productDTO){
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setUnit(productDTO.getUnit());
        product.setStock(productDTO.getStock());
        product.setAvailable(productDTO.getAvailable()!=null?productDTO.getAvailable():true);

        Optional<Customer> bySellerId = customerRepository.findById(productDTO.getSellerId());
        if(bySellerId.isPresent()){
            Customer seller = bySellerId.get();
            product.setSeller(seller);
        }else{
            return new ResponseEntity<>("Seller Not found",HttpStatus.NOT_FOUND);
        }


        Optional<Category> byCategoryId = categoryRepository.findById(productDTO.getCategoryId());
        if(byCategoryId.isPresent()){
            Category category = byCategoryId.get();
            product.setCategory(category);
        }
        else{
            return new ResponseEntity<>("Category not found", HttpStatus.NOT_FOUND);
        }
//        ProductImages
        if(productDTO.getImageurls()!= null){
            boolean isFirst= true;

            for(String imgUrl: productDTO.getImageurls()){
                ProductImages img = new ProductImages();

                img.setImageUrl(imgUrl);
                img.setProduct(product);
                img.setPrimary(isFirst);
                isFirst=false;
                product.getImages().add(img);
            }

        }
//        ProductSpecification

        if(productDTO.getSpecifications()!=null){
            for(SpecificationDTO specificationDTO :productDTO.getSpecifications()){
                ProductSpecification specification = new ProductSpecification();

                specification.setName(specificationDTO.getName());
                specification.setValue(specificationDTO.getValue());
                specification.setProduct(product);
                product.getSpecifications().add(specification);


            }
        }
        Product save = productRepository.save(product);

        return ResponseEntity.ok(save);


    }

//  UPADTE
    public ResponseEntity<?> update(Long id,ProductDTO productDTO){
        Optional<Product> byId = productRepository.findById(id);
        if(byId.isPresent()){
            Product product = byId.get();
            product.setName(productDTO.getName());
            product.setPrice(productDTO.getPrice());
            product.setUnit(productDTO.getUnit());
            product.setStock(productDTO.getStock());
            product.setAvailable(productDTO.getAvailable());

            Optional<Customer> bySellerId1 = customerRepository.findById(productDTO.getSellerId());
            if(bySellerId1.isPresent()){
                Customer seller = bySellerId1.get();
                product.setSeller(seller);
            }else{
                return new ResponseEntity<>("Seller Not found",HttpStatus.NOT_FOUND);
            }

            Optional<Category> byIdCategory = categoryRepository.findById(productDTO.getCategoryId());
            if(byIdCategory.isPresent()){
                Category category = byIdCategory.get();
                product.setCategory(category);
            }
            else {
                return new ResponseEntity<>("Category not found", HttpStatus.NOT_FOUND);
            }

            if(productDTO.getImageurls() !=null && !productDTO.getImageurls().isEmpty()){
                product.getImages().clear();

                boolean isPrimary=true;
                for(String imgUrl:productDTO.getImageurls()){
                    ProductImages img = new ProductImages();
//                    img.setImageUrl(img.getImageUrl());
                    img.setImageUrl(imgUrl);
                    img.setProduct(product);
                    img.setPrimary(isPrimary);
                    isPrimary=false;
                    product.getImages().add(img);
                }
            }
            if(productDTO.getSpecifications() !=null){
                product.getSpecifications().clear();
                for(SpecificationDTO s:productDTO.getSpecifications()){
                    ProductSpecification specification = new ProductSpecification();
                    specification.setName(s.getName());
                    specification.setValue(s.getValue());
                    specification.setProduct(product);
                    product.getSpecifications().add(specification);
                }
            }
            Product save = productRepository.save(product);
            return new ResponseEntity<>(save,HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Product Not found",HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> delete(Long id){
        Optional<Product> byId = productRepository.findById(id);
        if(byId.isPresent()){
            Product product = byId.get();
            product.getImages().clear();//newlyadded
            product.getSpecifications().clear();//newlyadded
            productRepository.deleteById(id);
//            return new ResponseEntity<>("Data deleted",HttpStatus.OK);
            return  ResponseEntity.ok().body(Map.of("message","Deleted successfully "));
        }
//        return new ResponseEntity<>("Product not found",HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Product not found"));

    }

    public ResponseEntity<List<Product>> getAllProduct(){
        List<Product> all = productRepository.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    public ResponseEntity<?> getById(Long id){
        Optional<Product> byId = productRepository.findById(id);
        if(byId.isPresent()){
            Product product = byId.get();
            return new ResponseEntity<>(product,HttpStatus.OK);
        }
        return new ResponseEntity<>("Product not found",HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<Product>> getBySellerId(Long id){
        List<Product> bySellerId = productRepository.findBySeller_Id(id);
        return ResponseEntity.ok(bySellerId);
    }

    public ResponseEntity<List<Product>> getByCategoryId(Long id){
        List<Product> byCategoryId = productRepository.findByCategory_Id(id);
        return ResponseEntity.ok(byCategoryId);
    }

}
