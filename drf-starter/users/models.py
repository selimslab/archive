from django.contrib.auth.models import AbstractUser
from django.db import models
from django.contrib.postgres.fields import JSONField


class CustomUser(AbstractUser):
    user_id = models.AutoField(primary_key=True)
    joined = models.DateTimeField(auto_now_add=True)

    user_data = JSONField(default=dict)
