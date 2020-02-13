node {
 deleteDir()
 
//  stage('Merge') {
//   sh "git init"
//   sh "git fetch --no-tags --progress git@git:group/reponame.git +refs/heads/*:refs/remotes/origin/* --depth=200"
//   sh "git checkout origin/${env.BRANCH_name}"
//   sh "git merge origin/${env.sourceBranch}"
//   sh "git log --graph --abbrev-commit --max-count=10"
//  }

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
    minSeverity: 'INFO',
    maxNumberOfViolations: 99999,
    keepOldComments: false,
 
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