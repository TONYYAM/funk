package com.yjl.funk.model;

/**
 * Created by Administrator on 2017/11/6.
 */

public class Album {

    /**
     * album_id : 564200646
     * title : 浅浅
     * publishcompany :  脸雾（北京）影视文化发展有限公司
     * country : 内地
     * songs_total : 1
     * pic_small : http://musicdata.baidu.com/data2/pic/44db67cca05e71e43dca488dd53f0626/565030017/565030017.png@s_1,w_90,h_90
     * pic_big : http://musicdata.baidu.com/data2/pic/44db67cca05e71e43dca488dd53f0626/565030017/565030017.png@s_1,w_150,h_150
     * pic_radio : http://musicdata.baidu.com/data2/pic/44db67cca05e71e43dca488dd53f0626/565030017/565030017.png@s_1,w_300,h_300
     * artist_id : 123446035
     * all_artist_id : 123446035
     * author : 周深
     * publishtime : 2017-11-06
     * resource_type_ext : 0
     * price : 0.00
     * is_recommend_mis : 0
     * is_first_publish : 0
     * is_exclusive : 0
     */

    private String album_id;
    private String title;
    private String publishcompany;
    private String country;
    private String songs_total;
    private String pic_small;
    private String pic_big;
    private String pic_radio;
    private String artist_id;
    private String all_artist_id;
    private String author;
    private String publishtime;
    private String resource_type_ext;
    private String price;
    private String is_recommend_mis;
    private String is_first_publish;
    private String is_exclusive;
    private String language;
    private String info;
    private String styles;
    private String artist_ting_uid;
    private String gender;
    private String area;
    private String hot;
    private int favorites_num;
    private int recommend_num;
    private int collect_num;
    private int share_num;
    private int comment_num;
    private String pic_s500;
    private String pic_s1000;
    private String ai_presale_flag;
    private String listen_num;
    private String buy_url;
    private int my_num;
    private int song_sale;

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishcompany() {
        return publishcompany;
    }

    public void setPublishcompany(String publishcompany) {
        this.publishcompany = publishcompany;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSongs_total() {
        return songs_total;
    }

    public void setSongs_total(String songs_total) {
        this.songs_total = songs_total;
    }

    public String getPic_small() {
        return pic_small;
    }

    public void setPic_small(String pic_small) {
        this.pic_small = pic_small;
    }

    public String getPic_big() {
        return pic_big.split("\\@")[0];
    }

    public void setPic_big(String pic_big) {
        this.pic_big = pic_big;
    }

    public String getPic_radio() {
        return pic_radio;
    }

    public void setPic_radio(String pic_radio) {
        this.pic_radio = pic_radio;
    }

    public String getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(String artist_id) {
        this.artist_id = artist_id;
    }

    public String getAll_artist_id() {
        return all_artist_id;
    }

    public void setAll_artist_id(String all_artist_id) {
        this.all_artist_id = all_artist_id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublishtime() {
        return publishtime;
    }

    public void setPublishtime(String publishtime) {
        this.publishtime = publishtime;
    }

    public String getResource_type_ext() {
        return resource_type_ext;
    }

    public void setResource_type_ext(String resource_type_ext) {
        this.resource_type_ext = resource_type_ext;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getIs_recommend_mis() {
        return is_recommend_mis;
    }

    public void setIs_recommend_mis(String is_recommend_mis) {
        this.is_recommend_mis = is_recommend_mis;
    }

    public String getIs_first_publish() {
        return is_first_publish;
    }

    public void setIs_first_publish(String is_first_publish) {
        this.is_first_publish = is_first_publish;
    }

    public String getIs_exclusive() {
        return is_exclusive;
    }

    public void setIs_exclusive(String is_exclusive) {
        this.is_exclusive = is_exclusive;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getStyles() {
        return styles;
    }

    public void setStyles(String styles) {
        this.styles = styles;
    }

    public String getArtist_ting_uid() {
        return artist_ting_uid;
    }

    public void setArtist_ting_uid(String artist_ting_uid) {
        this.artist_ting_uid = artist_ting_uid;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getHot() {
        return hot;
    }

    public void setHot(String hot) {
        this.hot = hot;
    }

    public int getFavorites_num() {
        return favorites_num;
    }

    public void setFavorites_num(int favorites_num) {
        this.favorites_num = favorites_num;
    }

    public int getRecommend_num() {
        return recommend_num;
    }

    public void setRecommend_num(int recommend_num) {
        this.recommend_num = recommend_num;
    }

    public int getCollect_num() {
        return collect_num;
    }

    public void setCollect_num(int collect_num) {
        this.collect_num = collect_num;
    }

    public int getShare_num() {
        return share_num;
    }

    public void setShare_num(int share_num) {
        this.share_num = share_num;
    }

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public String getPic_s500() {
        return pic_s500;
    }

    public void setPic_s500(String pic_s500) {
        this.pic_s500 = pic_s500;
    }

    public String getPic_s1000() {
        return pic_s1000;
    }

    public void setPic_s1000(String pic_s1000) {
        this.pic_s1000 = pic_s1000;
    }

    public String getAi_presale_flag() {
        return ai_presale_flag;
    }

    public void setAi_presale_flag(String ai_presale_flag) {
        this.ai_presale_flag = ai_presale_flag;
    }

    public String getListen_num() {
        return listen_num;
    }

    public void setListen_num(String listen_num) {
        this.listen_num = listen_num;
    }


    public String getBuy_url() {
        return buy_url;
    }

    public void setBuy_url(String buy_url) {
        this.buy_url = buy_url;
    }

    public int getMy_num() {
        return my_num;
    }

    public void setMy_num(int my_num) {
        this.my_num = my_num;
    }

    public int getSong_sale() {
        return song_sale;
    }

    public void setSong_sale(int song_sale) {
        this.song_sale = song_sale;
    }
}
