package group.rober.base.menu.service;

import group.rober.base.menu.model.MenuEntry;
import group.rober.sql.core.DataAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by luyu on 2017/12/19.
 */
@Service
public class MenuService {

    @Autowired
    private DataAccessor dataAccessor;

    public List<MenuEntry> getUserMenu() {
        List<MenuEntry> menuList = dataAccessor.selectList(MenuEntry.class,"select * from FOWK_MENU where ENABLED='Y' ORDER BY SORT_CODE ASC");
        return menuList;
    }

    public List<MenuEntry> getAllMenus() {
        List<MenuEntry> menuList = dataAccessor.selectList(MenuEntry.class,"select * from FOWK_MENU where ENABLED='Y' ORDER BY SORT_CODE ASC");
        return menuList;
    }
}
