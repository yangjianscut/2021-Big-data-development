drop table if exists shopping_db.user_info;
create table shopping_db.user_info
(
	user_id INT primary key,
	phone_number VARCHAR(11),
	user_name VARCHAR(5),
	ID_Number VARCHAR(18),
	Register_time DATE,
	pet_name VARCHAR(12),
	sex VARCHAR(2),
                age int,
                email VARCHAR(12)
);
-- Add comments to the table 
comment on table shopping_db.user_info
  is '�û���Ϣ��';
-- Add comments to the columns 
comment on column shopping_db.user_info.user_id
  is '�û�id';
comment on column shopping_db.user_info.phone_number
  is '�ֻ�����';
 comment on column shopping_db.user_info.user_name
  is '�û�����';
 comment on column shopping_db.user_info.ID_Number
  is '���֤����';
 comment on column shopping_db.user_info.Register_time
  is 'ע��ʱ��';
 comment on column shopping_db.user_info.pet_name
  is '�û��ǳ�';
  comment on column shopping_db.user_info.sex
  is '�û��Ա�';
  comment on column shopping_db.user_info.AGE
  is '�û�����';
  comment on column shopping_db.user_info.email
  is '�����ַ';


--�û���ַ��
drop table if exists shopping_db.user_address;
create table shopping_db.user_address
(
	user_id int primary key,
	provience VARCHAR(5),
	city VARCHAR(5),
	county VARCHAR(5),
	specific_address VARCHAR(10)
);
-- Add comments to the table 
comment on table shopping_db.user_address
  is '�û���ַ��'; 
 -- Add comments to the columns 
comment on column shopping_db.user_address.user_id
  is '�û�id';
 comment on column shopping_db.user_address.provience
  is 'ʡ��';
 comment on column shopping_db.user_address.city
  is '��';
 comment on column shopping_db.user_address.county
  is '��';
 comment on column shopping_db.user_address.specific_address
  is '�����ַ';
 
 --�û���½��
 drop table if exists shopping_db.login;
 create table shopping_db.login
 (
 	user_id INT primary key ,
 	phone_number VARCHAR(11),
 	passwords VARCHAR(10)
 );
 -- Add comments to the table 
comment on table shopping_db.login
  is '�û���½��'; 
 -- Add comments to the columns 
comment on column shopping_db.login.user_id
  is '�û�id';
 comment on column shopping_db.login.phone_number
  is '�û��˺�';
 comment on column shopping_db.login.passwords
  is '�û�����';
 
 --�û����ﳵ��
 drop table if exists shopping_db.shopping_cart;
 create table shopping_db.shopping_cart
 (
 	user_id INT ,
 	sku_id INT ,
 	numbers INT,
 	total_price INT
 );
 -- Add comments to the table 
comment on table shopping_db.shopping_cart
  is '�û����ﳵ��'; 
 -- Add comments to the columns 
comment on column shopping_db.shopping_cart.user_id
  is  '�û�id';
comment on column shopping_db.shopping_cart.sku_id
  is  '��Ʒid';
 comment on column shopping_db.shopping_cart.numbers
  is  '��Ʒ����';
 comment on column shopping_db.shopping_cart.total_price
  is  '�ܼ۸�';
 
 --������
 drop table if exists shopping_db.orders;
 create table shopping_db.orders
 (
 	id INT primary key,
 	user_id INT ,
 	sku_id INT ,
 	numbers INT,
 	total_prices INT,
 	order_month_time INT
 );
  -- Add comments to the table 
comment on table shopping_db.orders
  is '������Ϣ��'; 
comment on column shopping_db.orders.id
  is '����id';
comment on column shopping_db.orders.user_id
  is '�û�id';
comment on column shopping_db.orders.sku_id
  IS '��Ʒid';
comment on column shopping_db.orders.numbers
  IS '��Ʒ����';
comment on column shopping_db.orders.total_prices
  IS  '�ܼ۸�';
comment on column shopping_db.orders.order_month_time
  IS  '��������ʱ��';

 --���۶��ܱ�
 DROP TABLE IF EXISTS shopping_db.sales;
 CREATE TABLE shopping_db.sales
 (
 	sex varchar(2),
 	months int,
 	city varchar(4),
 	category varchar(4),
 	pro_sum int
 );
-- Add comments to the table 
comment on table shopping_db.sales
  is '������Ϣ�ܱ�'; 
comment on column shopping_db.sales.sex
  IS  '�Ա�';
comment on column shopping_db.sales.months
  IS  '�·�';
comment on column shopping_db.sales.city
  IS  '����';
comment on column shopping_db.sales.category
  IS  '��Ʒ���';
comment on column shopping_db.sales.pro_sum
  IS  '�����ܶ�';
 
 --�����з�������۱�
 DROP TABLE IF EXISTS shopping_db.sale_city;
 CREATE TABLE shopping_db.sale_city
 (
 	city VARCHAR(4),
 	months int,
 	pro_sum int
 );
 -- Add comments to the table 
comment on table shopping_db.sale_city
  is '������Ʒ�����ܱ�'; 
comment on column shopping_db.sale_city.city
  IS '����';
 comment on column shopping_db.sale_city.months
  IS '�·�';
 comment on column shopping_db.sale_city.pro_sum
  IS '�ܶ�';
 
 --���Ա��������۱�
 DROP TABLE IF EXISTS shopping_db.sale_sex;
 CREATE TABLE shopping_db.sale_sex
 (
 	sex VARCHAR(2),
 	months int,
 	pro_sum int
 );
 -- Add comments to the table 
comment on table shopping_db.sale_sex
  is '���Ա������Ʒ�����ܱ�'; 
comment on column shopping_db.sale_sex.sex
  IS '�Ա�';
comment on column shopping_db.sale_sex.months
  IS '�·�';
comment on column shopping_db.sale_sex.pro_sum
  IS '�����ܶ�';
 
 --����Ʒ���ֵ������ܶ��
 DROP TABLE IF EXISTS shopping_db.sale_cate;
 CREATE TABLE shopping_db.sale_cate
 (
 	category VARCHAR(4),
 	months int,
 	pro_sum int
 );
 -- Add comments to the table 
comment on table shopping_db.sale_cate
  is '����Ʒ�����Ʒ�����ܱ�'; 
comment on column shopping_db.sale_cate.category
  IS '��Ʒ���';
comment on column shopping_db.sale_cate.months
  IS '�·�';
comment on column shopping_db.sale_cate.pro_sum
  IS '�����ܶ�';
 
 --�̼���Ϣ
DROP TABLE IF EXISTS shopping_db.seller_info;
CREATE TABLE shopping_db.seller_info
(
	seller_id INT PRIMARY KEY,
	name VARCHAR(5),
	account VARCHAR(10),
	passwords VARCHAR(10)
);
-- Add comments to the table 
comment on table shopping_db.seller_info
  is '������Ϣ��'; 
comment on column shopping_db.seller_info.seller_id
  IS  '����id';
comment on column shopping_db.seller_info.passwords
  IS  '����';
comment on column shopping_db.seller_info.account
  IS  '�˻�';

--��Ʒ�����
DROP TABLE IF EXISTS shopping_db.product_cate;
CREATE TABLE shopping_db.product_cate
(
	spu_id int PRIMARY KEY,
	product_name VARCHAR(5),
	category VARCHAR(4)
);
-- Add comments to the table 
comment on table shopping_db.product_cate
  is '��Ʒ�����';
comment on column shopping_db.product_cate.spu_id
  IS 'id';
comment on column shopping_db.product_cate.product_name
  IS '��Ʒ����';
comment on column shopping_db.product_cate.category
  IS '��Ʒ���';

 --������Ʒ��Ϣ��
 DROP TABLE IF EXISTS shopping_db.seller_pro;
 CREATE TABLE shopping_db.seller_pro
 (
 	sku_id int PRIMARY KEY,
 	spu_id int ,
 	seller_id int ,
 	numbers int,
 	prices int
 );
 -- Add comments to the table 
comment on table shopping_db.seller_pro
 IS '������Ʒ��Ϣ��';
comment on column shopping_db.seller_pro.sku_id
 IS '��Ʒid';
comment on column shopping_db.seller_pro.spu_id
 IS '��Ʒ���id';
comment on column shopping_db.seller_pro.seller_id
 IS '�̼�id';
comment on column shopping_db.seller_pro.numbers
 IS '�����';
comment on column shopping_db.seller_pro.prices
 IS '�۸�';

drop table if exists shopping_db.users_allinfo;
create table shopping_db.users_allinfo
(
	months int,
	users_new_register int,
	users_registered int
);
-- Add comments to the table 
comment on table shopping_db.users_allinfo
  is '�û���Ϣͳ�Ʊ�';
-- Add comments to the columns 
comment on column shopping_db.users_allinfo.months
  is '�·�';
comment on column shopping_db.users_allinfo.users_new_register
  is '����ע������';
comment on column shopping_db.users_allinfo.users_registered
  is '�û�����';