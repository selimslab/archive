a distributed microservices based web scraper with a REST api

## design
+ scrapy crawls the items 
+ records items to mongodb servers
+ api provides endpoints to access the items 
+ demo shows an example usage of api

## development
+ to set the virtual environment 
`python3 -m venv venv
source venv/bin/activate`

+ install dependencies for local development
`pip install -r requirements.txt`

+ for linux simply type `bash setup` 

+ local server `bash server`

## COMPONENTS

## API 
+ api url is https://westapi.herokuapp.com/
+ eve framework is used for REST api
+ docs: http://python-eve.org/quickstart.html and http://python-eve.org/features.html

+ scrapinghub python client is used to communicate with spiders in cloud
+ docs: https://python-scrapinghub.readthedocs.io/en/latest/quickstart.html

+ deployed on heroku
+ docs: https://devcenter.heroku.com/articles/getting-started-with-python#introduction
WARNING: heroku is not a production server. 
It sleeps after 30 min. inactivity and takes a while to awake again. 
Deploy to AWS EC2 or upgrade Heroku plan.  

to deploy

`cd api`

`heroku login`

`git add .`

`git commit -m 'commit message'`

`git push heroku master`


### API endpoints

#### in terminal

+ get all properties `curl -i https://westapi.herokuapp.com/ads`

+ get single property with id `curl -i https://westapi.herokuapp.com/ads?where=id==1`

+ crawl all properties `curl -i https://westapi.herokuapp.com/crawl?urls='PAGE_URL'&pagination=True&coordinates=True'`

+ crawl last added properties `curl -i https://westapi.herokuapp.com/crawl?urls='PAGE_URL&pagination=False'`

+ get properties in a radius(km) `curl -i https://westapi.herokuapp.com/close?lat=39.9649568&lon=32.511421&radius=6`


#### in python 
``` python
import requests

# get all properties  
response = requests.get('https://westapi.herokuapp.com/ads').json()

# crawl all properties
response = requests.get('https://westapi.herokuapp.com/crawl?urls='PAGE_URL'&pagination=True')
```

and so on 


## SCRAPER  
+ built using scrapy framework
https://docs.scrapy.org/en/latest/intro/overview.html

+ spiders in scraper/app/spiders 
+ scraper/app/pipelines.py gets scraped items and records them to the db

+ deployed on scrapy cloud
+ docs: https://doc.scrapinghub.com/scrapy-cloud.html

+ shub is the cli tool for cloud
+ https://shub.readthedocs.io/en/stable/quickstart.html

+ python client interface is 
+ https://python-scrapinghub.readthedocs.io/en/latest/


+ to deploy

`cd scraper`

`shub deploy`

NOTE: 
Scrapy Cloud seems like a good option 
but crawlers can be deployed to another server although procedure is longer. 
https://docs.scrapy.org/en/latest/deploy

### IP ROTATION  
+ python scrapy-proxies package is used. 
+ Moreover, spiders wait 2 second for every request to be kind and avoid IP bans.  

however free proxies are transparent, http server can see the real IP sending requests.
In short, you will still get banned. 

+ If you need to make a lot of requests, Crawlera proxy is a good option. 
+ $25/month for 150k requests. $100/month for 1M requests. 
+ https://scrapinghub.com/crawlera


+ https://getproxylist.com/
+ http://pubproxy.com/


## DB
+ mongodb is on https://mlab.com





