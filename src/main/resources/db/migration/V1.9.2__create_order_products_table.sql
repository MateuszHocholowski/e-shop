CREATE TABLE ORDER_PRODUCTS
(
	ORDER_ID bigint,
    PRODUCT_ID bigint,
    AMOUNT int,
    primary key (ORDER_ID, PRODUCT_ID),
    foreign key (ORDER_ID) references orders(ID),
    foreign key (PRODUCT_ID) references products(ID)
)