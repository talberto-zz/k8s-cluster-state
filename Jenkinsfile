#!groovy

node {
    stage 'Checkout'
    checkout scm

    env.PATH = "${tool 'Maven 3'}/bin:${env.PATH}"
    env.PATH = "${tool 'docker 1.9.1'}/bin:${env.PATH}"

    stage 'Build'
    sh 'mvn clean package'
    sh 'mvn docker:build'
//    docker.image('496870459963.dkr.ecr.eu-west-1.amazonaws.com/photomanagement/photo-upload-base:1').pull()
}
