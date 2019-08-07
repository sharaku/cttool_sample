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

def __exec_single_stage(def ow_env, def stage_param)
{
	def _node

	// �g�p����node�����肷��B
	// node���w�肳��Ă��Ȃ��ꍇ�́Amaster���g�p����B
	if (stage_param.node == null) {
		_node = "master"
	} else {
		_node = stage_param.node
	}

	node (_node) {
		// unstash���ݒ肳��Ă���ꍇ�͑S�����s����B
		deleteDir()
		if (stage_param.unstash != null) {
			stage_param.stash.each { __stash ->
				unstash __stash
			}
		} else {
			unstash 'initialize'
		}

		// �X�N���v�g���Ȃ���Ή������Ȃ��B
		if (stage_param.script != null) {
			def __env = ""
			// ���ϐ���`������΁A���ϐ���ݒ肵�Ă���A
			// shell�����s���Ă���
			if (stage_param.env != null) {
				__env = stage_param.env
			}
			ow_env.each { line ->
				__env += line
			}

			if (__env != "") {
				withEnv(__env) {
					__exec_script(stage_param.script)
				}
			else {
				__exec_script(stage_param.script)
			}
		}

		// stash�͂��̂܂ܓn���B
		// ����āAname, excludes��ݒ肷�邱�ƁB
		if (stage_param.stash != null) {
			stage_param.stash.each { __stash ->
				stash __stash
			}
		}
	}
}


// parallel������s���B
def __exec_parallel(def stage_name, def stage_list, def ow_env, def stage_param)
{
	def __parallel = [:]

	echo "debug: __exec_parallel($stage_name, $stage_list, $stage_param)"

	stage_param.parallel.each { __line ->
		__parallel[__line] = {
			stage(__line) {
				if (stage_list[__line].parallel != null) {
					__exec_parallel(stage_name, stage_list, ow_env, stage_list[__line])
				} else {
					__exec_single_stage(ow_env, stage_list[__line])
				}
			}
		}
	}
	echo "debug: parallel($__parallel)"

	stage(stage_name) {
		parallel(__parallel)
	}
}

// �s�͎��̃t�H�[�}�b�g�Ƃ���
// {jobname} {env:xxx=xxx,xxx=xxx}
def __mk_env(def list)
{
	def __ow_env=[]

	list.each { __line ->
		def vals = __line.split(":")
		if (vals[0] == "env") {
			vals[1].split(",").each { __env ->
				__ow_env += __env
			}
		}
	}
	return __ow_env
}

def __exec_stages(def stages, def stage_list)
{
	stages.each { __line ->
		def __line_split = __line.split(" ")
		def __job = __line_split[0]
		def __ow_env=[]

		// �ǉ��������X�g�����
		__ow_env = __mk_env(__line_split)

		if (stage_list[__job] == null) {
			// �w�肳�ꂽjob�͂���܂���ł����B
			echo "${__job} is not found."
		} else {
			stage(__job) {
				if (stage_list[__job].parallel != null) {
					__exec_parallel(stage_name, stage_list, __ow_env, stage_list[__job])
				} else {
					__exec_single_stage(__ow_env, stage_list[__job])
				}
			}
		}
	}
}

// *********************************************************************
// �������炪�G���g���B
// *********************************************************************
node {
	def yaml

	// clean checkout����
	deleteDir()
	checkout scm
	stash name: 'initialize'

	script {
		// �ݒ�t�@�C����ǂݍ���
		// Pipeline Utility Steps Plugin�̊֐����g��
		yaml = readYaml(file: 'config.yml')
		echo "$yaml"

		timestamps {
			// ���ϐ���`������ꍇ�͊��ϐ���ݒ肷��B
			// �㏑���p�̊��ϐ���`������ΐݒ肷��B
			def __env = ""
			def __stages = ""

			if (yaml.config.env != null) {
				__env = yaml.config.env
			}
			if (params.env != null) {
				def __ow_env = params.env.split("\n")
				__ow_env.each { line ->
					__env += line
				}
			}

			// job�ꗗ�̏㏑���ݒ肪����ꍇ�͏㏑������B
			// params.stages�́A���ϐ��ݒ�������ɓ����Ă���̂ŁA
			// �������ă��X�g�ɂ���B
			if (yaml.stages != null) {
				__stages = yaml.stages
			}
			if (params.stages != null && params.stages != "") {
				def __ow_stages = params.stages.split("\n")
				__ow_stages.each { line ->
					__stages += line
				}
			}

			if (__env != "") {
				withEnv(__env) {
					__exec_stages(__stages, yaml.stage)
				}
			else {
				__exec_stages(__stages, yaml.stage)
			}

		}
	}
}

