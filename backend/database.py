from pymongo import MongoClient, GEO2D
from bson.objectid import ObjectId
from pprint import pprint

from lib import MONGO_CONNECTION_STRING

CLIENT = MongoClient(MONGO_CONNECTION_STRING)
print(MONGO_CONNECTION_STRING)
DB = CLIENT.vgdb
DATA = DB.data

#	REFERENCE FOR ALL MONGODB FUNCTIONS:
#	http://api.mongodb.com/python/current/examples/geo.html


#	Given a location as a coordinate tuple, and a distance, returns every nearby object
def get(location, distance=10):
	query = {"loc": {"$within": {"$center": [list(location), distance]}}}
	ret = []
	for doc in DATA.find(query).sort("_id"):
		ret.append(doc)
	return ret


#	Given a location as a coordinate tuple, and a data package, inserts into the database
def put(location, data):
	return DATA.insert_one(data).inserted_id


#	Deletes a document from database based on the given ID
def delete(doc_id):
	return DATA.remove({"_id": ObjectId(doc_id)})