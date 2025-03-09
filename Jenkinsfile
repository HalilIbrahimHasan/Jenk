pipeline {
    agent any
    
    environment {
        JAVA_HOME = '/Users/selma/java/temurin-11/Contents/Home'
        PATH = "${JAVA_HOME}/bin:/opt/homebrew/bin:/usr/local/bin:${PATH}"
    }
    
    stages {
        stage('Setup') {
            steps {
                sh '''
                    # Create Java directory if it doesn't exist
                    mkdir -p /Users/selma/java
                    
                    # Install Java 11 if not present
                    if [ ! -d "${JAVA_HOME}" ]; then
                        echo "Installing Java 11..."
                        cd /Users/selma/java
                        
                        # Download and extract Temurin JDK 11
                        curl -L -o temurin11.tar.gz https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.26%2B4/OpenJDK11U-jdk_aarch64_mac_hotspot_11.0.26_4.tar.gz
                        tar xzf temurin11.tar.gz
                        rm -rf temurin-11
                        mv jdk-11.0.26+4 temurin-11
                        rm temurin11.tar.gz
                    fi
                    
                    # Print directory structure for debugging
                    echo "Java directory structure:"
                    ls -la "${JAVA_HOME}/bin"
                    
                    # Install Maven if not present using Homebrew
                    if ! command -v mvn &> /dev/null; then
                        echo "Installing Maven..."
                        brew install maven
                    fi
                    
                    # Export environment variables
                    export JAVA_HOME="${JAVA_HOME}"
                    export PATH="${JAVA_HOME}/bin:${PATH}"
                    
                    echo "Environment variables:"
                    echo "JAVA_HOME=${JAVA_HOME}"
                    echo "PATH=${PATH}"
                    
                    echo "Java version:"
                    "${JAVA_HOME}/bin/java" -version
                    
                    echo "Maven version:"
                    JAVA_HOME="${JAVA_HOME}" mvn -version
                    
                    # Verify Java installation
                    if [ ! -x "${JAVA_HOME}/bin/java" ]; then
                        echo "Java binary not found or not executable at: ${JAVA_HOME}/bin/java"
                        exit 1
                    fi
                    
                    # Verify Maven installation
                    if ! JAVA_HOME="${JAVA_HOME}" mvn -version > /dev/null 2>&1; then
                        echo "Maven verification failed"
                        exit 1
                    fi
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
                    
                    echo "Build environment:"
                    echo "JAVA_HOME=${JAVA_HOME}"
                    echo "PATH=${PATH}"
                    
                    JAVA_HOME="${JAVA_HOME}" mvn clean compile
                '''
            }
        }
        
        stage('Test') {
            steps {
                sh '''
                    export JAVA_HOME="${JAVA_HOME}"
                    export PATH="${JAVA_HOME}/bin:${PATH}"
                    JAVA_HOME="${JAVA_HOME}" mvn test
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