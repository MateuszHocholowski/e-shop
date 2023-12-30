CREATE TABLE ORDERS
(
	ID bigint PRIMARY KEY unique not null auto_increment,
	ORDER_DATE datetime,
    ADMISSION_DATE datetime,
    PAYMENT_DATE datetime,
    REALIZATION_DATE datetime,
    TOTAL_PRICE double,
    USER_ID bigint,
    FOREIGN KEY (USER_ID) REFERENCES USERS(ID)
)