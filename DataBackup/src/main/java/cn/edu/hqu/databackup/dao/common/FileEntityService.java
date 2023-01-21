package cn.edu.hqu.databackup.dao.common;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.api.pojo.entity.common.File;
import cn.edu.hqu.databackup.pojo.vo.ACMContestRankVO;
import cn.edu.hqu.databackup.pojo.vo.OIContestRankVO;

import java.util.List;

/**
 * @author egret
 */
public interface FileEntityService extends IService<File> {
    int updateFileToDeleteByUidAndType(String uid, String type);

    int updateFileToDeleteByGidAndType(Long gid, String type);

    List<File> queryDeleteAvatarList();

    List<File> queryCarouselFileList();

    List<List<String>> getContestRankExcelHead(List<String> contestProblemDisplayIDList, Boolean isACM);

    List<List<Object>> changeACMContestRankToExcelRowList(List<ACMContestRankVO> acmContestRankVOList,
                                                          List<String> contestProblemDisplayIDList,
                                                          String rankShowName);

    List<List<Object>> changOIContestRankToExcelRowList(List<OIContestRankVO> oiContestRankVOList,
                                                        List<String> contestProblemDisplayIDList,
                                                        String rankShowName);
}
