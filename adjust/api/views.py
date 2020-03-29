#
from rest_framework import filters, viewsets
from rest_framework.response import Response
from django.db.models import Sum, FloatField
from django_filters.rest_framework import DjangoFilterBackend

#
from typing import Iterable

# local
from api.serializers import RecordSerializer
from api.filters import CustomFilter
from api.models import Record


class RecordViewSet(viewsets.ModelViewSet):
    serializer_class = RecordSerializer
    filter_backends = [DjangoFilterBackend, filters.OrderingFilter]
    filter_class = CustomFilter

    allowed_fields = {
        "date",
        "channel",
        "country",
        "os",
        "impressions",
        "clicks",
        "installs",
        "spend",
        "revenue",
        "cpi",
    }
    allowed_groups = {"date", "channel", "country", "os"}
    fields = allowed_fields - {"cpi"}  # default fields, without cpi
    queryset = Record.objects.all()

    def list(self, request) -> Response:
        fields_to_group_by = self.request.query_params.get("group_by", None)
        self.group(fields_to_group_by)

        fields_to_sum = self.request.query_params.get("sum", None)
        self.aggregate(fields_to_sum)

        # filter
        self.queryset = self.filter_queryset(self.queryset)

        # init the serializer with dynamic fields
        serializer = RecordSerializer(self.queryset, many=True, fields=self.fields)

        return Response(serializer.data)

    def group(self, fields_to_group_by: str):
        if fields_to_group_by:
            groups = fields_to_group_by.split(",")
            # sanitize input
            groups = self.allowed_groups.intersection(set(groups))
            # only grouped fields remain
            self.fields = self.fields.intersection(groups)
            # create queryset
            self.queryset = Record.objects.values(*groups)

    def aggregate(self, fields_to_sum: str):
        if fields_to_sum:
            fields_to_sum = fields_to_sum.split(",")
            # sanitize input
            fields_to_sum = self.allowed_fields.intersection(set(fields_to_sum))
            # add aggregated fields
            self.fields = self.fields.union(fields_to_sum)
            annotations = self.get_annotations_dict(fields_to_sum)
            self.queryset = self.queryset.annotate(**annotations)

    def get_annotations_dict(self, annotation_fields: set) -> dict:
        annotations = dict()
        for field in annotation_fields:
            if field == "cpi":
                cpi_formula = Sum("spend", output_field=FloatField()) / Sum(
                    "installs", output_field=FloatField()
                )
                annotations[field] = cpi_formula
                # since cpi is an optional field, only add when necessary
                self.fields.add("cpi")
            else:
                annotations[field] = Sum(field)

        return annotations
