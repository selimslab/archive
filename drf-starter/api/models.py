from django.db import models
from django.contrib.postgres.fields import JSONField
from baseline.settings import AUTH_USER_MODEL


# Create your models here.
class Product(models.Model):
    product_id = models.AutoField(primary_key=True)

    product_name = models.TextField(default="")
    brand = models.TextField(default="")
    category = models.TextField(default="")
    src = models.URLField(default="")

    sku_ids = JSONField(default=list)


class SKU(models.Model):
    sku_id = models.AutoField(primary_key=True)
    item_ids = JSONField(default=list)

    digits = models.FloatField(default=0)
    unit = models.CharField(max_length=50, default="")
    size = models.TextField(default="")


class Item(models.Model):
    item_id = models.AutoField(primary_key=True)

    name = models.TextField(default="")

    price = models.FloatField(default=0)
    market = models.TextField(default="")

    src = models.URLField(default="")
    link = models.URLField(default="")

    out_of_stock = models.BooleanField(default=False)


class ShoppingList(models.Model):
    list_id = models.AutoField(primary_key=True)

    user_id = models.PositiveIntegerField()

    sku_ids = JSONField(default=list)

    title = models.CharField(max_length=50, default="")

    time_created = models.DateTimeField(auto_now_add=True)
