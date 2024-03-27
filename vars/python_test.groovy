// Directory is path to service, also docker repo name
def call(dir, imageName, build) {
    pipeline {
        agent any
        environment {
            PATH = "/var/lib/jenkins/.local/bin:$PATH"
        }
        stages {
            stage('Lint') {
                steps {
                    script {
                        sh """
                            pip install pylint
                            pylint --fail-under=5 --disable import-error ./${dir}/*.py
                            """
                    }
                }
            }
            stage('Security') {
                steps {
                    script {
                        sh """
                            pip install bandit
                            bandit -r ./${dir}
                            """
                    }
                }
            }
            stage('Package') {
                when {
                    expression { env.GIT_BRANCH == 'origin/main' }
                }
                steps {
                    withCredentials([string(credentialsId: 'Dockerhub', variable: 'TOKEN')]) {
                        script {
                            sh """
                            cd ${dir}
                            docker login -u 'tristan007' -p '$TOKEN' docker.io
                            docker build -t ${dir}:latest --tag tristan007/${dir}:${imageName} .
                            docker push tristan007/${dir}:${imageName}
                        """
                        }
                    }
                }
            }

            stage('Deploy') {
                steps {
                    sshagent(credentials : ['ssh-key']) {
                        // https://stackoverflow.com/questions/18522647/run-ssh-and-immediately-execute-command - Run commands using quotes
                        sh 'ssh -t -t tristandavis888@34.118.240.191 -o StrictHostKeyChecking=no "echo pwd"'
                    }
                }
            }
        }
    }
}