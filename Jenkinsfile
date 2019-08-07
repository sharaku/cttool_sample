#!groovy

//load 'libcitool.groovy'

def err_msg = ""
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

def __exec_single_stage(def stage_param)
{
	echo "debug: __exec_stage($stage_param)"
	def _node
	if (stage_param.node == null) {
		_node = "master"
	} else {
		_node = stage_param.node
	}

	node (_node){
		stage_param.script.each { __script ->
			if (__script.sh != null) {
				sh __script.sh
			}
		}
	}
}


// パラメータに沿ってstageを実行する
def __exec_stage(def stage_param)
{
	echo "debug: __exec_stage($stage_param)"
	if (stage_param.parallel != null) {
		echo "${stage_param.name} is parallel."
	} else {
		__exec_single_stage(stage_param)
	}
}


def __stages(def stages, def stage_list)
{
	echo "debug: __stages($stages, $stage_list)"
	stages.each { __line ->
		if (stage_list[__line] == null) {
			echo "${__line} is not found."
		} else {
			__exec_stage(stage_list[__line])
		}
	}
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
			__stages(yaml.stages, yaml.stage)
		}
	}

	stage('job2'){
		echo "job2"
	}

	stage('onetime teardown'){
		echo "onetime teardown"
	}
}


