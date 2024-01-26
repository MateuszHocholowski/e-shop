CREATE TABLE BASKET_PRODUCTS
(
	BASKET_ID bigint,
    PRODUCT_ID bigint,
    AMOUNT int,
    primary key (BASKET_ID, PRODUCT_ID),
    foreign key (BASKET_ID) references baskets(ID),
    foreign key (PRODUCT_ID) references products(ID)
)