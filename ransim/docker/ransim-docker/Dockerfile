FROM ubuntu:18.04

ARG HTTP_PROXY=${HTTP_PROXY}
ARG HTTPS_PROXY=${HTTPS_PROXY}

ENV http_proxy $HTTP_PROXY
ENV https_proxy $HTTPS_PROXY

RUN \
apt-get clean && \
apt-get update && \
apt-get install -y zip unzip curl wget ssh telnet maven && \
apt-get install -y software-properties-common && \
apt-get install -y jq httpie && \
apt-get install -y python-pip && \
apt-get install -y vim && \
add-apt-repository ppa:openjdk-r/ppa && \
apt-get clean && \
apt-get update && \
apt-get install -y openjdk-8-jdk

RUN useradd --create-home --shell /bin/bash policy

RUN \
    apt-get install -y apt-transport-https && \
apt-key adv --recv-keys --keyserver hkp://keyserver.ubuntu.com:80 0xF1656F24C74CD1D8 && \
apt-get clean && \
apt-get update && \
apt-get install -y mariadb-client

RUN mkdir -p /opt/app/policy /tmp/ransim-install && chown policy /opt/app/policy /tmp/ransim-install

WORKDIR /tmp/ransim-install

COPY install.zip docker-install.sh do-start.sh wait-for-port.sh ./
RUN unzip install.zip && rm install.zip && chown policy * && chmod +x *.sh

USER policy

ENTRYPOINT [ "bash", "./do-start.sh" ]



