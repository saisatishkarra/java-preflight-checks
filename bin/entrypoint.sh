#!/bin/bash

function shutdown {
  kill -s SIGTERM $NODE_PID
  wait $NODE_PID
}

trap shutdown SIGTERM SIGINT

#echo "entrypoint.sh >> received: [$@]"
# exec "$@"

usage() {
    echo "$0 SRC_DIR"
    exit 1
}

if [[ -z $1 ]]; then
    usage
else
    SRC_DIR=$1
fi


# pmd java
echo "Running PMD"
mkdir pmd
/usr/local/lib/pmd/bin/run.sh pmd -d  ${SRC_DIR} -f xml -rulesets java-quickstart -l java -r pmd/pmd.xml

# checkstyle
echo "Running Checkstyle"
mkdir checkstyle-result
java -jar /usr/local/lib/checkstyle.jar ${SRC_DIR} -f xml -c /sun_checks.xml -o checkstyle-result/checkstyle-result.xml
#echo "Convertin Checkstyle xml to html report using xsl"
#xsltproc -o cs-report.xml checkstyle-frames.xsl ${CHECK_STYLE_REPORT_FILE}.html


# Code Coverage with OpenClover
# echo "Running OpenClover Code Coverage"
# java -jar /usr/local/lib/openclover.jar

