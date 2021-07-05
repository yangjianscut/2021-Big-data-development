select months, sum(pro_sum) 
from shopping_db.sales 
group by months;

--按月查看新增注册人数
select months,users_new_register
from shopping_db.users_allinfo 
order by months;

--按月查看城市销售额
select months,city,pro_sum
from shopping_db.sale_city;

--按月查看城市、商品类别的销售额
select shopping_db.sale_city.months,city,category, shopping_db.sale_city.pro_sum
from shopping_db.sale_city,shopping_db.sale_cate 
where shopping_db.sale_city.months = shopping_db.sale_cate.months;

--按月查看性别、商品类别的销售额
select shopping_db.sale_sex.months,sex,category,shopping_db.sale_sex.pro_sum
from shopping_db.sale_sex,shopping_db.sale_cate 
where shopping_db.sale_sex.months = shopping_db.sale_cate.months;