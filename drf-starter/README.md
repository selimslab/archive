deployed at [shoppers-rest-api.herokuapp.com](shoppers-rest-api.herokuapp.com)

[view API docs](shoppers-rest-api.herokuapp.com/docs)

Procfile for heroku

### Steps from scratch

```
django-admin startproject baseline
cd baseline
django-admin startapp api
django-admin startapp users

pipenv shell
pipenv install django djangorestframework x y z ... for all the packages in Pipfile
add packages to baseline/settings.py INSTALLED_APPS
add the lines under #CUSTOM SETTINGS to settings.py

set up a postgres DB

# after every model change
python manage.py makemigrations api users
python manage.py migrate

add models, serializers, views, urls

git commit and push 

python manage.py runserver
```

### Reminders
+ make DEBUG = False before deployment
+ 




