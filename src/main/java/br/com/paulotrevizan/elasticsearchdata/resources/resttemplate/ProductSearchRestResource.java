package br.com.paulotrevizan.elasticsearchdata.resources.resttemplate;

import br.com.paulotrevizan.elasticsearchdata.domains.Product;
import br.com.paulotrevizan.elasticsearchdata.services.resttemplate.ProductSearchRestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products/rest")
@RequiredArgsConstructor
public class ProductSearchRestResource {

    private final ProductSearchRestService productRestSearchService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<String> create(@Valid @RequestBody final Product request) {
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(productRestSearchService.createProductIndex(request)).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/bulk")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<List<IndexedObjectInformation>> createBulkIndex(
            @Valid @RequestBody final List<Product> request) {
        return ResponseEntity.ok(productRestSearchService.createProductIndexByBulk(request));
    }

    @GetMapping("/manufacturer")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<List<Product>> getProductsByManufacturer(
            @RequestParam(name = "manufacturer") final String manufacturer) {
        return ResponseEntity.ok(productRestSearchService.findProductsByManufacturer(
                manufacturer));
    }

    @GetMapping("/name")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<List<Product>> getProductsByName(
            @RequestParam(name = "name") final String name) {
        return ResponseEntity.ok(productRestSearchService.findProductsByName(name));
    }

    @GetMapping("/price")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<List<Product>> getProductsByPrice(
            @RequestParam(name = "price") final Double price) {
        return ResponseEntity.ok(productRestSearchService.findProductsByPrice(price));
    }

    @GetMapping("/search")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<List<Product>> getProductsBySearch(
            @RequestParam(name = "query") final String query) {
        return ResponseEntity.ok(productRestSearchService.processSearch(query));
    }

    @GetMapping("/suggestion")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<List<Product>> getProductsBySuggestions(
            @RequestParam(name = "query") final String query) {
        return ResponseEntity.ok(productRestSearchService.fetchSuggestions(query));
    }

}
