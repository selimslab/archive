from pymongo.errors import BulkWriteError


def bulk_exec(collection, ops):
    try:
        collection.bulk_write(ops, ordered=False)
        return []
    except BulkWriteError as bwe:
        print(bwe.details)
        return ops
