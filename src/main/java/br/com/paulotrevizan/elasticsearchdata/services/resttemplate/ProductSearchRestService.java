package br.com.paulotrevizan.elasticsearchdata.services.resttemplate;

import br.com.paulotrevizan.elasticsearchdata.domains.Product;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSearchRestService {

    private static final String PRODUCT_INDEX = "productindex";

    private final ElasticsearchOperations elasticsearchOperations;

    public List<IndexedObjectInformation> createProductIndexByBulk(final List<Product> products) {
        List<IndexQuery> queries = products.stream()
                .map(product -> new IndexQueryBuilder()
                        .withObject(product)
                        .build())
                .collect(Collectors.toList());

        return elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of(PRODUCT_INDEX));
    }

    public String createProductIndex(final Product product) {
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withObject(product)
                .build();

       return elasticsearchOperations.index(indexQuery, IndexCoordinates.of(PRODUCT_INDEX));
    }

    // NativeQuery
    public List<Product> findProductsByManufacturer(final String manufacturer) {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("manufacturer", manufacturer);

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();

        return getProductMatches(searchQuery);
    }

    // StringQuery
    public List<Product> findProductsByName(final String name) {
        Query searchQuery = new StringQuery(
                "{\"match\":{\"name\":{\"query\":\""+ name + "\"}}}\"");

        return getProductMatches(searchQuery);
    }

    // CriteriaQuery
    public List<Product> findProductsByPrice(final Double price) {
        Criteria criteria = new Criteria("price")
                .greaterThan(price)
                .lessThan(price * 2);

        Query searchQuery = new CriteriaQuery(criteria);

        return getProductMatches(searchQuery);
    }

    // FuzzySearch & MultiMatchQuery
    public List<Product> processSearch(final String query) {
        QueryBuilder queryBuilder = QueryBuilders
                .multiMatchQuery(query, "name", "description")
                .fuzziness(Fuzziness.AUTO);

        Query searchQuery = new NativeSearchQueryBuilder()
                .withFilter(queryBuilder)
                .build();

        return getProductMatches(searchQuery);
    }

    // Sugestões com Wildcard
    public List<Product> fetchSuggestions(final String query) {
        QueryBuilder queryBuilder = QueryBuilders
                .wildcardQuery("name", query + "*");

        Query searchQuery = new NativeSearchQueryBuilder()
                .withFilter(queryBuilder)
                .withPageable(PageRequest.of(0, 5))
                .build();

       return getProductMatches(searchQuery);
    }

    private List<Product> getProductMatches(Query searchQuery) {
        SearchHits<Product> productHits = getProductHits(searchQuery);

        List<Product> productMatches = new ArrayList<>();
        productHits.forEach(product ->
            productMatches.add(product.getContent())
        );

        return productMatches;
    }

    private SearchHits<Product> getProductHits(Query searchQuery) {
        return elasticsearchOperations.search(searchQuery,
                Product.class,
                IndexCoordinates.of(PRODUCT_INDEX));
    }

}
