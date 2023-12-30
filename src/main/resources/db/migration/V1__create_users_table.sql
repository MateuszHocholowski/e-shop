CREATE TABLE USERS
(
	ID bigint PRIMARY KEY unique not null auto_increment,
	LOGIN varchar(20),
    PASSWORD varchar(20)
)