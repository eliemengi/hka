```shell
    cd C:\Zimmermann\kubernetes

    kubectl apply -f network-policy-default.yaml
    kubectl apply -f high-priority.yaml
    kubectl apply -f medium-priority.yaml
    kubectl apply -f low-priority.yaml

    kubectl apply -f namespace.yaml
    kubectl label --overwrite ns acme `
        pod-security.kubernetes.io/audit=baseline pod-security.kubernetes.io/audit-version=latest `
        pod-security.kubernetes.io/warn=baseline pod-security.kubernetes.io/warn-version=latest

    kubectl apply -f limit-range.yaml
    kubectl apply -f network-policy.yaml
    kubectl get networkpolicy --namespace acme
    kubectl apply -f resource-quota.yaml
    kubectl replace -f service-account.yaml
    kubectl describe namespace acme

    kubectl create namespace traefik
    kubectl create namespace kgateway-system
```
