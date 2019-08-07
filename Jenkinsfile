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
			def __env = ""

			// 環境変数定義があれば、環境変数を設定してから、
			// shellを実行していく
			// 上書き用の環境変数定義があれば設定する。
			if (stage_param.env != null) {
				__env = stage_param.env
			}

			// スクリプトを実行する。
			withEnv(__env) {
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

// *********************************************************************
// ここからがエントリ。
// *********************************************************************
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

		timestamps {
			// 環境変数定義がある場合は環境変数を設定する。
			// 上書き用の環境変数定義があれば設定する。
			def __env = ""
			def __stages = ""

			if (yaml.config.env != null) {
				__env = yaml.config.env
			}
			if (params.env != null && params.env != "") {
				def __ow_env = params.env.split("\n")
				__ow_env.each { line ->
					__env += line
				}
			}

			// job一覧の上書き設定がある場合は上書きする。
			// params.stagesは、環境変数設定も同時に入ってくるので、
			// 分離してリストにする。
			if (yaml.stages != null) {
				__stages = yaml.stages
			}
			if (params.stages != null && params.stages != "") {
				def __ow_stages = params.stages.split("\n")
				__ow_stages.each { line ->
					__stages += line
				}
			}

			withEnv(__env) {
				echo "$__stages"
				__exec_stages(__stages, yaml.stage)
			}
		}
	}
}

