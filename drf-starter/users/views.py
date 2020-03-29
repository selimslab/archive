from django.shortcuts import render

# Create your views here.
# Create your views here.
from allauth.socialaccount.providers.facebook.views import FacebookOAuth2Adapter
from rest_auth.registration.views import SocialLoginView


from django.contrib.auth import get_user_model
from rest_framework import viewsets

from users.serializers import UserSerializer
from rest_framework.parsers import FormParser, MultiPartParser
from rest_framework.mixins import UpdateModelMixin


class FacebookLogin(SocialLoginView):
    adapter_class = FacebookOAuth2Adapter


class UserViewSet(viewsets.ReadOnlyModelViewSet, UpdateModelMixin):
    """
    POST request to users/me/ returns the user data
    don't forget to add user token to Authorization header
    """

    queryset = get_user_model().objects.all()
    serializer_class = UserSerializer
    parser_classes = (
        MultiPartParser,
        FormParser,
    )

    def perform_create(self, serializer):
        serializer.save(owner=self.request.user,)

    def put(self, request, *args, **kwargs):
        return self.partial_update(request, *args, **kwargs)

    def get_object(self):
        pk = self.kwargs.get("pk")

        if pk == "me":
            return self.request.user

        return super(UserViewSet, self).get_object()
