MONGO_HOST = "ds147450.mlab.com"
MONGO_PORT = 47450

# Skip these if your db has no auth. But it really should.
MONGO_USERNAME = "west"
MONGO_PASSWORD = "west123"

MONGO_DBNAME = "westeros"

# Enable reads (GET), inserts (POST) and DELETE for resources/collections
# (if you omit this line, the API will default to ['GET'] and provide
# read-only access to the endpoint).
RESOURCE_METHODS = ["GET", "POST", "DELETE"]

# Enable reads (GET), edits (PATCH) and deletes of individual items
# (defaults to read-only item access).
ITEM_METHODS = ["GET", "PATCH", "DELETE"]


ALLOW_UNKNOWN = True

"""
ads = {
    # 'title' tag used in item links.
	'item_title': 'ad',

    # Schema definition, based on Cerberus grammar. Check the Cerberus project
    # (https://github.com/pyeve/cerberus) for details.
    'schema': {
        'title': {
            'type': 'string',
		},
		'link' :{
			'type': 'string',
            'required': True,
		},
		'id':{
			'type': 'integer',
			'unique': True,
		},
		'location':{
			'type': 'dict'

		}

	}

}
"""

# The DOMAIN dict explains which resources will be available and how they will
# be accessible to the API consumer.
DOMAIN = {"ads": {}}
