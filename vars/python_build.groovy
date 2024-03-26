def call() {
    pipeline {
        agent any

        stages {
            stage('TEST') {
                steps {
                    echo "TEST"
                }
            }
        }
    }
}

