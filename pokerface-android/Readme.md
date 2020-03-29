
A computer vision Android app with a REST API and image processing/deep learning backend

+ [back end server source code](https://github.com/selimslab/PokerFace-Server)


### Mobile device
+ Smart phone detects faces on device using Firebase MLKit
+ Device sends detected face to a backend server 
+ built with Kotlin, Retrofit, Rx, and Picasso 
+ Minimum SDK 19, Android 4.4 Kitkat 

### The server
+ a dockerized deep learning model and C++ source for HOG transform.
+ Served over a Python Flask backend on alpine linux with gunicorn web server

1. preprocesses 
2. generates 128 measurements by feeding the test image to deep neural network
3. compares the image to the database
4. Returns the result, a name or no match 

### Preprocessing 
1. image to grayscale

2. detect the faces via Histogram of Oriented Gradients(HOG) transform 
  + turning  pixels to gradients
  + and max pooling by mapping gradients in 4x4 boxes to 1 gradient

3. Deal with posing through 
  + rotate scale shear, basic image transformations to warp the picture so that the eyes and lips are always in the              sample place in the image.
  + detect 68 landmarks on face

### Deep Learning
+ A deep CNN is trained on a Tesla K80 for 24 hrs, with triplets of landmarks from over 500k images 


### Backend API 

https://poker-face-api.herokuapp.com

- python backend built on Flask framework, Docker and face_recognition library

#### endpoints 

<b>/meet</b>

- accepts a photo and name, returns the status as json response

<b>/predict</b>

- accepts a photo and returns a name guess and status

#### build and deploy
 
- the server app runs in a Docker container, 

- runtime is python 3.6 and gunicorn WSGI server

* [container docs](https://devcenter.heroku.com/articles/container-registry-and-runtime)


## References

[the great article](https://medium.com/@ageitgey/machine-learning-is-fun-part-4-modern-face-recognition-with-deep-learning-c3cffc121d78)

[core library](https://github.com/ageitgey/face_recognition

<a href="https://play.google.com/store/apps/details?id=com.commencisstaj18.ozturkse.visionary"><img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" width="250"></a>
