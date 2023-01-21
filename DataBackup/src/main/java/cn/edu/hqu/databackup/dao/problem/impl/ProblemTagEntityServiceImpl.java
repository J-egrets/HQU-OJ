package cn.edu.hqu.databackup.dao.problem.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.mapper.ProblemTagMapper;
import cn.edu.hqu.api.pojo.entity.problem.ProblemTag;
import cn.edu.hqu.databackup.dao.problem.ProblemTagEntityService;

/**
 * @Author: egret
 */
@Service
public class ProblemTagEntityServiceImpl extends ServiceImpl<ProblemTagMapper, ProblemTag> implements ProblemTagEntityService {
}