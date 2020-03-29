from django.db import models


class Record(models.Model):
    date = models.DateField()
    channel = models.TextField()
    country = models.CharField(max_length=3)
    os = models.CharField(max_length=10)
    impressions = models.PositiveIntegerField()
    clicks = models.PositiveIntegerField()
    installs = models.PositiveIntegerField()
    spend = models.FloatField()
    revenue = models.FloatField()
