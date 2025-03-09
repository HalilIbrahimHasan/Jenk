pipeline {
    agent any
    
    environment {
        JAVA_HOME = '/Library/Java/JavaVirtualMachines/temurin-11.jdk/Contents/Home'
        PATH = "${JAVA_HOME}/bin:/opt/homebrew/bin:/usr/local/bin:${PATH}"
    }
    
    stages {
        stage('Setup') {
            steps {
                sh '''
                    # Install Java 11 if not present
                    if [ ! -d "${JAVA_HOME}" ]; then
                        echo "Installing Java 11..."
                        brew install --cask temurin11
                    fi
                    
                    # Install Maven if not present
                    if ! command -v mvn &> /dev/null; then
                        echo "Installing Maven..."
                        brew install maven
                    fi
                    
                    # Export environment variables
                    export JAVA_HOME="${JAVA_HOME}"
                    export PATH="${JAVA_HOME}/bin:${PATH}"
                    
                    echo "Java version:"
                    java -version
                    
                    echo "Maven version:"
                    mvn -version
                '''
            }
        }
        
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh '''
                    export JAVA_HOME="${JAVA_HOME}"
                    export PATH="${JAVA_HOME}/bin:${PATH}"
                    mvn clean compile
                '''
            }
        }
        
        stage('Test') {
            steps {
                sh '''
                    export JAVA_HOME="${JAVA_HOME}"
                    export PATH="${JAVA_HOME}/bin:${PATH}"
                    mvn test
                '''
            }
            post {
                always {
                    // Publish Cucumber HTML reports
                    cucumber buildStatus: 'UNSTABLE',
                            reportTitle: 'Cucumber Report',
                            fileIncludePattern: '**/CucumberTestReport.json',
                            trendsLimit: 10,
                            classifications: [
                                [
                                    'key': 'Browser',
                                    'value': 'Chrome'
                                ]
                            ]
                    
                    // Publish Cucumber HTML reports (alternative format)
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/cucumber-reports',
                        reportFiles: 'cucumber-pretty.html',
                        reportName: 'Cucumber HTML Report',
                        reportTitles: 'Cucumber Report'
                    ])
                    
                    // Publish Extent Spark Report
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/SparkReport',
                        reportFiles: 'Spark.html',
                        reportName: 'Extent Spark Report',
                        reportTitles: 'Extent Report'
                    ])
                    
                    // Archive the test results
                    junit '**/target/cucumber-reports/*.xml'
                    
                    // Archive the reports
                    archiveArtifacts artifacts: 'target/cucumber-reports/**/*,target/SparkReport/**/*', 
                                 allowEmptyArchive: true
                }
            }
        }
    }
    
    post {
        always {
            // Clean workspace after build
            cleanWs()
        }
        success {
            echo 'Tests executed successfully!'
        }
        failure {
            echo 'Test execution failed!'
        }
    }
} 