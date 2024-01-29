package com.orzechazo.eshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.orzechazo.eshop.bootstrap.tests.BootstrapBasket;
import com.orzechazo.eshop.bootstrap.tests.BootstrapProduct;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.BasketRepository;
import com.orzechazo.eshop.repositories.ProductRepository;
import com.orzechazo.eshop.services.BasketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.orzechazo.eshop.bootstrap.tests.BootstrapBasket.DB_BASKET1_ID;
import static com.orzechazo.eshop.bootstrap.tests.BootstrapBasket.DB_BASKET1_TOTAL_PRICE;
import static com.orzechazo.eshop.bootstrap.tests.BootstrapProduct.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BasketControllerE2ETest {
    private static final String BASKET_ID_NOT_IN_DB = "basketNotInDb";
    private static final String PRODUCT_NOT_IN_DB = "productNotInDb";
    @Autowired
    private BasketRepository basketRepository;
    @Autowired
    private ProductRepository productRepository;
    private MockMvc mockMvc;
    private final ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private int DB_DEFAULT_BASKET_COUNT;
    @BeforeEach
    void setUp() {
        BasketServiceImpl basketService = new BasketServiceImpl(basketRepository,productRepository);

        BootstrapBasket bootstrap = new BootstrapBasket(basketRepository);
        bootstrap.loadData();
        BootstrapProduct bootstrapProduct = new BootstrapProduct(productRepository);
        bootstrapProduct.loadData();

        BasketController basketController = new BasketController(basketService);
        mockMvc = MockMvcBuilders.standaloneSetup(basketController).build();

        DB_DEFAULT_BASKET_COUNT = bootstrap.getBaskets().size();
    }

    @Test
    void getBasketByBasketId() throws Exception {
        mockMvc.perform(get("/baskets/" + DB_BASKET1_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketId",equalTo(DB_BASKET1_ID)))
                .andExpect(jsonPath("$.totalPrice",equalTo(DB_BASKET1_TOTAL_PRICE.intValue())));
    }

    @Test
    void getBasketByBasketIdThatIsNotInDB() throws Exception {
        mockMvc.perform(get("/baskets/" + BASKET_ID_NOT_IN_DB)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("Basket: " + BASKET_ID_NOT_IN_DB + " doesn't exist in database",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void createBasket() throws Exception {
        BasketDto newBasket = BasketDto.builder().totalPrice(new BigDecimal("2.5")).build();
        mockMvc.perform(put("/baskets/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(newBasket)));

        assertEquals(DB_DEFAULT_BASKET_COUNT+1, basketRepository.count());
    }

    @Test
    void updateBasket() throws Exception {
        BasketDto basketToUpdate = BasketDto.builder().basketId(DB_BASKET1_ID)
                .totalPrice(new BigDecimal("7")).build();
        mockMvc.perform(post("/baskets/update/" + DB_BASKET1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(basketToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketId",equalTo(DB_BASKET1_ID)))
                .andExpect(jsonPath("$.totalPrice",equalTo(new BigDecimal("7").intValue())));

        assertEquals(DB_DEFAULT_BASKET_COUNT,basketRepository.count());
    }

    @Test
    void testUpdateBasketWithIdNotInDB() throws Exception {
        BasketDto basketToUpdate = BasketDto.builder().basketId(BASKET_ID_NOT_IN_DB).build();

        mockMvc.perform(post("/baskets/update/" + BASKET_ID_NOT_IN_DB)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(basketToUpdate)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("Basket: " + BASKET_ID_NOT_IN_DB + " doesn't exist in database",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void deleteBasket() throws Exception {
        mockMvc.perform(delete("/baskets/delete/" + DB_BASKET1_ID))
                .andExpect(status().isOk());

        assertEquals(DB_DEFAULT_BASKET_COUNT-1, basketRepository.count());
    }

    @Test
    void addProductToEmptyBasketWithoutSpecifiedAmount() throws Exception {
        List<String> expectedProductNamesList = List.of(DB_PRODUCT1_NAME);

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/add/" + DB_PRODUCT1_NAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productNamesMap", hasEntry(DB_PRODUCT1_NAME, 1)))
                .andExpect(jsonPath("$.productNamesMap.keys()")
                        .value(containsInAnyOrder(expectedProductNamesList.toArray())));
    }

    @Test
    void addProductToEmptyBasketWithSpecifiedAmount() throws Exception{
        List<String> expectedProductNamesList = List.of(DB_PRODUCT1_NAME);
        int amountToAdd = DB_PRODUCT1_AMOUNT - 1;

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/add/" + DB_PRODUCT1_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("amount", Integer.toString(amountToAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productNamesMap", hasEntry(DB_PRODUCT1_NAME, amountToAdd)))
                .andExpect(jsonPath("$.productNamesMap.keys()")
                        .value(containsInAnyOrder(expectedProductNamesList.toArray())));
    }

    @Test
    void addTwoDifferentProductsToBasket() throws Exception {
        List<String> expectedProductNamesList = List.of(DB_PRODUCT1_NAME, DB_PRODUCT2_NAME);
        //given
        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/add/" + DB_PRODUCT1_NAME)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        //then
        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/add/" + DB_PRODUCT2_NAME)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productNamesMap", hasEntry(DB_PRODUCT1_NAME, 1)))
                .andExpect(jsonPath("$.productNamesMap", hasEntry(DB_PRODUCT2_NAME, 1)))
                .andExpect(jsonPath("$.productNamesMap.keys()")
                        .value(containsInAnyOrder(expectedProductNamesList.toArray())));
    }
    @Test
    void addProductToBasketTwiceWithDifferentAmount() throws Exception {
        int amountToAdd = DB_PRODUCT1_AMOUNT - 1;
        int expectedAmount = amountToAdd + 1;

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/add/" + DB_PRODUCT1_NAME)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/add/" + DB_PRODUCT1_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .param("amount", Integer.toString(amountToAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productNamesMap." + DB_PRODUCT1_NAME,
                        equalTo(expectedAmount)));
    }

    @Test
    void tryToChangeProductNumberInBasketWithWrongInput() {
            //todo add Exception to catch wrong input in controller
    }

    @Test
    void SubtractProductFromBasket() throws Exception {
        int expectedAmount = DB_PRODUCT1_AMOUNT - 1;
        List<String> expectedProductNamesList = List.of(DB_PRODUCT1_NAME);

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/add/" + DB_PRODUCT1_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .param("amount", Integer.toString(DB_PRODUCT1_AMOUNT)));

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/subtract/" + DB_PRODUCT1_NAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productNamesMap.keys()")
                        .value(containsInAnyOrder(expectedProductNamesList.toArray())))
                .andExpect(jsonPath("$.productNamesMap." + DB_PRODUCT1_NAME,
                        equalTo(expectedAmount)));


    }

    @Test
    void subtractTheSameProductTwiceWithoutRemovingFromBasket() throws Exception {
        int amountToSubtract = 2;
        int expectedAmount = DB_PRODUCT1_AMOUNT - 1 - amountToSubtract;
        List<String> expectedProductNamesList = List.of(DB_PRODUCT1_NAME);

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/add/" + DB_PRODUCT1_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .param("amount", Integer.toString(DB_PRODUCT1_AMOUNT)));

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/subtract/" + DB_PRODUCT1_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .param("amount", Integer.toString(amountToSubtract)));

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/subtract/" + DB_PRODUCT1_NAME)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productNamesMap.keys()")
                        .value(containsInAnyOrder(expectedProductNamesList.toArray())))
                .andExpect(jsonPath("$.productNamesMap." + DB_PRODUCT1_NAME,
                        equalTo(expectedAmount)));
    }

    @Test
    void removeProductFromBasket() throws Exception {

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/add/" + DB_PRODUCT1_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .param("amount", Integer.toString(DB_PRODUCT1_AMOUNT)));

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/remove/" + DB_PRODUCT1_NAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productNamesMap.keys()", empty()));
    }

    @Test
    void removeOnlyOneOfProductsFromBasket() throws Exception {
        List<String> expectedProductNamesList = List.of(DB_PRODUCT1_NAME);

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/add/" + DB_PRODUCT1_NAME)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/add/" + DB_PRODUCT2_NAME)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/remove/" + DB_PRODUCT2_NAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productNamesMap.keys()")
                        .value(containsInAnyOrder(expectedProductNamesList.toArray())));
        
    }

    @Test
    void subtractMoreOfProductThatIsInBasket() throws Exception {
        int amountToSubtract = DB_PRODUCT1_AMOUNT + 1;

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/add/" + DB_PRODUCT1_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .param("amount", Integer.toString(DB_PRODUCT1_AMOUNT)));

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/subtract/" + DB_PRODUCT1_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("amount", Integer.toString(amountToSubtract)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productNamesMap.keys()", empty()));
    }

    @Test
    void tryToAddProductThatIsNotInDb() throws Exception {
        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/add/" + PRODUCT_NOT_IN_DB)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("Product: "
                                + PRODUCT_NOT_IN_DB + " doesn't exist in database.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void tryToSubtractProductThatIsNotInBasket() throws Exception {
        List<String> expectedProductNamesList = List.of(DB_PRODUCT1_NAME);

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/add/" + DB_PRODUCT1_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .param("amount", Integer.toString(DB_PRODUCT1_AMOUNT)));

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/subtract/" + DB_PRODUCT1_NAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productNamesMap.keys()")
                        .value(containsInAnyOrder(expectedProductNamesList.toArray())));
        //todo add exception when product is not in the basket
    }

    @Test
    void tryToAddMoreOfProductThatIsInDb() throws Exception {
        int amountToAdd = DB_PRODUCT1_AMOUNT + 1;

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/add/" + DB_PRODUCT1_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .param("amount", Integer.toString(amountToAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productNamesMap",
                        hasEntry(DB_PRODUCT1_NAME, DB_PRODUCT1_AMOUNT)));
    }

    @Test
    void fetchAllProductsFromBasket() throws Exception {

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/add/" + DB_PRODUCT1_NAME)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/add/" + DB_PRODUCT2_NAME)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/baskets/" + DB_BASKET1_ID + "/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keys()" , hasSize(2)));
    }

    @Test
    void tryToSubtractProductFromBasketWithNegativeAmount() throws Exception {
        int negativeNumber = -5;
        mockMvc.perform(post("/baskets/" + DB_BASKET1_ID + "/products/subtract/" + DB_PRODUCT1_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                        .param("amount", Integer.toString(negativeNumber)))
                .andExpect(status().isOk());
        //todo add Wrong Input Exception when trying to subtract negative number
    }
}