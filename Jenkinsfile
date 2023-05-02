pipeline {
    agent any
    tools {
        maven 'maven_3_5_0'
    }

    stages {
        stage('Build Maven') {
            steps {
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/nagendrapalla/Gallery']])
                bat 'mvn clean install'
            }
        }
        stage('Build Docker Image') {
            steps {
                bat 'docker build -t palla4you/imgur-gallery-api .'
            }
        }
        stage('Push Docker Image to Hub') {
            steps {
                withCredentials([string(credentialsId: 'imgur-docker', variable: 'imgurdocker')]) {
                    bat 'docker login -u palla4you -p Palla@1994'
                }

                bat 'docker push palla4you/imgur-gallery-api'
            }
        }
    }
}