# OGEL Backend

Interview assignment. Data from csv is populated untouched to in memory database.
Based on that reports are generated without creating new tables or keeping a cache.
In normal environment I would probably was more keen to keep data in database more
closely to actual presentation as a separate table or view. Other improvement I would
consider from the start would be a cache of some sort. I did not implement tests or
exception handling as the solution itself was meant to be an approach to a problem
not finished product so some simplifications were assumed.

From the structure perspective I divided process of generating reports into three
stages. Firstly ProductionService gets all related events from the database,
then ReportBuilder populates events into ReportGenerators and then reports are creted
for every machine. 


## Compile and run

### Compile
From project root directory
```
./mvnw clean install
```
### Run
From project root directory
```
./mvnw exec:java
```
## Demo
App is deployed to heroku under the url:  
<https://ogel-backend.herokuapp.com>

### Available reports:
<https://ogel-backend.herokuapp.com/reports/oee>  
<https://ogel-backend.herokuapp.com/reports/production>  
<https://ogel-backend.herokuapp.com/reports/temperature>  
