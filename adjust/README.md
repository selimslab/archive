## adjust
a Django REST API with filter, group, sort, aggregate, and explore mobile user events 


### quick start 

```bash
git clone https://github.com/selimslab/adjust.git
cd adjust

pipenv shell
pipenv install

python manage.py makemigrations api
python manage.py migrate

python manage.py runserver

python post_csv_to_api.py
```

## docs
swagger docs at http://localhost:8000/docs


### fields
"date",
"channel",
"country",
"os",
"impressions",
"clicks",
"installs",
"spend",
"revenue",
"cpi"

## API Usage

### filtering 

filter by time range, channels, countries, operating systems

http://localhost:8000/api/v1/records?os=ios

http://localhost:8000/api/v1/records/?os=ios&date_after=2017-05-01&date_before=2017-05-31

### grouping

parameter: group_by
 
"date", "channel", "country", or "os"

http://localhost:8000/api/v1/records?group_by=os

separate multiple arguments by ","

http://localhost:8000/api/v1/records?group_by=os,country

### sum 

aggregate records

http://localhost:8000/api/v1/records?group_by=date&sum=installs,clicks

### sort 
order by any field 

parameter: ordering

http://localhost:8000/api/v1/records?ordering=date

> use - for descending

http://localhost:8000/api/v1/records?ordering=-date

        
## Use cases
> Show the number of impressions and clicks that occurred before the 1st of June 2017, broken down by channel and country, sorted by clicks in descending order.

```
http://localhost:8000/api/v1/records/?date_before=2017-06-01&group_by=channel,country&sum=impressions,clicks&ordering=-clicks
```


> Show the number of installs that occurred in May of 2017 on iOS, broken down by date, sorted by date in ascending order.


```
http://localhost:8000/api/v1/records/?os=ios&date_after=2017-05-01&date_before=2017-05-31&group_by=date&sum=installs&ordering=date
```


> Show revenue, earned on June 1, 2017 in US, broken down by operating system and sorted by revenue in descending order

```
http://localhost:8000/api/v1/records?country=US&date=2017-06-01&group_by=os&sum=revenue&ordering=-revenue
```


> Show CPI and spend for Canada (CA) broken down by channel ordered by CPI in descending order. Please think carefully which is an appropriate aggregate function for CPI.

```
http://localhost:8000/api/v1/records/?country=CA&group_by=channel&sum=cpi&ordering=-cpi
```


