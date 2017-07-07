Spelling Checker RESTful API
===================

A Java project RESTful service to check spelling mistakes.


Download
-------------
Clone

Usage
-------------
- Clone and deploy on Apache Tomcat Server.
- Send post request to this server with JSON body
```json
{
	"text":"[Text need to check]"
}
```
- Remember to add **Authorization** field in the request header with **username:password** (must be encoded by **Base64**) that matches with server

Note
-------------
Path to below directories */src/main/webapp/conf/*<br>
- **auth_string.txt** file contains authentication string
- **data_tempate.txt** file contains JSON template to send to elastic search server

Dependency 
-------------
- A server that deployed this project [newai-elasticsearch](https://github.com/thieunguyenhung/newai-elasticsearch)

License 
-------------
- [Apache license 2.0](https://www.apache.org/licenses/LICENSE-2.0)
- [elastic Open Source license](https://www.elastic.co/subscriptions)


### © 2017 NewAI Team