## Quickstart
Requires python 3.7

`pipenv install`

`pipenv shell`

`python3 api.py`

#### import 

in a separate terminal tab

`python3 import.py`

#### endpoints
tasks 
http://localhost:5000/tasks

logs 
http://localhost:5000/logs

## Comments
Flask and sqlite is chosen for the simplicity and the scope of the work 

Duration field is modeled as a string. It is possible to parse it, turn it into seconds, and de-serialize it to the same format. However it would add unnecessary complexity.

Flask-restful is chosen because it makes it simple to grow the API and keeps the code clean 

Models are dataclasses to jsonify them easily 