# msa-sample-kube-cycle

```
cd service-api
mvn clean package -B
docker build -t sanaloveyou/serviceapi .
docker push sanaloveyou/serviceapi
cd ..

cd service-snapshot
mvn clean package -B
docker build -t sanaloveyou/servicesnap .
docker push sanaloveyou/servicesnap
cd ..


cd kube-monitor
mvn clean package -B
docker build -t sanaloveyou/servicemonitor .
docker push sanaloveyou/servicemonitor
cd ..


cd service-UI
npm run build
docker build -t sanaloveyou/serviceui .
docker push sanaloveyou/serviceui
cd ..

```


ingress-nginx 설치  

kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/mandatory.yaml  
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/provider/baremetal/service-nodeport.yaml  

LoadBalancer로 서비스 타입 변경


### @ Siege
```
  -V, --version             VERSION, prints the version number.
  -h, --help                HELP, prints this section.
  -C, --config              CONFIGURATION, show the current config.
  -v, --verbose             VERBOSE, prints notification to screen.
  -q, --quiet               QUIET turns verbose off and suppresses output.
  -g, --get                 GET, pull down HTTP headers and display the
                            transaction. Great for application debugging.
  -p, --print               PRINT, like GET only it prints the entire page.
  -c, --concurrent=NUM      CONCURRENT users, default is 10
  -r, --reps=NUM            REPS, number of times to run the test.
  -t, --time=NUMm           TIMED testing where "m" is modifier S, M, or H
                            ex: --time=1H, one hour test.
  -d, --delay=NUM           Time DELAY, random delay before each requst
  -b, --benchmark           BENCHMARK: no delays between requests.
  -i, --internet            INTERNET user simulation, hits URLs randomly.
  -f, --file=FILE           FILE, select a specific URLS FILE.
  -R, --rc=FILE             RC, specify an siegerc file
  -l, --log[=FILE]          LOG to FILE. If FILE is not specified, the
                            default is used: PREFIX/var/siege.log
  -m, --mark="text"         MARK, mark the log file with a string.
                            between .001 and NUM. (NOT COUNTED IN STATS)
  -H, --header="text"       Add a header to request (can be many)
  -A, --user-agent="text"   Sets User-Agent in request
  -T, --content-type="text" Sets Content-Type in request
      --no-parser           NO PARSER, turn off the HTML page parser
      --no-follow           NO FOLLOW, do not follow HTTP redirects

```
---
### @Siege Example
```bash
# http://localhost:8086/kube/pod // user 100, REPS 10, Time 60s, delay 0 
foo@bar:~$ siege --concurrent=100 --reps=10 --time=60S -b http://localhost:8086/kube/pod
 
@ Try 1
# Lifting the server siege...
  Transactions:		       32676 hits
  Availability:		      100.00 %
  Elapsed time:		       59.92 secs
  Data transferred:	      437.39 MB
  Response time:		        0.17 secs
  Transaction rate:	      545.33 trans/sec
  Throughput:		        7.30 MB/sec
  Concurrency:		       92.55
  Successful transactions:       32676
  Failed transactions:	           0
  Longest transaction:	       20.63
  Shortest transaction:	        0.00 
 
@ Try 2 
# Lifting the server siege...
  Transactions:		       32729 hits
  Availability:		       99.70 %
  Elapsed time:		       59.92 secs
  Data transferred:	      438.10 MB
  Response time:		        0.06 secs
  Transaction rate:	      546.21 trans/sec
  Throughput:		        7.31 MB/sec
  Concurrency:		       34.86
  Successful transactions:       32729
  Failed transactions:	         100
  Longest transaction:	        0.79
  Shortest transaction:	        0.01  
 
# http://localhost:8086/kube/podWithoutCache // user 100, REPS 10, Time 60s, delay 0 
foo@bar:~$ siege --concurrent=100 --reps=10 --time=60S http://localhost:8086/kube/podWithoutCache

@ Try 1
# Lifting the server siege... 
  Transactions:		       10388 hits
  Availability:		      100.00 %
  Elapsed time:		       59.89 secs
  Data transferred:	      139.05 MB
  Response time:		        0.57 secs
  Transaction rate:	      173.45 trans/sec
  Throughput:		        2.32 MB/sec
  Concurrency:		       98.28
  Successful transactions:       10388
  Failed transactions:	           0
  Longest transaction:	        1.81
  Shortest transaction:	        0.04
  
@ Try 2  
# Lifting the server siege...
  Transactions:		       10575 hits
  Availability:		      100.00 %
  Elapsed time:		       59.35 secs
  Data transferred:	      144.38 MB
  Response time:		        0.55 secs
  Transaction rate:	      178.18 trans/sec
  Throughput:		        2.43 MB/sec
  Concurrency:		       97.18
  Successful transactions:       10575
  Failed transactions:	           0
  Longest transaction:	        1.91
  Shortest transaction:	        0.03

```