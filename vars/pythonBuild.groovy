def call() {
    pipeline {
        agent any
        stage('Test') {
            echo 'Hello from Shared Library'
        }
    }
}
