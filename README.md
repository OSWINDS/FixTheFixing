# FixTheFixing
Fix the Fixing EU project code

###Before you run:
1) Install Mongo in Windows: https://www.youtube.com/watch?v=1uFY60CESlM <br/>
2) Install MongoDB plugin in IntelliJ <br/>
3) Install Maven in Windows: http://www.mkyong.com/maven/how-to-install-maven-in-windows/ <br/>
4) Check out project from Version Control <br/>
5) Create mongod service in Windows <br/>

###Dependencies
org.mongodb:mongodb-driver:3.2.2

###Example Arguments for Twitter
querysearch="(Djokovic OR Novak) fixing" since=2016-01-18 until=2016-01-30 maxtweets=1000 theme="djokovic"

###Search query for Mongo Explorer (Ctrl+F in IntelliJ)
In filter text box: {'tweet.date':'2016/01/28'} , which typically follows a JSON format.
