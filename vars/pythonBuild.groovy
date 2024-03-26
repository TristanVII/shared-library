// vars/testFunction.groovy
def call(String message) {
    pipeline {
        agent any
        stages {
            stage('Test Stage') {
                steps {
                    echo "Hello from the shared library!"
                    echo "Message: ${message}"
                }
            }
        }
    }
}
}