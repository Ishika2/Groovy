from flask import Flask
from model import songModel

app = Flask(__name__)


@app.route('/')
def hello_world():
    return 'Hello, World!'


@app.route('/song/<string:song_name>')
def song(song_name):
    songModel(song_name)
    return "all okay"

if __name__ == '__main__':
    app.run()