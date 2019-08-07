#!groovy

def __exec_script(def script)
{
	if (script != null) {
		script.each { __script ->
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
		// unstashが設定されている場合は全部実行する。
		deleteDir()
		if (stage_param.unstash != null) {
			stage_param.stash.each { __stash ->
				unstash __stash
			}
		} else {
			unstash 'initialize'
		}

		// スクリプトがなければ何もしない。
		if (stage_param.script != null) {
			// 環境変数定義があれば、環境変数を設定してから、
			// shellを実行していく
			if (stage_param.env != null) {
				withEnv(stage_param.env) {
					__exec_script(stage_param.script)
				}
			} else {
				__exec_script(stage_param.script)
			}
		}

		// stashはそのまま渡す。
		// よって、name, excludesを設定すること。
		if (stage_param.stash != null) {
			stage_param.stash.each { __stash ->
				stash __stash
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
				if (stage_list[__line].parallel != null) {
					__exec_parallel(stage_name, stage_list, stage_list[__line])
				} else {
					__exec_single_stage(stage_list[__line])
				}
			}
		}
	}
}

// ここからがエントリ。
node {
	def yaml

	// clean checkoutする
	deleteDir()
	checkout scm
	stash name: 'initialize'

	script {
		// 設定ファイルを読み込む
		// Pipeline Utility Steps Pluginの関数を使う
		yaml = readYaml(file: 'config.yml')
		echo "$yaml"

		// 環境変数定義がある場合は環境変数を設定する。
		timestamps {
			def __env = ""
			if (yaml.config.env != null) {
				__env = yaml.config.env
			}
			if (params.env != null) {
				def __ow_env = params.env.split("\n")
				__ow_env.each { line ->
					__env += line
				}
			}
			echo "$__env"
			withEnv(__env) {
				__exec_stages(yaml.stages, yaml.stage)
			}
		}
	}
}

