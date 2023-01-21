package cn.edu.hqu.databackup.service.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.api.pojo.entity.discussion.Discussion;
import cn.edu.hqu.api.pojo.entity.discussion.DiscussionReport;
import cn.edu.hqu.api.pojo.entity.problem.Category;
import cn.edu.hqu.databackup.pojo.vo.DiscussionVO;

import java.util.List;

/**
 * @author egret
 */
public interface DiscussionService {

    public CommonResult<IPage<Discussion>> getDiscussionList(Integer limit,
                                                            Integer currentPage,
                                                            Integer categoryId,
                                                            String pid,
                                                            Boolean onlyMine,
                                                            String keyword,
                                                            Boolean admin);

    public CommonResult<DiscussionVO>  getDiscussion(Integer did);

    public CommonResult<Void>  addDiscussion(Discussion discussion);

    public CommonResult<Void>  updateDiscussion(Discussion discussion);

    public CommonResult<Void>  removeDiscussion(Integer did);

    public CommonResult<Void>  addDiscussionLike(Integer did, Boolean toLike);

    public CommonResult<List<Category>>  getDiscussionCategory();

    public CommonResult<List<Category>>  upsertDiscussionCategory(List<Category> categoryList);

    public CommonResult<Void>  addDiscussionReport(DiscussionReport discussionReport);

}
