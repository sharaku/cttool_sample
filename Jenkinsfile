#!groovy

//load 'libcitool.groovy'

def __exec_single_stage(def stage_param)
{
	def _node

	// 使用するnodeを決定する。
	// nodeが指定されていない場合は、masterを使用する。
	if (stage_param.node == null) {
		_node = "master"
	} else {
		_node = stage_param.node
	}

	node (_node) {
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


// parallel操作を行う。
def __exec_parallel(def stage_name, def stage_list, def stage_param)
{
	def __parallel = [:]

	echo "debug: __exec_parallel($stage_name, $stage_list, $stage_param)"

	stage_param.parallel.each { __line ->
		__parallel[__line] = {
			stage(__line) {
				if (stage_list[__line].parallel != null) {
					__exec_parallel(stage_name, stage_list, stage_list[__line])
				} else {
					__exec_single_stage(stage_list[__line])
				}
			}
		}
	}
	echo "debug: parallel($__parallel)"

	stage(stage_name) {
		parallel(__parallel)
	}
}


def __exec_stages(def stages, def stage_list)
{
	stages.each { __line ->
		if (stage_list[__line] == null) {
			// 指定されたjobはありませんでした。
			echo "${stage_list[__line]} is not found."
		} else {
			stage(__line) {
				__exec_stage(__line, stage_list, stage_list[__line])
				if (stage_list[__line].parallel != null) {
					__exec_parallel(stage_name, stage_list, stage_list[__line])
				} else {
					__exec_single_stage(stage_list[__line])
				}
			}
		}
	}
}

node {
	def yaml

	checkout scm
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
}


