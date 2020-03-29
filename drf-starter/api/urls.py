from django.urls import path

from rest_framework.routers import DefaultRouter
from django.conf.urls import include
from api.views import ShoppingListViewSet, ItemViewSet, SKUViewSet, ProductViewSet
from users.views import UserViewSet


router = DefaultRouter()
router.register(r"lists", ShoppingListViewSet)
router.register(r"users", UserViewSet)
router.register(r"items", ItemViewSet)
router.register(r"skus", SKUViewSet)
router.register(r"products", ProductViewSet)


urlpatterns = [
    path("", include(router.urls)),
]
