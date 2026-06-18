#!/bin/bash
echo "Verificando contenedores..."
docker ps | grep insurance_db && echo "DB ok" || echo "DB no corre"
docker ps | grep insurance_backend && echo "Backend ok" || echo "Backend no corre"
docker ps | grep insurance_frontend && echo "Frontend ok" || echo "Frontend no corre"
echo ""
echo "Probando endpoints..."
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/aseguradoras
echo " - GET /api/aseguradoras"
curl -s -o /dev/null -w "%{http_code}" http://localhost
echo " - GET / (frontend)"