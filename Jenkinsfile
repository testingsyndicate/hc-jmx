pipeline {
  agent {
    label 'gradle'
  }

  stages {
    stage('Clean') {
      steps {
        sh './gradlew clean'
      }
    }

    stage('Build') {
      steps {
        sh './gradlew build'
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
        sh './gradlew publish -PpublishUsername=$PUBLISH_USR -PpublishPassword=$PUBLISH_PSW -PsigningKey=$KEY'
      }
    }
  }
}
