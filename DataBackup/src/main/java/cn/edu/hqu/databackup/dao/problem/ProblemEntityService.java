package cn.edu.hqu.databackup.dao.problem;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.edu.hqu.databackup.pojo.dto.ProblemDTO;
import cn.edu.hqu.databackup.pojo.vo.ImportProblemVO;
import cn.edu.hqu.databackup.pojo.vo.ProblemVO;
import cn.edu.hqu.api.pojo.entity.problem.Problem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @Author: egret
 */

public interface ProblemEntityService extends IService<Problem> {
    Page<ProblemVO> getProblemList(int limit, int currentPage, Long pid, String title,
                                   Integer difficulty, List<Long> tid, String oj);

    boolean adminUpdateProblem(ProblemDTO problemDto);

    boolean adminAddProblem(ProblemDTO problemDto);

    ImportProblemVO buildExportProblem(Long pid, List<HashMap<String, Object>> problemCaseList, HashMap<Long, String> languageMap, HashMap<Long, String> tagMap);
}
