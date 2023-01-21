package cn.edu.hqu.databackup.dao.problem.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.mapper.CategoryMapper;
import cn.edu.hqu.api.pojo.entity.problem.Category;
import cn.edu.hqu.databackup.dao.problem.CategoryEntityService;

/**
 * @Author: egret
 */
@Service
public class CategoryEntityServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryEntityService {
}