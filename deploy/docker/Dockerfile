FROM openjdk:14

ARG SimpleSearchJar=SimpleSearch.jar

EXPOSE 8050
ENV searchJarEnv=$SimpleSearchJar
COPY ./../build/libs/$searchJarEnv ./simplesearch/
COPY ./deployments/docker/entrypoint.sh ./
RUN chmod +x ./entrypoint.sh
ENTRYPOINT ["./entrypoint.sh"]
