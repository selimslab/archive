from rest_framework import serializers
from .models import ShoppingList, Item, SKU, Product
from rest_framework.serializers import ModelSerializer
from api.mixins import CreateListModelMixin
from drf_queryfields import QueryFieldsMixin


class ItemSerializer(QueryFieldsMixin, ModelSerializer):
    class Meta:
        model = Item
        fields = "__all__"


class SKUSerializer(QueryFieldsMixin, ModelSerializer):
    class Meta:
        model = SKU
        fields = "__all__"


class ProductSerializer(QueryFieldsMixin, ModelSerializer):
    class Meta:
        model = Product
        fields = "__all__"


class ShoppingListSerializer(ModelSerializer):
    class Meta:
        model = ShoppingList
        fields = "__all__"
