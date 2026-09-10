.PHONY: help build test image-build up restart status logs smoke down clean

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
down:
	$(MAKE) -C digibank-microservices down
clean:
	$(MAKE) -C digibank-microservices clean
