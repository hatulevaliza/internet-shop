pipeline {
    agent any

    stages {
        stage('Check Structure') {
            steps {
                bat 'if exist pom.xml (echo pom.xml found) else (exit 1)'
                bat 'if exist Jenkinsfile (echo Jenkinsfile found) else (exit 1)'
                bat 'if exist src (echo src found) else (exit 1)'
            }
        }
        stage('Build') {
            steps {
                bat 'mvn compile'
            }
        }
        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }
        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }
    }
}