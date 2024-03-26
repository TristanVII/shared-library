def call() {
    pipeline {
    agent any

    stages {
        stage('Build') {
            echo "TESTTEST"
        }
    }


}

}