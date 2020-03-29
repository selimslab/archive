from rest_framework.serializers import (
    ModelSerializer,
    FloatField,
    SerializerMethodField,
)
from api.models import Record


class DynamicFieldsSerializer(ModelSerializer):
    """ Enables dynamic serializer fields """

    def __init__(self, *args, **kwargs):
        fields = kwargs.pop("fields", set())
        super().__init__(*args, **kwargs)
        if fields and "__all__" not in fields:
            all_fields = set(self.fields.keys())
            for not_requested in all_fields - set(fields):
                # Drop any fields that are not specified in the `fields` argument.
                self.fields.pop(not_requested)


class RecordSerializer(DynamicFieldsSerializer):
    cpi = FloatField(allow_null=True)

    class Meta:
        model = Record
        fields = (
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
        )
