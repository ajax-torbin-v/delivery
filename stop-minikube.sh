#!/bin/bash
kubectl delete -f k8s/config/mongo-secret.yaml
kubectl delete -f k8s/config/domain-configmap.yaml
kubectl delete -f k8s/config/nats-deployment.yaml
kubectl delete -f k8s/config/mongo-deployment.yaml
kubectl delete -f k8s/config/mongo-express-deployment.yaml
kubectl delete -f k8s/config/domain-deployment.yaml
kubectl delete -f k8s/config/gateway-deployment.yaml
kubectl delete -f k8s/config/ingress.yaml
kubectl delete -f k8s/config/kafka.yaml
