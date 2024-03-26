// Directory is path to service, also docker repo name
def call(dir imageName, build) {
    pipeline {
    agent any
    environment {
        PATH = "/var/lib/jenkins/.local/bin:$PATH"
    }
    stages {
        stage('Lint') {
            steps {
                script {
                    sh """#!/usr/bin/env bash
                            pip install pylint
                            pylint --fail-under=5 --disable import-error ./${dir}/*.py
                            """
                }
            }
        }
        stage('Security') {
            steps {
                script {
                    sh """#!/usr/bin/env bash
                            pip install bandit
                            bandit -r ./${dir}
                            """
                }
            }
        }
        stage('Package') {
            when {
                expression { ${build} == true }
            }
            steps {
                withCredentials([string(credentialsId: 'Dockerhub', variable: 'TOKEN')]) {
                    sh "cd ./${dir}"
                    sh "docker login -u 'tristan007' -p '$TOKEN' docker.io"
                    sh "docker build -t ${dir}:latest --tag tristan007/${dir}:${imageName} ."
                    sh "docker push tristan007/${dir}:${imageName}"
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo 'Deploy'
                }
            }
        }
        // stage('Unit Test') {
        //     steps {
        //         script {
        //             // Remove any existing test results
        //             def test_reports_exist = fileExists 'test-reports'
        //             if (test_reports_exist) {
        //                 sh 'rm test-reports/*.xml || true'
        //             }
        //             def api_test_reports_exist = fileExists 'api-test-reports'
        //             if (api_test_reports_exist) {
        //                 sh 'rm api-test-reports/*.xml || true'
        //             }

        //             // Code to Run the Unit Tests with for loop
        //             def testFiles = findFiles(glob: 'test_*.py')
        //             for (file in testFiles) {
        //                 // sh "source venv/bin/activate && python3 ${file.path}"
        //                 sh "source venv/bin/activate && coverage run --omit */dist-packages/*,*/site-packages/* ${file.path}"
        //             }
        //         }
        //     }
        //     post {
        //         always {
        //             // Process the test results if they exist
        //             script {
        //                 def test_reports_exist = fileExists 'test-reports'
        //                 if (test_reports_exist) {
        //                     junit 'test-reports/*.xml'
        //                 }
        //                 def api_test_reports_exist = fileExists 'api-test-reports'
        //                 if (api_test_reports_exist) {
        //                     junit 'api-test-reports/*.xml'
        //                 }
        //             }
        //             sh 'source venv/bin/activate && coverage report -m'
        //         }
        //     }
        // }
        // stage('Package') {
        //     when {
        //         expression { env.GIT_BRANCH == 'origin/main' }
        //     }
        //     steps {
        //         withCredentials([string(credentialsId: 'DockerHub', variable: 'TOKEN')]) {
        //             sh "docker login -u 'tristan007' -p '$TOKEN' docker.io"
        //             sh "docker build -t ${dockerRepoName}:latest --tag tristan007/${dockerRepoName}:${imageName} ."
        //             sh "docker push tristan007/${dockerRepoName}:${imageName}"
        //         }
        //     }
        // }
        // stage('Zip Artifacts') {
        //     steps {
        //         // Archive artifacts
        //         // Zip all Python files
        //         script {
        //             sh 'zip -r app.zip *.py'
        //             archiveArtifacts 'app.zip'
        //         }
        //     }
        // }
    }


}

}