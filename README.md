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
foo@bar:~$ siege -c100 -r10 -t60S -b http://localhost:8086/kube/pod
 
# http://localhost:8086/kube/podWithoutCache // user 100, REPS 10, Time 60s, delay 0 
foo@bar:~$ siege -c10 -r10 -t20 http://localhost:8086/kube/podWithoutCache


```