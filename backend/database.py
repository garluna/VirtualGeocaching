from pymongo import MongoClient, GEO2D
from bson.objectid import ObjectId
from pprint import pprint
import json

from lib import MONGO_CONNECTION_STRING

CLIENT = MongoClient(MONGO_CONNECTION_STRING)
DB = CLIENT.vgdb
DATA = DB.data

#	REFERENCE FOR ALL MONGODB FUNCTIONS:
#	http://api.mongodb.com/python/current/examples/geo.html


#	Given a location as a coordinate tuple, and a distance, returns every nearby object that the user should see
def get(data):
	data = json.loads(data)
	query = {"loc": {"$within": {"$center": [list(data["location"]), data["distance"]]}}}
	ret = []

	for doc in DATA.find(query).sort("_id"):
		if (doc["private"] == True and doc["user"] == data["user"]) or doc["private"] == False:
			ret.append(doc)

	as_dict = {}
	for doc in ret:
		as_dict[doc["_id"]] = doc
	return json.dumps(as_dict)


#	Inserts a document into the database
def put(data):
	data = json.loads(data)
	ret = {}
	ret["id"] = DATA.insert_one(data).inserted_id
	return json.dumps(ret)


#	Deletes a document from database based on the given ID
def delete(data):
	data = json.loads(data)
	return json.dumps(DATA.remove({"_id": ObjectId(data["id"])}))