package cn.project.jd.impl;

import cn.project.jd.dao.ItemDao;
import cn.project.jd.pojo.Item;
import cn.project.jd.service.ItemServie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemServie {
    @Autowired
    private ItemDao idap;
    @Override
    public void save(Item it) {
        idap.save(it);
    }

    @Override
    public List<Item> findall(Item it) {
        //声明查询条件
        Example<Item> example = Example.of(it);
        //根据条件查询数据
       List<Item> items = this.idap.findAll(example);
        return items;
    }
}
