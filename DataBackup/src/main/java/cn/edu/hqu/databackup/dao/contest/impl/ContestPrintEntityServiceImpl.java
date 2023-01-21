package cn.edu.hqu.databackup.dao.contest.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import cn.edu.hqu.databackup.mapper.ContestPrintMapper;
import cn.edu.hqu.api.pojo.entity.contest.ContestPrint;
import cn.edu.hqu.databackup.dao.contest.ContestPrintEntityService;

/**
 * @Author: egret
 */
@Service
public class ContestPrintEntityServiceImpl extends ServiceImpl<ContestPrintMapper, ContestPrint> implements ContestPrintEntityService {
}