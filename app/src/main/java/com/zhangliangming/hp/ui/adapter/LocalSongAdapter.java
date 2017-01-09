package com.zhangliangming.hp.ui.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhangliangming.hp.ui.R;
import com.zhangliangming.hp.ui.common.Constants;
import com.zhangliangming.hp.ui.logger.LoggerManage;
import com.zhangliangming.hp.ui.manage.MediaManage;
import com.zhangliangming.hp.ui.model.AudioInfo;
import com.zhangliangming.hp.ui.model.Category;
import com.zhangliangming.hp.ui.model.SongInfo;
import com.zhangliangming.hp.ui.model.SongMessage;
import com.zhangliangming.hp.ui.observable.ObserverManage;
import com.zhangliangming.hp.ui.util.AudioInfoUtil;
import com.zhangliangming.hp.ui.util.DataUtil;
import com.zhangliangming.hp.ui.util.DateUtil;
import com.zhangliangming.hp.ui.widget.ListItemRelativeLayout;


public class LocalSongAdapter extends Adapter<ViewHolder> implements Observer {
    /**
     * 标题
     */
    public final static int CATEGORYTITLE = 0;
    /**
     * item
     */
    public final static int ITEM = 1;

    /**
     * 询问item
     */
    private static final int TYPE_FOOTER = 2;

    private List<Category> categorys;
    private Context context;
    /**
     *
     */
    private LoggerManage logger;

    /**
     * 播放歌曲索引
     */
    private int playIndexPosition = -1;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            SongMessage songMessage = (SongMessage) msg.obj;
            if (songMessage.getType() == SongMessage.LIKEDELMUSIC) {
                if (songMessage.getSongInfo() != null) {

                }
            } else if (songMessage.getType() == SongMessage.LOCALUNLIKEMUSIC) {
                if (songMessage.getSongInfo() != null)
                    updateSong(songMessage.getSongInfo());
            } else if (songMessage.getType() == SongMessage.INITMUSIC) {
                if (MediaManage.getMediaManage(context).getPlayListType() != MediaManage.PLAYLISTTYPE_LOCALLIST) {
                    if (playIndexPosition != -1) {
                        notifyItemChanged(playIndexPosition);
                        playIndexPosition = -1;
                    }
                } else {
                    if (playIndexPosition != -1) {
                        notifyItemChanged(playIndexPosition);
                        playIndexPosition = -1;
                    }
                    if (songMessage.getSongInfo() != null)
                        updateSong(songMessage.getSongInfo());
                }
            } else if (songMessage.getType() == SongMessage.UPDATEMUSIC) {
                if (songMessage.getSongInfo() != null)
                    updateSong(songMessage.getSongInfo());
            } else if (songMessage.getType() == SongMessage.ADDMUSIC) {
                if (songMessage.getSongInfo() != null) {
                    addMusic(songMessage.getSongInfo());
                }
            } else if (songMessage.getType() == SongMessage.SCANEDMUSIC) {
                notifyDataSetChanged();
            }
        }

    };

    /**
     * 添加歌曲
     *
     * @param songInfo
     */
    private void addMusic(SongInfo songInfo) {

        logger.i(songInfo.getDisplayName());

        if (categorys == null) {
            return;
        }
        // int count = 0;
        for (int j = 0; j < categorys.size(); j++) {
            Category category = categorys.get(j);
            char categoryChar = songInfo.getCategory().charAt(0);
            String childCategory = songInfo.getChildCategory();
            char tempCategory = category.getmCategoryName().charAt(0);
            if (tempCategory == '#') {
                tempCategory = '^';
            }
            if (categoryChar == '#') {
                categoryChar = '^';
            }

            if (categoryChar == tempCategory) {

                List<AudioInfo> lists = category.getmCategoryItem();
                if (lists.size() == 0) {

                    AudioInfo audioInfo = AudioInfoUtil.getAudioInfo(songInfo);
                    if (audioInfo != null)
                        lists.add(audioInfo);

                    if (categoryChar == '^') {
                        categoryChar = '#';
                    }
                    Category categoryTemp = new Category(categoryChar + "");
                    categoryTemp.setmCategoryItem(lists);
                    categorys.remove(j);
                    categorys.add(j, categoryTemp);


                } else {
                    for (int i = 0; i < lists.size(); i++) {
                        SongInfo tempSongInfo = lists.get(i).getSongInfo();
                        String tempChildCategory = tempSongInfo
                                .getChildCategory();

                        if (childCategory.compareTo(tempChildCategory) < 0) {

                            AudioInfo audioInfo = AudioInfoUtil.getAudioInfo(songInfo);
                            if (audioInfo != null)
                                lists.add(i, audioInfo);

                            if (categoryChar == '^') {
                                categoryChar = '#';
                            }
                            Category categoryTemp = new Category(categoryChar
                                    + "");
                            categoryTemp.setmCategoryItem(lists);
                            categorys.remove(j);
                            categorys.add(j, categoryTemp);


                            break;
                        } else if (i == lists.size() - 1) {
                            AudioInfo audioInfo = AudioInfoUtil.getAudioInfo(songInfo);
                            if (audioInfo != null)
                                lists.add(audioInfo);

                            if (categoryChar == '^') {
                                categoryChar = '#';
                            }
                            Category categoryTemp = new Category(categoryChar
                                    + "");
                            categoryTemp.setmCategoryItem(lists);
                            categorys.remove(j);
                            categorys.add(j, categoryTemp);


                            break;
                        }
                    }
                }

                break;

            } else if (categoryChar < tempCategory
                    || j == categorys.size() - 1) {

                if (categoryChar == '^') {
                    categoryChar = '#';
                }
                Category categoryTemp = new Category(categoryChar + "");
                List<AudioInfo> lists = new ArrayList<AudioInfo>();

                AudioInfo audioInfo = AudioInfoUtil.getAudioInfo(songInfo);
                if (audioInfo != null)
                    lists.add(audioInfo);
                categoryTemp.setmCategoryItem(lists);
                categorys.add(j, categoryTemp);

                break;

            }

        }


    }

    public LocalSongAdapter(Context context,
                            List<Category> localPlayListSongCategorys) {
        this.context = context;
        this.categorys = localPlayListSongCategorys;
        logger = LoggerManage.getZhangLogger(context);
        ObserverManage.getObserver().addObserver(this);
    }

    @Override
    public int getItemCount() {
        int count = 0;

        if (null != categorys) {
            // 所有分类中item的总和是ListVIew Item的总个数
            for (Category category : categorys) {
                count += category.getItemCount();
            }
        }
        // 添加了底部的菜单，所以加多一个item
        return count + 1;
    }

    /**
     * 获取有效item的个数
     *
     * @return
     */
    public int getmCategoryItemCount() {
        int count = 0;

        if (null != categorys) {
            // 所有分类中item的总和是ListVIew Item的总个数
            for (Category category : categorys) {
                count += category.getmCategoryItemCount();
            }
        }
        // 添加了底部的菜单，所以加多一个item
        return count;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (holder instanceof CategoryViewHolder) {
            CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
            String mCategoryName = (String) getItem(position);

            categoryViewHolder.getCategoryTextTextView().setText(mCategoryName);


        } else if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            SongInfo songInfo = (SongInfo) getItem(position);
            itemViewHolder.getSongNameTextView().setText(
                    songInfo.getDisplayName());

            reshViewHolder(position, itemViewHolder, songInfo);

        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            int size = getmCategoryItemCount();
            footerViewHolder.getFooterTextView().setText("共有" + size + "首歌曲");

        }
    }

    /**
     * 刷新item ui
     *
     * @param position
     * @param itemViewHolder
     * @param songInfo
     */
    private void reshViewHolder(final int position,
                                final ItemViewHolder itemViewHolder, final SongInfo songInfo) {


        // 设置播放状态
        if (MediaManage.getMediaManage(context).getPlayListType() == MediaManage.PLAYLISTTYPE_LOCALLIST
                && Constants.playInfoID.equals(songInfo.getSid())) {

            playIndexPosition = position;

            itemViewHolder.getListitemBG().setSelect(true);
            itemViewHolder.getStatus().setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.getListitemBG().setSelect(false);
            itemViewHolder.getStatus().setVisibility(View.INVISIBLE);
        }

        itemViewHolder.getListitemBG().setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        System.out.println("zhangliang setOnClickListener : " + DateUtil.dateToString(new Date()));
                        if (playIndexPosition == position) {

                            if (MediaManage.getMediaManage(context)
                                    .getPlayStatus() == MediaManage.PLAYING) {
                                // 当前正在播放，发送暂停
                                SongMessage msg = new SongMessage();
                                msg.setSongInfo(songInfo);
                                msg.setType(SongMessage.PAUSEMUSIC);
                                ObserverManage.getObserver().setMessage(msg);
                            } else {

                                SongMessage songMessage = new SongMessage();
                                songMessage.setType(SongMessage.PLAYMUSIC);
                                // 通知
                                ObserverManage.getObserver().setMessage(
                                        songMessage);
                            }

                        } else {
                            itemViewHolder.getListitemBG().setSelect(true);
                            itemViewHolder.getStatus().setVisibility(
                                    View.VISIBLE);
                            if (playIndexPosition != -1) {
                                notifyItemChanged(playIndexPosition);
                            }
                            playIndexPosition = position;

                            if (MediaManage.getMediaManage(context)
                                    .getPlayListType() != MediaManage.PLAYLISTTYPE_LOCALLIST
                                    || Constants.playInfoID.equals("")) {

                                Constants.playListType = MediaManage.PLAYLISTTYPE_LOCALLIST;
                                MediaManage
                                        .getMediaManage(context)
                                        .initPlayListData(
                                                MediaManage.PLAYLISTTYPE_LOCALLIST);
                            }
                            Constants.playInfoID = songInfo.getSid();

                            // 发送播放
                            SongMessage msg = new SongMessage();
                            msg.setSongInfo(songInfo);
                            msg.setType(SongMessage.PLAYINFOMUSIC);
                            ObserverManage.getObserver().setMessage(msg);

                            DataUtil.saveValue(context,
                                    Constants.playInfoID_KEY,
                                    Constants.playInfoID);
                        }
                    }
                });
    }

    @Override
    public void update(Observable arg0, Object data) {
        if (data instanceof SongMessage) {
            SongMessage songMessage = (SongMessage) data;

            if (songMessage.getType() == SongMessage.LIKEDELMUSIC
                    || songMessage.getType() == SongMessage.LOCALUNLIKEMUSIC
                    || songMessage.getType() == SongMessage.INITMUSIC
                    || songMessage.getType() == SongMessage.UPDATEMUSIC
                    || songMessage.getType() == SongMessage.ADDMUSIC
                    || songMessage.getType() == SongMessage.SCANEDMUSIC) {
                Message msg = new Message();
                msg.obj = songMessage;
                mHandler.sendMessage(msg);
            }

        }
    }

    /**
     * 更新歌曲信息
     *
     * @param songInfo
     */
    private void updateSong(SongInfo songInfo) {
        if (null == categorys) {
            return;
        }
        int count = 0;
        for (int i = 0; i < categorys.size(); i++) {
            Category category = categorys.get(i);


            List<AudioInfo> songInfos = category.getCategoryItem();
            int j = 0;
            for (; j < songInfos.size(); j++) {
                if (songInfos.get(j).getSongInfo().getSid().equals(songInfo.getSid())) {
                    songInfos.remove(j);

                    AudioInfo audioInfo = AudioInfoUtil.getAudioInfo(songInfo);
                    if (audioInfo != null)

                        songInfos.add(j, audioInfo);

                    categorys.remove(i);
                    category.setmCategoryItem(songInfos);
                    categorys.add(i, category);

                    reshViewHolderUI(count + j + 1);
                    // print();

                    return;
                }
            }
            count += category.getItemCount();
        }
    }

    /**
     * 刷新界面
     *
     * @param oldExpandIndex
     */
    protected void reshViewHolderUI(int oldExpandIndex) {
        this.notifyItemChanged(oldExpandIndex);
    }

    /**
     * 根据索引获取内容
     *
     * @param position
     * @return
     */
    public Object getItem(int position) {
        // 异常情况处理
        if (null == categorys || position < 0 || position > getItemCount()) {
            return null;
        }

        // 同一分类内，第一个元素的索引值
        int categroyFirstIndex = 0;

        for (Category category : categorys) {
            int size = category.getItemCount();
            // 在当前分类中的索引值
            int categoryIndex = position - categroyFirstIndex;
            // item在当前分类内
            if (categoryIndex < size) {
                return category.getItem(categoryIndex);
            }
            // 索引移动到当前分类结尾，即下一个分类第一个元素索引
            categroyFirstIndex += size;
        }

        return null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (viewType == ITEM) {

            // 加载数据item的布局，生成VH返回
            View v = LayoutInflater.from(context).inflate(
                    R.layout.localmusiclist_item, viewGroup, false);
            return new ItemViewHolder(v);

        } else if (viewType == CATEGORYTITLE) {

            // 加载数据item的布局，生成VH返回
            View v = LayoutInflater.from(context).inflate(
                    R.layout.localmusiclist_category_title, viewGroup, false);
            return new CategoryViewHolder(v);

        } else if (viewType == TYPE_FOOTER) {
            // 加载数据item的布局，生成VH返回
            View v = LayoutInflater.from(context).inflate(
                    R.layout.localmusiclist_footer, viewGroup, false);
            return new FooterViewHolder(v);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        // 异常情况处理
        if (null == categorys || position < 0 || position > getItemCount()) {
            return CATEGORYTITLE;
        }

        if (position + 1 == getItemCount())
            return TYPE_FOOTER;

        int categroyFirstIndex = 0;

        for (Category category : categorys) {
            int size = category.getItemCount();
            // 在当前分类中的索引值
            int categoryIndex = position - categroyFirstIndex;
            if (categoryIndex == 0) {
                return CATEGORYTITLE;
            }
            categroyFirstIndex += size;
        }
        return ITEM;
    }

    // 可复用的VH
    class ItemViewHolder extends ViewHolder {
        private View itemView;

        private TextView songname;
        private View lineView;

        //
        private LinearLayout localPopdownLinearLayout;

        private RelativeLayout arrowDownImageView;

        private RelativeLayout arrowUpImageView;

        private View status;

        private ListItemRelativeLayout listitemBG;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public TextView getSongNameTextView() {
            if (songname == null) {
                songname = (TextView) itemView.findViewById(R.id.songname);
            }
            return songname;
        }


        public View getStatus() {
            if (status == null) {
                status = itemView.findViewById(R.id.status);
            }
            return status;
        }

        public ListItemRelativeLayout getListitemBG() {
            if (listitemBG == null) {
                listitemBG = (ListItemRelativeLayout) itemView
                        .findViewById(R.id.listitemBG);
            }
            return listitemBG;
        }
    }

    class FooterViewHolder extends ViewHolder {
        private View itemView;
        private TextView footerTextView;
        private View lineView;

        public FooterViewHolder(View view) {
            super(view);
            this.itemView = view;
        }

        public TextView getFooterTextView() {
            if (footerTextView == null) {
                footerTextView = (TextView) itemView
                        .findViewById(R.id.list_size_text);
            }
            return footerTextView;
        }

        public View getlineView() {
            if (lineView == null) {
                lineView = itemView.findViewById(R.id.line);
            }
            return lineView;
        }
    }

    class CategoryViewHolder extends ViewHolder {
        private View itemView;
        private TextView categoryTextTextView;
        private View lineView;

        public CategoryViewHolder(View view) {
            super(view);
            this.itemView = view;
        }

        public TextView getCategoryTextTextView() {
            if (categoryTextTextView == null) {
                categoryTextTextView = (TextView) itemView
                        .findViewById(R.id.category_text);
            }
            return categoryTextTextView;
        }

        public View getlineView() {
            if (lineView == null) {
                lineView = itemView.findViewById(R.id.line);
            }
            return lineView;
        }

    }

    /**
     * 通过sid获取当前的播放索引
     *
     * @param sid
     * @return
     */
    public int getPositionForSid(String sid) {
        int index = -1;
        // 异常情况处理
        if (null == categorys) {
            return -1;
        }

        int count = 0;
        for (int i = 0; i < categorys.size(); i++) {
            Category category = categorys.get(i);


            List<AudioInfo> songInfos = category.getCategoryItem();
            int j = 0;
            for (; j < songInfos.size(); j++) {
                if (songInfos.get(j).getSongInfo().getSid().equals(sid)) {

                    index = count + j + 1;

                    break;
                }
            }
            count += category.getItemCount();
        }

        return index;
    }

    /**
     * 通过所在的索引获取item所在的位置
     *
     * @param charAt
     * @return
     */
    public int getPositionForSection(char charAt) {
        int count = 0;
        if (null != categorys) {
            for (Category category : categorys) {
                char temp = category.getmCategoryName().charAt(0);
                if (temp == charAt) {
                    return count;
                }
                count += category.getItemCount();
            }
        }
        return -1;
    }

    /**
     * 通过索引获取当前显示的所属分类组
     *
     * @param position
     * @return
     */
    public char getPositionForIndex(int position) {
        Object obj = getItem(position);
        if (obj instanceof String) {
            String mCategoryName = (String) obj;
            return mCategoryName.charAt(0);
        } else if (obj instanceof SongInfo) {
            SongInfo songInfo = (SongInfo) obj;
            return songInfo.getCategory().charAt(0);
        }
        return (char) -1;
    }
}
