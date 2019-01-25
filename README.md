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

```