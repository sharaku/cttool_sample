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

def __exec_single_stage(def stage_name, def stage_param)
{
	echo "debug: __exec_single_stage($stage_param)"
	def _node

	// 使用するnodeを決定する。
	// nodeが指定されていない場合は、masterを使用する。
	if (stage_param.node == null) {
		_node = "master"
	} else {
		_node = stage_param.node
	}

	node (_node) {
		stage(stage_name) {
			if (stage_param.script != null) {
				stage_param.script.each { __script ->
					if (__script.sh != null) {
						sh __script.sh
					} else if (__script.echo  != null) {
						echo __script.echo
					} else if (__script.powershell  != null) {
						powershell __script.sh
					}
				}
			}
		}
	}
}


// パラメータに沿ってstageを実行する
def __exec_stage(def stage_name, def stage_param)
{
	echo "debug: __exec_stage($stage_param)"
	if (stage_param.parallel != null) {
		echo "${stage_param.name} is parallel."
	} else {
		__exec_single_stage(stage_name, stage_param)
	}
}


def __exec_stages(def stages, def stage_list)
{
	echo "debug: __exec_stages($stages, $stage_list), env_A=$env_A env_B=$env_B"
	stages.each { __line ->
		echo "debug: __line=$__line"
		if (stage_list[__line] == null) {
			echo "${__line} is not found."
		} else {
			echo "debug: stage_list[$__line] = ${stage_list[__line]}"
			__exec_stage(__line, stage_list[__line])
		}
	}
}

node {
	stage('onetime setup'){
		checkout scm
		echo "onetime setup"
	}

	def yaml
	script {
		// 設定ファイルを読み込む
		// Pipeline Utility Steps Pluginの関数を使う
		yaml = readYaml(file: 'config.yml')
		echo "$yaml"

		if (yaml.config.env) {
			withEnv(yaml.config.env) {
				__exec_stages(yaml.stages, yaml.stage)
			}
		} else {
			__exec_stages(yaml.stages, yaml.stage)
		}
	}

	stage('onetime teardown'){
		echo "onetime teardown"
	}
}


