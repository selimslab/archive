from django.urls import path
from rest_framework.routers import DefaultRouter
from django.conf.urls import include
from api.views import RecordViewSet

router = DefaultRouter()
router.register(r"records", RecordViewSet, basename="Record")

urlpatterns = [
    path("", include(router.urls)),
]
