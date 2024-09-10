#!/bin/bash
kubectl delete -f k8s/config/mongo-configmap.yaml
kubectl delete -f k8s/config/mongo-deployment.yaml
kubectl delete -f k8s/config/mongo-secret.yaml
kubectl delete -f k8s/config/delivery-deployment.yaml
kubectl delete -f k8s/config/mongo-express-deployment.yaml
kubectl delete -f k8s/config/ingress.yaml

minikube addons disable ingress
