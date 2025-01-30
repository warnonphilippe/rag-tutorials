# Static ———————————————————————————————————————————————————————————————————————————————————————————————————————————————
LC_LANG				= it_IT
DEFAULT_GOAL 		:= help
SHELL 				= /bin/bash

RED					= \033[0;31m
GREEN				= \033[0;32m
ORANGE				= \033[0;33m
NC					= \033[0m

# Setup ————————————————————————————————————————————————————————————————————————————————————————————————————————————————
env					:= dev
docker-os			:= mac
docker_compose_exec := docker-compose
project_name		:= $(shell basename $(CURDIR))
compose				:= $(docker_compose_exec) --file docker/docker-compose.yml
compose_test		:= $(compose) --file docker/docker-compose.yml

.PHONY: start
start: ## avvia tutti i servizi
		$(compose) -p $(project_name) start

.PHONY: stop
stop: ## ferma l'ambiente di sviluppo
		$(compose) -p $(project_name) stop $(s)

.PHONY: up
up: ## tira su l'ambiente di sviluppo
		$(compose) -p $(project_name) up -d --remove-orphans

.PHONY: enter
enter: ## entra in ambiente ZSH come root
		$(compose) -p $(project_name) exec -u root ollama //bin//sh

.PHONY: erase
erase: ## ferma ed elimina i containers ed i loro volume
		$(compose) -p $(project_name) stop
		$(compose) -p $(project_name) down -v

.PHONY: help
help: ## Mostra questo messaggio
	@cat $(MAKEFILE_LIST) | grep -e "^[a-zA-Z_\-]*: *.*## *" | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
