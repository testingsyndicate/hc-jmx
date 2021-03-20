pipeline {
  agent {
    label 'gradle'
  }

  stages {
    stage('Clean') {
      steps {
        sh 'gradle clean'
      }
    }

    stage('Build') {
      steps {
        sh 'gradle build'
      }
    }

    stage('Publish') {
      when {
        branch 'master'
      }
      environment {
        PUBLISH = credentials('publishing')
        KEY = credentials('signing')
      }
      steps {
        sh 'gradle publish -PpublishUsername=$PUBLISH_USR -PpublishPassword=$PUBLISH_PSW -PsigningKey=$KEY'
      }
    }
  }
}
