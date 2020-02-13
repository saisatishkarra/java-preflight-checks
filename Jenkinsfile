node {

 deleteDir()

 stage('Checkout') {
    checkout scm
 }

 stage('Docker run tests'){
     sh 'docker build -t checks:latest .'
     sh 'docker run --rm -d -w /work -v $(pwd):/work checks:latest /usr/local/bin/entrypoint.sh src/main/java'
 }
 stage('Static code analysis') {
  // sh "${mvnHome}/bin/mvn package -DskipTests -Dmaven.test.failure.ignore=false -Dsurefire.skip=true -Dmaven.compile.fork=true -Dmaven.javadoc.skip=true"

  step([
   $class: 'ViolationsToGitHubRecorder', 
   config: [
    gitHubUrl: 'https://api.github.com/',
    repositoryOwner: 'saisatishkarra', 
    repositoryName: 'java-preflight-checks',  
    pullRequestId: "${env.CHANGE_ID}", 

    // Only specify one of these!
    credentialsId: 'github-sai',

    createCommentWithAllSingleFileComments: true, 
    createSingleFileComments: true, 
    commentOnlyChangedContent: true, 
    commentOnlyChangedFiles: true,
    minSeverity: 'ERROR',
    maxNumberOfViolations: 99999,
    keepOldComments: true,
 
    commentTemplate: """
    **Reporter**: {{violation.reporter}}{{#violation.rule}}
    
    **Rule**: {{violation.rule}}{{/violation.rule}}
    **Severity**: {{violation.severity}}
    **File**: {{violation.file}} L{{violation.startLine}}{{#violation.source}}
    
    **Source**: {{violation.source}}{{/violation.source}}
    
    {{violation.message}}
    """,

    violationConfigs: [
     [ pattern: '.*/checkstyle-result\\.xml$', parser: 'CHECKSTYLE', reporter: 'Checkstyle' ], 
     [ pattern: '.*/findbugsXml\\.xml$', parser: 'FINDBUGS', reporter: 'Findbugs' ], 
     [ pattern: '.*/pmd\\.xml$', parser: 'PMD', reporter: 'PMD' ], 
    ]
   ]
  ])
 }
}