from django_filters.rest_framework import FilterSet
from django_filters import DateFromToRangeFilter
from api.models import Record
from django_filters.rest_framework import DjangoFilterBackend
from rest_framework import filters


class CustomFilter(FilterSet):
    date = DateFromToRangeFilter()

    class Meta:
        model = Record
        fields = ("channel", "country", "os", "date")
