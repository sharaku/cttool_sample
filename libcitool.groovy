

// �p�����[�^�ɉ�����stage�����s����
def __exec_stage(def stage_param)
{
    // �f�B���N�g�����N���A���A�t�@�C�����}�X�^�[����]������B
    deleteDir()
    if (stage_param.unstash != null) {
        unstash 'initialize'
    } else {
        unstash stage_param.unstash.name
    }

    script.each { __line ->
        if (__line.sh != null) {
            sh __line.sh
        }
    }

    // ���ʂ̕ۑ��w�����������ꍇ�́Astash���s���B
    if (stage_param.stash != null) {
        stash stage_param.stash
    }
    
}

def __mk_parallel(def parallel_list, def stage_list)
{
    def parallel = [:]
    parallel_list.each { oneline ->
        parallel[oneline] = {
            node (stage_list[oneline].node){
                __exec_stage(stage_list[oneline])
          }
        }
    }
    return parallel
}
//
//stage_list = [job1:[node:"master", sh:"make", unstash:"projects"], job2:[node:"master", sh:"make"]]
//parallel_list = ["job1", "job2"]
//
//__mk_parallel(parallel_list, stage_list)
//