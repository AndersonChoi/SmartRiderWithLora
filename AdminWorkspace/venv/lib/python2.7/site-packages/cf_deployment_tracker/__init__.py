import time
import json
from re import search
from requests import post
from os import environ as env


def track(tracker_url=None):

    # Get version and repository URL from 'setup.py'
    version = None
    repo_url = None
    try:
        with open('setup.py') as setup:
            setup_file = setup.read()
            try:
                version_regex = r'version=[\'\"][\d.]+[\'\"]'
                version = search(version_regex, setup_file).group(0)[9:-1]
            except AttributeError:
                print ("No version available")
            try:
                url_regex = r'url=[\'\"]https?:\/\/(www\.)?' \
                            r'[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}' \
                            r'\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)[\'\"]'
                repo_url = search(url_regex, setup_file).group(0)[5:-1]
            except AttributeError:
                print ("No version available")
    except IOError as e:
        print ("No setup.py file exists for the current app")

    if env.get('VCAP_APPLICATION') is not None:
        vcap_app = json.loads(env['VCAP_APPLICATION'])
        event = dict()
        event['date_sent'] = time.strftime("%Y-%m-%dT%H:%M:%S.000Z", time.gmtime())
        if version is not None:
            event['code_version'] = version
        if repo_url is not None:
            event['repository_url'] = repo_url
        event['runtime'] = 'python'
        event['application_name'] = str(vcap_app['name'])
        event['space_id'] = str(vcap_app['space_id'])
        event['application_version'] = str(vcap_app['application_version'])
        event['application_uris'] = [str(uri) for uri in vcap_app['application_uris']]

        # Check for VCAP_SERVICES env var with at least one service
        # Refer to http://docs.cloudfoundry.org/devguide/deploy-apps/environment-variable.html#VCAP-SERVICES
        if env.get('VCAP_SERVICES') is not None and json.loads(env['VCAP_SERVICES']):
            vcap_services = json.loads(env['VCAP_SERVICES'])
            event['bound_vcap_services'] = dict()

            # For each bound service, count the number of instances and identify used plans
            for service in vcap_services:
                event['bound_vcap_services'][service] = {
                    'count': len(vcap_services[service]),
                    'plans': []
                }

                # Append plans for each instance
                for instance in vcap_services[service]:
                    if 'plan' in instance.keys():
                        event['bound_vcap_services'][service]['plans'].append(str(instance['plan']))

                if len(event['bound_vcap_services'][service]['plans']) == 0:
                    del event['bound_vcap_services'][service]['plans']

        # Create and format request to Deployment Tracker
        url = 'https://deployment-tracker.mybluemix.net/api/v1/track' if tracker_url is None else tracker_url
        headers = {'content-type': "application/json"}
        try:
            response = post(url, data=json.dumps(event), headers=headers)
            print ('Uploaded stats: %s' % response.text)
        except Exception as e:
            print ('Deployment Tracker upload error: %s' % str(e))
