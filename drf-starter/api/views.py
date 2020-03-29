from django.shortcuts import render

from rest_framework import viewsets

from api.permissions import IsOwnerOrReadOnly, IsAdminOrReadOnly
from rest_framework import permissions

from api.models import ShoppingList, Item, SKU, Product
from api.serializers import (
    ShoppingListSerializer,
    ItemSerializer,
    SKUSerializer,
    ProductSerializer,
)

from rest_framework import status, viewsets
from rest_framework.response import Response


class ShoppingListViewSet(viewsets.ModelViewSet):
    """
    Shopping Lists
    """

    queryset = ShoppingList.objects.all()
    serializer_class = ShoppingListSerializer
    # permission_classes = (permissions.IsAuthenticatedOrReadOnly, IsOwnerOrReadOnly,)


class ItemViewSet(viewsets.ModelViewSet):
    """
    Send a list for multiple item creation
    """

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(
            data=request.data, many=isinstance(request.data, list)
        )
        serializer.is_valid(raise_exception=True)
        self.perform_create(serializer)
        headers = self.get_success_headers(serializer.data)
        return Response(
            serializer.data, status=status.HTTP_201_CREATED, headers=headers
        )

    queryset = Item.objects.all()
    serializer_class = ItemSerializer
    # permission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly)


class SKUViewSet(viewsets.ModelViewSet):
    queryset = SKU.objects.all()
    serializer_class = SKUSerializer


class ProductViewSet(viewsets.ModelViewSet):
    queryset = Product.objects.all()
    serializer_class = ProductSerializer
