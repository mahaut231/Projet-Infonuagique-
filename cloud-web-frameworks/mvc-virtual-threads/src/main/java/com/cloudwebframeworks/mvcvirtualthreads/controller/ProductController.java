package com.cloudwebframeworks.mvcvirtualthreads.controller;

import com.cloudwebframeworks.mvcvirtualthreads.dto.Dtos.ProductResponse;
import com.cloudwebframeworks.mvcvirtualthreads.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        return productService.findById(id);
    }
}
