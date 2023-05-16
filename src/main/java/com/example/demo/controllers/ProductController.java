package com.example.demo.controllers;


import com.example.demo.models.Product;
import com.example.demo.models.ResponseObject;
import com.example.demo.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1/Products")
public class ProductController {
    @Autowired
    private ProductRepository  repository;
    @GetMapping("")
    List<Product> getAllProducts(){
        return  repository.findAll();
    }
    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> finProduct(@PathVariable Long id){
        /*  return repository.findById(id).orElseThrow(() ->new RuntimeException("ERR! cancel find product"));*/
        Optional<Product> foundProduct = repository.findById(id);
        if(foundProduct.isPresent()){
            return  ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("Succsess","Query product successfully",foundProduct)
            );
        }else{
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed","Cannot find product with id = "+id,null)
            );
        }
    }
    @PostMapping("/insert")
    ResponseEntity<ResponseObject> insertProduct(@RequestBody Product newProduct){
        List<Product> foundProducts = repository.findByProductName(newProduct.getProductName().trim());
        if(foundProducts.size() > 0){
            return  ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed","Product Name already taken","")
            );
        }
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success","Insert Product Successfully",repository.save(newProduct))
            );

    }
    @PutMapping("/{id}")
    ResponseEntity<ResponseObject> updateProduct(@RequestBody Product newProduct, @PathVariable Long id){
              Product foundProduct =  repository.findById(id)
                      .map(product -> {
                  product.setProductName(newProduct.getProductName());
                  product.setYear(newProduct.getYear());
                  product.setPrice(newProduct.getPrice());
                  return repository.save(product);
              }).orElseGet(() ->{
                  newProduct.setId(id);
                  return  repository.save(newProduct);
              });
             return ResponseEntity.status(HttpStatus.OK).body(
                     new ResponseObject("success","Update Product Successfully",foundProduct)
             );
    } 
}
