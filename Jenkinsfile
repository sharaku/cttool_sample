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
			// 設定ファイルを読み込む
			// Pipeline Utility Steps Pluginの関数を使う
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


