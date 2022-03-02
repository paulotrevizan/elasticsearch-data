package br.com.paulotrevizan.elasticsearchdata.resources;

import br.com.paulotrevizan.elasticsearchdata.domains.Product;
import br.com.paulotrevizan.elasticsearchdata.services.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductSearchResource {

    private final ProductSearchService productSearchService;

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Product> getProductById(
            @PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(productSearchService.findById(id));
    }

    @GetMapping("/name")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<List<Product>> getProductByName(
            @RequestParam(name = "name") final String name) {
        return ResponseEntity.ok(productSearchService.findByName(name));
    }

    @GetMapping("/containing")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<List<Product>> getProductByNameContaining(
            @RequestParam(name = "name") final String name) {
        return ResponseEntity.ok(productSearchService.findByNameContaining(name));
    }

    @GetMapping("/manufacturer")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<List<Product>> getProductByManufacturerAndCategory(
            @RequestParam(name = "manufacturer") final String manufacturer,
            @RequestParam(name = "category") final String category) {
        return ResponseEntity.ok(productSearchService.findByManufacturerAndCategory(
                manufacturer, category));
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<Product> create(@Valid @RequestBody final Product request) {
        Product product = productSearchService.createProductIndex(request);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(product.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/bulk")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<List<Product>> createBulk(@Valid @RequestBody final List<Product> request) {
        return ResponseEntity.ok(productSearchService.createProductIndexByBulk(request));
    }

}
