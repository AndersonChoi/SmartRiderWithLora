"""Cloud Foundry test"""
from flask import Flask, render_template
import cf_deployment_tracker
import os
import urllib2
import simplejson


# Emit Bluemix deployment event
cf_deployment_tracker.track()

app = Flask(__name__)
# On Bluemix, get the port number from the environment variable VCAP_APP_PORT
# When running this app on the local machine, default the port to 8080
port = int(os.getenv('VCAP_APP_PORT', 8080))

@app.route('/')
def home():
    response = urllib2.urlopen("http://testweb.mybluemix.net/api/members")
    data = simplejson.load(response)
    return render_template('index.html',data=data)


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=port)
