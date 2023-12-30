CREATE TABLE PRODUCTS
(
	ID bigint PRIMARY KEY unique not null auto_increment,
    PRODUCT_NAME varchar(50),
    NET_PRICE double,
    GROSS_PRICE double,
    DESCRIPTION varchar(255),
    AMOUNT int,
    image tinyblob,
    USER_ID bigint,
    ORDER_ID bigint,
    FOREIGN KEY (USER_ID) REFERENCES USERS (ID),
    FOREIGN KEY (ORDER_ID) REFERENCES ORDERS (ID)
)