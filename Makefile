.PHONY: help build test image-build up restart status logs smoke validate-stack newman-scan zap-scan down clean

help:
	$(MAKE) -C digibank-microservices help
build:
	$(MAKE) -C digibank-microservices build
test:
	$(MAKE) -C digibank-microservices test
image-build:
	$(MAKE) -C digibank-microservices image-build
up:
	$(MAKE) -C digibank-microservices up
restart:
	$(MAKE) -C digibank-microservices restart
status:
	$(MAKE) -C digibank-microservices status
logs:
	$(MAKE) -C digibank-microservices logs
smoke:
	$(MAKE) -C digibank-microservices smoke
validate-stack:
	$(MAKE) -C digibank-microservices validate-stack
newman-scan:
	$(MAKE) -C digibank-microservices newman-scan
zap-scan:
	$(MAKE) -C digibank-microservices zap-scan
down:
	$(MAKE) -C digibank-microservices down
clean:
	$(MAKE) -C digibank-microservices clean
