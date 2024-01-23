CREATE TABLE BASKET_PRODUCTS
(
	BASKET_ID varchar(50),
    PRODUCT_NAME varchar(50),
    AMOUNT int,
    primary key (BASKET_ID, PRODUCT_NAME),
    foreign key (PRODUCT_NAME) references products(PRODUCT_NAME),
	foreign key (BASKET_ID) references baskets(BASKET_ID)
)