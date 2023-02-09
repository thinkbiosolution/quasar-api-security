from flask import Flask, request, redirect
from flask_oauthlib.client import OAuth
from twilio.rest import Client

app = Flask(__name__)
oauth = OAuth(app)

# setup OAuth client for your OAuth provider (e.g. Google)
google = oauth.remote_app(
    'google',
    consumer_key='<your-consumer-key>',
    consumer_secret='<your-consumer-secret>',
    request_token_params={
        'scope': 'email'
    },
    base_url='https://www.googleapis.com/oauth2/v1/',
    request_token_url=None,
    access_token_method='POST',
    access_token_url='https://accounts.google.com/o/oauth2/token',
    authorize_url='https://accounts.google.com/o/oauth2/auth',
)

# setup Twilio client
twilio_client = Client()

# OAuth login endpoint
@app.route('/login')
def login():
    return google.authorize(callback=url_for('authorized', _external=True))

# OAuth authorized endpoint
@app.route('/authorized')
def authorized():
    resp = google.authorized_response()
    if resp is None:
        return 'Access denied: reason=%s error=%s' % (
            request.args['error_reason'],
            request.args['error_description']
        )
    session['google_token'] = (resp['access_token'], '')
    me = google.get('userinfo')
    return 'Logged in as id=%s name=%s email=%s redirect=%s' % \
        (me.data['id'], me.data['name'], me.data['email'], request.args.get('next'))

# example endpoint that sends a text message using Twilio
@app.route('/send_text', methods=['POST'])
def send_text():
    # check if user is logged in
    if 'google_token' not in session:
        return redirect(url_for('login'))
    
    # send text message using Twilio
    message = twilio_client.messages.create(
        to="<to-phone-number>", 
        from_="<from-phone-number>",
        body=request.form['message']
    )
    return 'Text message sent!'

# OAuth logout endpoint
@app.route('/logout')
def logout():
    session.pop('google_token', None)
    return redirect(url_for('index'))

if __name__ == '__main__':
    app.debug = True
    app.run()
