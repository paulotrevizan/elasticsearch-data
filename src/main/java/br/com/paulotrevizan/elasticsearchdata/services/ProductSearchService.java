package br.com.paulotrevizan.elasticsearchdata.services;

import br.com.paulotrevizan.elasticsearchdata.domains.Product;
import br.com.paulotrevizan.elasticsearchdata.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;

    public List<Product> createProductIndexByBulk(final List<Product> products) {
        return (List<Product>) productRepository.saveAll(products);
    }

    public Product createProductIndex(final Product product) {
        return productRepository.save(product);
    }

    public Product findById(final String id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> findByName(final String name) {
        return productRepository.findByName(name);
    }

    public List<Product> findByNameContaining(final String name) {
        return productRepository.findByNameContaining(name);
    }

    public List<Product> findByManufacturerAndCategory(final String manufacturer,
                                                       final String category) {
        return productRepository.findByManufacturerAndCategory(manufacturer, category);
    }

}
