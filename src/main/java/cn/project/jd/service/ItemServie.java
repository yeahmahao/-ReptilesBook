package cn.project.jd.service;

import cn.project.jd.pojo.Item;

import java.util.List;

public interface ItemServie {


    /**
     * 保存商品
     * @param it
     */
    public void save(Item it);

    /**it
     * 根据条件查询
     * @param it
     * @return
     */
    public List<Item> findall(Item it);

}
