from flask import Flask, abort, request
import json

from database import put
from database import get
from database import delete

app = Flask(__name__)


@app.route('/receive', methods=['PUT'])
def handle_put():
    req_json = request.get_json()
    put(req_json)

@app.route('/retrieve', methods=['GET'])
def handle_get():
    req_json = request.get_json()
    location = ( req_json['latitude'] , req_json['longitude'] )
    get(location)

@app.route('/delete', methods=['DELETE'])
def handle_delete():
    req_json = request.get_json()
    delete(req_json['id'])


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
