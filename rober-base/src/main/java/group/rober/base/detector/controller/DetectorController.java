package group.rober.base.detector.controller;

import group.rober.base.detector.*;
import group.rober.base.detector.model.Detector;
import group.rober.base.detector.model.DetectorItem;
import group.rober.base.detector.service.DetectorService;
import group.rober.runtime.holder.ApplicationContextHolder;
import group.rober.runtime.holder.WebHolder;
import group.rober.runtime.kit.BeanKit;
import group.rober.runtime.kit.StringKit;
import group.rober.runtime.lang.MapData;
import group.rober.runtime.lang.RoberException;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "DetectorController", description = "自动风险探测执行器")
@RestController
@RequestMapping("/base")
public class DetectorController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    protected DetectorService detectorService;

    /**
     * 取探测器数据，由于要输出到前端，因为把敏感字段清除掉
     *
     * @param detectorCode
     * @return
     */
    @GetMapping("/detector/{detectorCode}")
    public Detector getDetector(@PathVariable("detectorCode") String detectorCode){
        Detector detector = detectorService.getDetector(detectorCode);
        Detector ret = BeanKit.deepClone(detector);
        ret.setInitClass(null);
        ret.setRevision(0L);
        ret.setCreatedBy(null);
        ret.setCreatedTime(null);
        ret.setUpdatedBy(null);
        ret.setUpdatedTime(null);

        ret.getItems().forEach(item -> {
            item.setItemStatus(null);
            item.setSuccessMessage(null);
            item.setExecType(null);
            item.setExecCondition(null);
            item.setExecScript(null);
            item.setRevision(0L);
            item.setCreatedBy(null);
            item.setCreatedTime(null);
            item.setUpdatedBy(null);
            item.setUpdatedTime(null);
        });

        return ret;
    }

    /**
     * 执行一个检查项
     *
     * @param detectorCode
     * @param itemCode
     * @return
     */
    @GetMapping("/detector/exec/{detectorCode}/{itemCode}")
    public DetectorMessage exec(@PathVariable("detectorCode") String detectorCode,@PathVariable("itemCode") String itemCode){
        DetectorMessage message = new DetectorMessage();

        MapData params = new MapData();
        params.putAll(WebHolder.getRequestParameterMap());

        Detector detector = detectorService.getDetector(detectorCode);
        if(detector!=null){
            String className = detector.getInitClass();

            DetectorContext context = new DetectorContext();

            Object bean;
            //根据初始化类进行初始化,把容器参数初始化好
            if(StringKit.isNotBlank(className)){
                bean = ApplicationContextHolder.getBeanByClassName(className);
                if(!(bean instanceof DetectorContextInitor)){
                    throw new RoberException("{0} is not instance of {1}",className,DetectorContextInitor.class);
                }
            } else {
                bean = ApplicationContextHolder.getBean(DefaultDetectorContextInitor.class);
            }
            DetectorContextInitor initor = (DetectorContextInitor)bean;
            initor.init0(context,params);
            initor.init(context);


            //查找执行项,并且执行他
            DetectorItem detectorItem = detector.lookupItem(itemCode);
            if(detectorItem !=null){
                String execScript = detectorItem.getExecScript();
                if(StringKit.isNotBlank(execScript)){
                    try{
//                        Thread.sleep(1000);
//                        DetectorItemExecutor executor = ApplicationContextHolder.getBean(execScript, DetectorItemExecutor.class);
                        DetectorItemExecutor executor = ApplicationContextHolder.getBeanByClassName(execScript);
                        return executor.exec(context);
                    }catch(Exception e){
                        logger.error("",e);
                        message.setPass(false);
                        message.getMessage().add(StringKit.format("系统错误：检查项[{0}-{1}]运行出错:{2}",detectorCode,itemCode,e.getMessage()));
                    }
                }else{
                    message.setPass(false);
                    message.getMessage().add(StringKit.format("系统错误：检查项[{0}-{1}]配置的脚本有误",detectorCode,itemCode));
                }
            }else{
                message.setPass(false);
                message.getMessage().add(StringKit.format("系统错误：检查项[{0}-{1}]不存在",detectorCode,itemCode));
            }
        }else {
            message.setPass(false);
            message.getMessage().add(StringKit.format("系统错误：检查器{0}不存在",detectorCode));
        }

        return message;
    }
}
