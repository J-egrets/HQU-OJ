package cn.edu.hqu.databackup.pojo.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.HashMap;

/**
 * @Author: egret
 */
@Data
public class CommentListVO {

    private IPage<CommentVO> commentList;

    private HashMap<Integer, Boolean>  commentLikeMap;
}