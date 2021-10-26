package com.example.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.dao.CategoryMapper;
import com.example.enums.ResponseEnum;
import com.example.pojo.Category;
import com.example.service.ICategoryService;
import com.example.vo.CategoryVo;
import com.example.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public void findSubCategoryId(Integer id, Set<Integer> resultSet) {
        List<Category> categories = categoryMapper.selectAll();
        findSubCategoryId(id, resultSet, categories);
    }

    private void findSubCategoryId(Integer id, Set<Integer> resultSet, List<Category> categories) {
        for(Category category: categories) {
            if(category.getParentId().equals(id)) {
                resultSet.add(category.getId());
                findSubCategoryId(category.getId(), resultSet, categories);
            }
        }
    }

    @Override
    public ResponseVo insertCategory(Category category) {
        int count = categoryMapper.insertCategory(category);
        if (count == 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.success();
    }

    /**
     * 通过id查找详情
     * @param id
     * @return
     */
    @Override
    public Category selectCategoryDetail(Integer id) {
        Category category = categoryMapper.selectCategoryDetail(id);
        return category;
    }

    /**
     * 通过id查找
     * @param id
     * @return
     */
    @Override
    public CategoryVo selectCategoryById(Integer id) {

        // 先找出所有的类目信息
        List<Category> categoryAllList = categoryMapper.selectAll();
        // log.info("categoryVo = {}", categoryAllList);

        // 对类目进行查找id相等的类目
        List<CategoryVo> categoryVoList = categoryAllList.stream()
                .filter(item -> item.getId().equals(id))
                .map(this::category2CategoryVo)
                .collect(Collectors.toList());
        if(categoryVoList == null || categoryVoList.size() == 0){
            log.warn("查无数据");
            return null;
        }
        CategoryVo rootVo = categoryVoList.get(0);

        // 查找id的parentId及它的父类目
        tofindParentById();
        // 查找parentId = id的子类目
        tangFindChildrenByParentId(categoryAllList,rootVo);

        log.info("voTree end:{}", JSON.toJSONString(rootVo));

        return rootVo;
    }

    /**
     *
     * @param categoryAllList
     * @param rootVo
     * @return
     */
    private void tangFindChildrenByParentId(List<Category> categoryAllList, CategoryVo rootVo) {
        List<CategoryVo> categoryChildrenList = new ArrayList<>();
        for(Category categories: categoryAllList) {
            if(categories.getParentId().equals(rootVo.getId())) {
                categoryChildrenList.add(category2CategoryVo(categories));
            }
        }
        rootVo.setChildren(categoryChildrenList);

        for (CategoryVo categoryVo : categoryChildrenList) {
            this.tangFindChildrenByParentId(categoryAllList,categoryVo);
        }
    }

    private void tofindParentById() {}


    /**
     * 递归查找类目为id的子类目
     * @param parentId         父id
     * @param categoryVoList   类目集合
     * @param categoryVoList     返回值集合
     * @return
     */
    public List<CategoryVo> findChildrenByParentId(Integer parentId, List<Category> categoryAllList, List<CategoryVo> categoryVoList) {
        List<CategoryVo> categoryChildrenList = new ArrayList<>();
           for (CategoryVo categoryVoItem: categoryVoList) {
                for(Category categories: categoryAllList) {

                    if(categories.getParentId().equals(parentId)) {
                        categoryChildrenList.add(category2CategoryVo(categories));
                    }
                }
               categoryVoItem.setChildren(categoryChildrenList);
                // @tang start
               for (CategoryVo categoryVo : categoryChildrenList) {
                   List<CategoryVo> parentList = new ArrayList<>();
                   parentList.add(categoryVo);
                   this.findChildrenByParentId(categoryVo.getId(),categoryAllList,parentList);
               }
               // @tang end

           }




        // log.info("categoryChildrenList = {}", categoryVoList);
        return categoryVoList;
    }

    private CategoryVo category2CategoryVo(Category category) {
        CategoryVo categoryVo = new CategoryVo();
        log.info("category = {}", category);
        BeanUtils.copyProperties(category, categoryVo);
        return categoryVo;
    }
}
