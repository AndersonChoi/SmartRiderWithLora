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
    response2 = urllib2.urlopen("http://testweb.mybluemix.net/api/end_devices")
    data = simplejson.load(response)
    data2 = simplejson.load(response2)
    return render_template('index.html',userData=data, deviceData=data2 )


@app.route('/log/<userdevice>')
def logpage(userdevice):

    deviceURL = "http://testweb.mybluemix.net/api/end_devices/"
    deviceURLappend="/list"
    eachDeviceURL =deviceURL+userdevice+deviceURLappend

    response = urllib2.urlopen("http://testweb.mybluemix.net/api/members")
    response2 = urllib2.urlopen(eachDeviceURL)
    data = simplejson.load(response)
    data2 = simplejson.load(response2)
    return render_template('log.html',userDeviceNumber = userdevice,userData=data, deviceDataList=data2 )

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=port)
