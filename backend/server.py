from flask import Flask, abort, request
import json

from lib import *

app = Flask(__name__)


@app.route('/receive', methods=['POST'])
def post():
    if not request.json:
        abort(400)
    print(request.json)
    return json.dumps(request.json)


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
