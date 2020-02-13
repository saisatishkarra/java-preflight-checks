#!/bin/bash

function shutdown {
  kill -s SIGTERM $NODE_PID
  wait $NODE_PID
}

trap shutdown SIGTERM SIGINT

#echo "entrypoint.sh >> received: [$@]"
# exec "$@"

usage() {
    echo "$0 SRC_DIR PMD_REPORT_FILE CHECK_STYLE_REPORT_FILE PMD_FILE_FORMAT  CHECK_STYLE_CONFIG_LOC"
    exit 1
}

if [[ -z $1 ]] || [[ -z $2 ]] || [[ -z $3 ]] ; then
    usage
else
    SRC_DIR=$1
    PMD_REPORT_FILE=$2
    CHECK_STYLE_REPORT_FILE=$3
fi


# pmd java
echo "Running PMD"
/usr/local/lib/pmd/bin/run.sh pmd -d  ${SRC_DIR} -f html -rulesets java-quickstart -l java -r ${PMD_REPORT_FILE}.html

# checkstyle
echo "Running Checkstyle"
java -jar /usr/local/lib/checkstyle.jar ${SRC_DIR} -f xml -c /google_checks.xml -o cs-report.xml
#echo "Convertin Checkstyle xml to html report using xsl"
#xsltproc -o cs-report.xml checkstyle-frames.xsl ${CHECK_STYLE_REPORT_FILE}.html


# Code Coverage with OpenClover
# echo "Running OpenClover Code Coverage"
# java -jar /usr/local/lib/openclover.jar

