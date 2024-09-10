#!/bin/bash

if ! command -v minikube &> /dev/null
then
    echo "Minikube is not installed. Please install Minikube first."
    exit 1
fi

if minikube status | grep -q "Running"; then
    echo "Minikube is already running."
else
    MINIKUBE_DRIVER=${1:-docker}

    echo "Starting Minikube with driver: $MINIKUBE_DRIVER..."
    minikube start --driver="$MINIKUBE_DRIVER"

    if [ $? -eq 0 ]; then
        echo "Minikube started successfully."
    else
        echo "Failed to start Minikube. Please check logs for more information."
        exit 1
    fi
fi

./gradlew build -x deltaCoverage

eval "$(minikube docker-env)"

minikube addons enable ingress

docker build -t delivery:latest .

kubectl apply -f k8s/config/mongo-secret.yaml
kubectl apply -f k8s/config/mongo-configmap.yaml
kubectl apply -f k8s/config/delivery-configmap.yaml
kubectl apply -f k8s/config/mongo-deployment.yaml

kubectl wait --for=condition=ready pod -l app=mongodb --timeout=300s

kubectl apply -f k8s/config/mongo-express-deployment.yaml
kubectl apply -f k8s/config/delivery-deployment.yaml
kubectl apply -f k8s/config/ingress.yaml

kubectl wait --for=condition=ready pod --all --timeout=300s

minikube service mongo-express-service --url
