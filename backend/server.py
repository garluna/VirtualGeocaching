from flask import Flask, abort, request
import json

from database import put
from database import get
from database import delete

app = Flask(__name__)


@app.route('/receive', methods=['PUT'])
def handle_put():
    put(json.dumps(request.json).doc)

@app.route('/retrieve', methods=['GET'])
def handle_get():
    get(json.dumps(request.json).location, json.dumps(request.json).distance)

@app.route('/delete', methods=['DELETE'])
def handle_delete():
    delete(json.dumps(request.json).id)


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
