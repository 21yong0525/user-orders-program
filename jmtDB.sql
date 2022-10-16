drop database if exists jmtdb;
create database if not exists jmtdb;

use jmtdb;

-- [테이블 생성]
drop table if exists users;
create table if not exists users(		-- 유저테이블
	userid varchar(10) not null,		-- 아이디
	password varchar(10) null,			-- 비밀번호
	name varchar(5) not null,			-- 이름
	phone char(13) not null,  			-- 전화번호
	int unsigned null default 0, 		-- 충전금액
	amount int unsigned null default 0,	-- 구매금액
	grade char(1) null default 'D',		-- 등급
	constraint pk_userid primary key (userid)
);

drop table if exists stores;
create table if not exists stores(		-- 매장 테이블
	storeName varchar(10) not null,		-- 이름 (pk)
	storePhone char(12) not null,		-- 전화번호
	constraint pk_storeName primary key (storeName)
);


drop table if exists products;
create table if not exists products (	-- 상품 테이블
	no int not null auto_increment,		-- 번호 (pk)
	storeName varchar(10) not null,		-- 매장 이름 (fk)->(store_storeName)
	productName varchar(10) not null, 	-- 이름 (uk)
price mediumint unsigned not null,		-- 가격
	stock int null default 0,			-- 재고
constraint pk_no primary key(no),
constraint uk_productName unique(productName),
constraint fk_products_stores_storeName foreign key(storeName) references stores(storeName) on delete cascade
);

drop table if exists orders;
create table if not exists orders(		-- 주문 테이블
orderNo int not null auto_increment,	-- 주문 번호 (pk)
userid varchar(10) not null,			-- 유저아이디 (fk)->(users_userid)
storeName varchar(10) not null,			-- 매장 이름 (fk)->(store_storeName)
productName varchar(10) not null, 		-- 상품 이름 
orderamount tinyint unsigned not null,	-- 주문 수량
	orderaccount int null default 0,	-- 주문 가격
	date datetime not null,			-- 주문 일시
   	constraint pk_orderNo primary key(orderNo),
    	constraint fk_orders_users_userid foreign key(userid) references users(userid) on delete cascade,
	constraint fk_orders_stores_storeName foreign key(storeName) references stores(storeName) on update cascade on delete cascade,
    	constraint fk_orders_products_productName foreign key(productName) references products(productName) on update cascade on delete cascade,
   	index idx_date (date)
);

drop table if exists csService;
create table if not exists csService(	-- 고객센터 게시판 테이블
	csNo int not null auto_increment,	-- 번호 (pk)
userid varchar(10) not null,			-- 아이디 (fk)->(users_userid)
	question text(50) not null,			-- 문의글내용
	constraint pk_csNo primary key(csNo),	
constraint fk_csService_users_userid foreign key(userid) references users(userid) on delete cascade
);

-- [프로시져]
-- 회원가입 procedure
drop procedure if exists signup;
DELIMITER $$
create procedure signup (
In userid char(10),
In password varchar(10),
In name varchar(5),
In phone char(13)
)
begin
insert into users(userid, password, name, phone) values(userid, password, name, phone);
end $$
delimiter ;
call signup('leehanyong','123456','이한용','010-8680-0713');

-- 유저 정보 출력 procedure
drop procedure if exists userinfo;
DELIMITER $$
create procedure userinfo (
IN in_userid char(10)
)
begin
select userid, INSERT(password, 3, 2, '**') password , name, phone, wallet, amount, grade from users where userid = in_userid;
end $$
delimiter ;

call userinfo('leehanyong');

-- 유저 정보 수정 procedure
drop procedure if exists userupdate;
DELIMITER $$
create procedure userupdate (
	IN in_password char(10),
IN in_phone char(13),
IN in_userid char(10)
)
begin
	 update users set password = in_password , phone = in_phone where userid = in_userid;
end $$
delimiter ;
call userupdate(?,?,?);

-- 매장 , 상품 출력 procedure
drop procedure if exists select_store_products;
DELIMITER $$
create procedure select_store_products (
	IN in_storename char(10),
    IN in_productname char(10)
)
begin
	SELECT *
	FROM stores s
    INNER JOIN products p
    ON s.storename = p.storename
    where s.storename like in_storename and p.productName like in_productname;
end $$
delimiter ;

call select_store_products ('규선이네','%%');
call select_store_products ('%%','%%');

-- 유저 등급 계산 procedure
drop procedure if exists update_grade;
DELIMITER $$
create procedure update_grade (
    IN in_userid varchar(10)
)
begin
declare in_grade char(1);
declare in_amount int;
set in_amount = (select amount from users where userid = in_userid);
case
when in_amount >= 90000 then set in_grade = 'A';
		when in_amount >= 60000 then set in_grade = 'B';
		when in_amount >= 30000 then set in_grade = 'C';
		when in_amount >= 0 then set in_grade = 'D';
	END case;
update users set grade = in_grade where userid = in_userid;
end $$
delimiter ;

call update_grade (); 

-- 주문 procedure
drop procedure if exists insert_orders;
DELIMITER $$
create procedure insert_orders (
IN in_userid varchar(10),
	IN in_storename char(10),
IN in_productname char(10),
IN in_orderamount tinyint unsigned
)
begin
	declare totalaccount int;
declare productstock int;
	set totalaccount = (in_orderamount*(select price from products where productname = in_productname));
	-- 총 상품 가격 계산
set productstock = (select stock from products where productname = in_productname);
- 상품 재고 수량과 주문 수량을 비교하여 주문
	if (productstock >= in_orderamount) then
insert into orders values(0,in_userid,in_storename,in_productname,in_orderamount,totalaccount,now());
else select '재고가 없어주문이 불가합니다.';
    end if;
end $$
delimiter ;

call insert_orders ('leehanyong','규선이네','치킨',1);

-- 문의글 입력 procedure
drop procedure if exists  insert_csService;
DELIMITER $$
create procedure insert_csService (
	IN in_userid varchar(10),
    	IN in_question text(50)
)
begin
	insert into csService values(0,in_userid,in_question);
end $$
delimiter ;

call insert_csService('leehanyong','치킨이 먹고 싶어요');

-- 로그인 유저 문의글 출력 procedure
drop procedure if exists select_csService;
DELIMITER $$
create procedure select_csService (
	IN in_userid varchar(10)
)
begin
	select * from csService where userid = in_userid;
end $$
delimiter ; 

call select_csService('leehanyong');  
-- [트리거]
-- 주문시 테이블들을 설정해주는 트리거
DROP TRIGGER IF EXISTS trg_orders;
DELIMITER $$
CREATE TRIGGER trg_orders
AFTER INSERT 
ON orders
FOR EACH ROW 
BEGIN
declare totalaccount int;
set totalaccount = (new.orderamount*(select price from products where productname = new.productname));
	update products set stock = stock - new.orderamount where productName = new.productName;
-- 주문 수량만큼 재고를 감소
	update users set wallet = wallet - totalaccount where userid = new.userid;
-- 주문 가격만큼 유저의 충전잔액 감소
update users set amount = amount + totalaccount where userid = new.userid;
-- 주문 가격만큼 유저의 총구매액 증가
call update_grade (new.userid);
-- 유저의 등급 재설정
END $$
DELIMITER ;

 
-- [펑션]
-- 등록된 상품 개수 출력 function
DELIMITER $$
create function getproductFunc()
    returns int
begin
    declare countProduct int;
    set countProduct = (select count(*) from products);
    return countProduct;
end $$
DELIMITER ;

SELECT getproductFunc();

-- 로그인 유저가 주문 수 출력 function
DELIMITER $$
create function getOrderFunc(name varchar(10))
    returns int
begin
    declare countorder int;
    set countorder = (select count(*) from orders where userid = name);
    return countorder;
end $$
DELIMITER ;

SELECT getOrderFunc('leehanyong');
 
