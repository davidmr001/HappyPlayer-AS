package com.zhangliangming.hp.ui.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class Category implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 分类名
     */
    private String mCategoryName;
    /**
     * 分类的内容
     */
    private List<AudioInfo> mCategoryItem = new ArrayList<AudioInfo>();

    public Category(String mCategroyName) {
        mCategoryName = mCategroyName;
    }

    public String getmCategoryName() {
        return mCategoryName;
    }

    public void addItem(AudioInfo audioInfo) {
        mCategoryItem.add(audioInfo);
    }

    public List<AudioInfo> getCategoryItem() {
        return mCategoryItem;
    }

    /**
     * 根据索引获取子内容
     *
     * @param pPosition
     * @return
     */
    public Object getItem(int pPosition) {
        if (pPosition < 0)
            return null;
        if (pPosition == 0) {
            return getmCategoryName();
        } else {
            if (mCategoryItem.size() == 0) {
                return null;
            }
            return mCategoryItem.get(pPosition - 1).getSongInfo();
        }
    }

    /**
     * 当前类别Item总数。Category也需要占用一个Item
     *
     * @return
     */
    public int getItemCount() {
        return mCategoryItem.size() + 1;
    }

    public int getmCategoryItemCount() {
        return mCategoryItem.size();
    }

    public List<AudioInfo> getmCategoryItem() {
        return mCategoryItem;
    }

    public void setmCategoryItem(List<AudioInfo> mCategoryItem) {
        this.mCategoryItem = mCategoryItem;
    }
}