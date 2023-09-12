package space.gavinklfong.demo.streamapi;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import space.gavinklfong.demo.streamapi.models.Order;
import space.gavinklfong.demo.streamapi.models.Product;
import space.gavinklfong.demo.streamapi.repos.OrderRepo;
import space.gavinklfong.demo.streamapi.repos.ProductRepo;

@DataJpaTest
public class StreamApiTest {

	@Autowired
	ProductRepo productRepository;

	@Autowired
	OrderRepo orderRepository;

	@Autowired
	ProductRepo productRepo;

	@Test
	@DisplayName("Obtain a list of product with category = \"Books\" and price > 100")
	public void obtainListOfProductWithCategoryBooksAndPriceGreaterThan100() {
		List<Product> products = productRepository.findAll();
		List<Product> collect = products.stream()
			.filter(p -> p.getCategory().equals("Books"))
			.filter(p -> p.getPrice() > 100)
			.collect(Collectors.toList());
			
		
		assertEquals(5, collect.size());
	}

	@Test
	@DisplayName("Obtain a list of order with products belong to category \"Baby\"")
	public void obtainListOfOrderWithProductsBelongToCategoryBaby() {
		List<Order> orders = orderRepository.findAll();
		orders.stream()
			.filter(o -> o.getProducts().stream()
				.anyMatch(p -> p.getCategory().equals("Baby")))
			.collect(Collectors.toList());
	}

	@Test
	@DisplayName("Obtain a list of product with category = \"Toys\" and then apply 10% discount")
	public void obtainListOfProductWithCategoryToysAndThenApply10PercentDiscount() {
		List<Product> products = productRepository.findAll();
	}
}
