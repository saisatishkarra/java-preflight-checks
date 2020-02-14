FROM openjdk:jre-alpine

ARG TARGET_SRC_DIR

ENV INSTALL_DIR /usr/local/lib
ENV CHECKSTYLE_VERSION 8.21
ENV PMD_VERSION 6.15.0

# Install curl
RUN apk add bash curl libxslt

# Install checkstyle
RUN curl -L -o ${INSTALL_DIR}/checkstyle.jar https://github.com/checkstyle/checkstyle/releases/download/checkstyle-${CHECKSTYLE_VERSION}/checkstyle-${CHECKSTYLE_VERSION}-all.jar

# Install PMD
RUN cd ${INSTALL_DIR} && \
  curl -L https://github.com/pmd/pmd/releases/download/pmd_releases%2F${PMD_VERSION}/pmd-bin-${PMD_VERSION}.zip --output pmd-bin-${PMD_VERSION}.zip &&\
  unzip pmd-bin-${PMD_VERSION}.zip && \
  mv pmd-bin-${PMD_VERSION} pmd && \
  rm pmd-bin-${PMD_VERSION}.zip


# Copy scripts
# COPY ./bin/ /usr/local/bin/

COPY ./bin/entrypoint.sh /usr/local/bin/entrypoint.sh
COPY ./bin/checkstyle-frames.xsl /usr/local/bin/checkstyle-frames.xsl

RUN chmod +x /usr/local/bin/*

WORKDIR /work
CMD ["/usr/local/bin/entrypoint.sh"]

