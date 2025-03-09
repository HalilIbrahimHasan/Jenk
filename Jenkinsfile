pipeline {
    agent any
    
    tools {
        maven 'Maven'
        jdk 'JDK11'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    // Publish Cucumber HTML reports
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/cucumber-reports/cucumber-pretty',
                        reportFiles: 'index.html',
                        reportName: 'Cucumber HTML Report',
                        reportTitles: ''
                    ])
                    
                    // Publish Extent Reports
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/spark',
                        reportFiles: 'index.html',
                        reportName: 'Extent Spark Report',
                        reportTitles: ''
                    ])
                }
            }
        }
    }
} 