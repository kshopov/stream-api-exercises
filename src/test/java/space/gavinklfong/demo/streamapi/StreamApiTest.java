package space.gavinklfong.demo.streamapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

		assertEquals(50, orders.size());
	}

	@Test
	@DisplayName("Obtain a list of product with category = \"Toys\" and then apply 10% discount")
	public void obtainListOfProductWithCategoryToysAndThenApply10PercentDiscount() {
		List<Product> products = productRepository.findAll();

		List<Product> discountedProducts = products.stream()
			.filter(p -> p.getCategory().equals("Toys"))
			.map(p -> p.withPrice(p.getPrice() * 0.9))
			.collect(Collectors.toList());

		assertEquals(50, discountedProducts.size());
		
	}

	@Test
	@DisplayName("Obtain a list of products ordered by customer of tier 2 between 01-Feb-2021 and 01-Apr-2021")
	public void obtainListOfProductsOrderedByCustomerOfTier2Between01Feb2021And01Apr2021() {
		List<Product> products =  orderRepository.findAll()
			.stream()
			.filter(o -> o.getCustomer().getTier() == 2)
			.filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 2, 1)) >= 0)
  			.filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 4, 1)) <= 0)
			.flatMap(o -> o.getProducts().stream())
			.distinct()
			.collect(Collectors.toList());

		assertEquals(19, products.size());
	}

	@Test
	@DisplayName("Get the cheapest products of \"Books\" category")
	public void getCheapestProductsOfBooksCategory() {
		List<Product> products = productRepository.findAll();
		Optional<Product> product = products.stream()
			.filter(p -> p.getCategory().equals("Books"))
			.min(Comparator.comparing(Product::getPrice));
		
			assertTrue(product.isPresent());
	}

	@Test
	@DisplayName("Get the 3 most recent placed order")
	public void getMostRecentPlacedOrder() {
		List<Order> orders = orderRepository.findAll();
		List<Order> recentOrders = orders.stream()
			.sorted(Comparator.comparing(Order::getOrderDate).reversed())
			.limit(3)
			.collect(Collectors.toList());

		assertEquals(3, recentOrders.size());
	}

	@Test
	@DisplayName("Get a list of orders which were ordered on 15-Mar-2021, log the order records to the console and then return its product list")
	public void getOrdersAndPrintToConsoleAndThenReturnProductList() {
		List<Order> orders = orderRepository.findAll();
		List<Product> products = orders.stream()
			.filter(o -> o.getOrderDate().equals(LocalDate.of(2021, 3, 15)))
			.peek(o -> System.out.println(o.toString()))
			.flatMap(o -> o.getProducts().stream())
			.distinct()
			.collect(Collectors.toList());

		assertEquals(7, products.size());
	}

	@Test
	@DisplayName("Calculate total lump sum of all orders placed in Feb 2021")
	public void calculateTotalLumpSumOfAllOrdersPlacedInFeb2021() {
		List<Order> orders = orderRepository.findAll();
		Double sum = orders.stream()
			.filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 2, 1)) >= 0)
  			.filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 3, 1)) <= 0)
			.flatMap(o -> o.getProducts().stream())
				.mapToDouble(p -> p.getPrice())
				.sum();

		assertEquals(11995.36f, sum.doubleValue(), 0.001);		
	}

}
