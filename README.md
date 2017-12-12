## App for displaying temperature info from a server
The app has 3 modes for retrieving data:
 * http request
 * socket
 * serial (USB)
 
The app has a default server address, which contains secondly refreshed weather data of Berlin, DE. Also, if there is no connection, the app has a "demo" button to test.

## Server
#### Http request
The response from the server (of type json-object) needs to contain the following attributes:
* temperature (float)
* humidity (float)
* last_updated (unix timestamp)

#### Socket
Coming soon.

#### Serial
Coming soon.
