package cloud.store.catalog;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "/api/catalog")
public class CatalogController {

    private final ProductRepository repository;

    public CatalogController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Product> getAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
    }
}
