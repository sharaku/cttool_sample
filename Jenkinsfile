#!groovy

//load 'libcitool.groovy'

def err_msg = ""

node {
	stage('onetime setup'){
		echo "onetime setup"
	}

	stage('job1'){
		echo "job1"
		def yaml
		script {
			// �ݒ�t�@�C����ǂݍ���
			// Pipeline Utility Steps Plugin�̊֐����g��
			yaml = readYaml(file: 'config.yml')
		}
		echo yaml
	}

	stage('job2'){
		echo "job2"
	}

	stage('job3'){
		echo "job3"
	}
}


