#!groovy

//load 'libcitool.groovy'

def err_msg = ""
// パラメータに沿ってstageを実行する
def __exec_stage(def stage_param)
{
}

def __mk_parallel(def parallel_list, def stage_list)
{
	def parallel = [:]
	parallel_list.each { oneline ->
		echo oneline
//		parallel[oneline] = {
//			node (stage_list[oneline].node){
//				__exec_stage(stage_list[oneline])
//			}
//		}
	}
	return parallel
}

node {
	stage('onetime setup'){
		checkout scm
		echo "onetime setup"
	}

	stage('job1'){
		echo "job1"
		def yaml
		script {
			// 設定ファイルを読み込む
			// Pipeline Utility Steps Pluginの関数を使う
			yaml = readYaml(file: 'config.yml')
			echo "$yaml"
		}
	}

	stage('job2'){
		echo "job2"
	}

	stage('onetime teardown'){
		echo "onetime teardown"
	}
}


