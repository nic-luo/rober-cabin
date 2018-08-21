package group.rober.base.detector.service;

import group.rober.base.detector.model.Detector;
import group.rober.base.detector.model.DetectorItem;
import group.rober.sql.core.DataAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetectorService {
    public static final String CACHE_KEY = "DetectorCache";

    @Autowired
    protected DataAccessor dataAccessor;

    @Cacheable(value = CACHE_KEY, key = "#code")
    public Detector getDetector(String code) {

        Detector detector = dataAccessor.selectOne(Detector.class,
                "select * from CONF_DETECTOR where CODE=:code"
                , "code", code);
        if (detector != null) {
            List<DetectorItem> detectorItemList =
                    dataAccessor.selectList(DetectorItem.class
                            , "select * from CONF_DETECTOR_ITEM where DETECTOR_CODE=:detectorCode and ITEM_STATUS=:itemStatus order by SORT_CODE ASC,GROUP_CODE ASC,ITEM_CODE ASC"
                            , "detectorCode", code,
                            "itemStatus", "VALID");
            detector.setItems(detectorItemList);
        }


        return detector;
    }
}
