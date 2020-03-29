from flask import Flask, render_template, request
import requests, json

app = Flask(__name__)

api_url = "https://westapi.herokuapp.com/"


@app.route("/")
def home():
    return render_template("map.html")


@app.route("/crawl")
def crawl():
    if request.form:
        pass
    else:
        return render_template("crawl.html")
