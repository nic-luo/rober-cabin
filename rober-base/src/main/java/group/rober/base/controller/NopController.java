package group.rober.base.controller;

import group.rober.base.BaseConsts;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NopController {
    @RequestMapping(BaseConsts.NOP_HANDLER)
    public void nop() {}
}
