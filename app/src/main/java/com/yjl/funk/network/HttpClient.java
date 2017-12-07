package com.yjl.funk.network;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.yjl.funk.MyApplication;
import com.yjl.funk.model.AlbumInfo;
import com.yjl.funk.model.ArtistAlbumList;
import com.yjl.funk.model.ArtistInfo;
import com.yjl.funk.model.ArtistSongList;
import com.yjl.funk.model.DownloadInfo;
import com.yjl.funk.model.Lrc;
import com.yjl.funk.model.NewAlbumListData;
import com.yjl.funk.model.OnlineSongList;
import com.yjl.funk.model.SearchResult;
import com.yjl.funk.utils.NetworkUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;



import okhttp3.Call;

/**
 * Created by hzwangchenyan on 2017/2/8.
 */
public class HttpClient {
    public static final String SPLASH_URL = "http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
    public static final String BASE_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting";
    public static final String METHOD_GET_MUSIC_LIST = "baidu.ting.billboard.billList";
    public static final String METHOD_ALBUM_DETAIL = "baidu.ting.album.getAlbumInfo";
    public static final String METHOD_MUSIC_LINK = "baidu.ting.song.play";
    public static final String METHOD_NEW_SONG = "baidu.ting.plaza.getNewSongs";
    public static final String METHOD_NEW_ALBUM = "baidu.ting.plaza.getRecommendAlbum";
    public static final String METHOD_ARTIST_INFO = "baidu.ting.artist.getInfo";
    public static final String METHOD_SEARCH_MUSIC = "baidu.ting.search.merge";
    public static final String METHOD_ARTIST_SONG = "baidu.ting.artist.getSongList";
    public static final String METHOD_ARTIST_ALBUM = "baidu.ting.artist.getAlbumList";
    public static final String METHOD_LRC = "baidu.ting.song.lry";
    public static final String PARAM_METHOD = "method";
    public static final String PARAM_TYPE = "type";
    public static final String PARAM_SIZE = "size";
    public static final String PARAM_OFFSET = "offset";
    public static final String PARAM_ALBUM_ID = "album_id";
    public static final String PARAM_SONG_ID = "songid";
    public static final String PARAM_TING_UID = "tinguid";
    public static final String PARAM_QUERY = "query";
    public static final String PARAM_LIMIT = "limit";
    public static final String PARAM_LIMITS = "limits";


    public static void downloadFile(String url, String destFileDir, String destFileName, @Nullable final HttpCallback<File> callback) {
        OkHttpUtils.get().url(url).build()
                .execute(new FileCallBack(destFileDir, destFileName) {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                    }

                    @Override
                    public void onResponse(File file, int id) {
                        if (callback != null) {
                            callback.onSuccess(file);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (callback != null) {
                            if (e instanceof SocketTimeoutException) {
                                callback.onFail(new HttpException("请求超时，请稍后再试"));
                            } else if (e instanceof ConnectException) {
                                callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                            } else {
                                if(!NetworkUtil.isNetConnect(MyApplication.getInstance())){
                                    callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                                }else {
                                    if (e instanceof HttpException){   callback.onFail((HttpException) e);}else {
                                        callback.onFail((new HttpException(e)) );
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        if (callback != null) {
                            callback.onFinish();
                        }
                    }
                });
    }

    public static void getNewAlbumList(int limit,@NonNull final HttpCallback<NewAlbumListData> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_NEW_ALBUM)
                .addParams(PARAM_LIMIT, String.valueOf(limit))
                .build()
                .execute(new JsonCallback<NewAlbumListData>(NewAlbumListData.class) {
                    @Override
                    public void onResponse(NewAlbumListData response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (callback != null) {
                            if (e instanceof SocketTimeoutException) {
                                callback.onFail(new HttpException("请求超时，请稍后再试"));
                            } else if (e instanceof ConnectException) {
                                callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                            } else {
                                if(!NetworkUtil.isNetConnect(MyApplication.getInstance())){
                                    callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                                }else {
                                    if (e instanceof HttpException){   callback.onFail((HttpException) e);}else {
                                        callback.onFail((new HttpException(e)) );
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
    public static void getNewAlbumInfo(String album_id,@NonNull final HttpCallback<AlbumInfo> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_ALBUM_DETAIL)
                .addParams(PARAM_ALBUM_ID, album_id)
                .build()
                .execute(new JsonCallback<AlbumInfo>(AlbumInfo.class) {
                    @Override
                    public void onResponse(AlbumInfo response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (callback != null) {
                            if (e instanceof SocketTimeoutException) {
                                callback.onFail(new HttpException("请求超时，请稍后再试"));
                            } else if (e instanceof ConnectException) {
                                callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                            } else {
                                if(!NetworkUtil.isNetConnect(MyApplication.getInstance())){
                                    callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                                }else {
                                    if (e instanceof HttpException){   callback.onFail((HttpException) e);}else {
                                        callback.onFail((new HttpException(e)) );
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
    public static void getNewSongList(int limit,@NonNull final HttpCallback<OnlineSongList> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_NEW_SONG)
                .addParams(PARAM_LIMIT, String.valueOf(limit))
                .build()
                .execute(new JsonCallback<OnlineSongList>(OnlineSongList.class) {
                    @Override
                    public void onResponse(OnlineSongList response, int id) {
                        if(response!=null) {
                            callback.onSuccess(response);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (callback != null) {
                            if (e instanceof SocketTimeoutException) {
                                callback.onFail(new HttpException("请求超时，请稍后再试"));
                            } else if (e instanceof ConnectException) {
                                callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                            } else {
                                if(!NetworkUtil.isNetConnect(MyApplication.getInstance())){
                                    callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                                }else {
                                    if (e instanceof HttpException){   callback.onFail((HttpException) e);}else {
                                        callback.onFail((new HttpException(e)) );
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
    public static void getSongListInfo(String type, int size, int offset, @NonNull final HttpCallback<OnlineSongList> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_GET_MUSIC_LIST)
                .addParams(PARAM_TYPE, type)
                .addParams(PARAM_SIZE, String.valueOf(size))
                .addParams(PARAM_OFFSET, String.valueOf(offset))
                .build()
                .execute(new JsonCallback<OnlineSongList>(OnlineSongList.class) {
                    @Override
                    public void onResponse(OnlineSongList response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (callback != null) {
                            if (e instanceof SocketTimeoutException) {
                                callback.onFail(new HttpException("请求超时，请稍后再试"));
                            } else if (e instanceof ConnectException) {
                                callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                            } else {
                                if(!NetworkUtil.isNetConnect(MyApplication.getInstance())){
                                    callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                                }else {
                                    if (e instanceof HttpException){   callback.onFail((HttpException) e);}else {
                                        callback.onFail((new HttpException(e)) );
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
        public static void getArtistInfo(String tingUid, @NonNull final HttpCallback<ArtistInfo> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_ARTIST_INFO)
                .addParams(PARAM_TING_UID, tingUid)
                .build()
                .execute(new JsonCallback<ArtistInfo>(ArtistInfo.class) {
                    @Override
                    public void onResponse(ArtistInfo response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (callback != null) {
                            if (e instanceof SocketTimeoutException) {
                                callback.onFail(new HttpException("请求超时，请稍后再试"));
                            } else if (e instanceof ConnectException) {
                                callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                            } else {
                                if(!NetworkUtil.isNetConnect(MyApplication.getInstance())){
                                    callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                                }else {
                                    if (e instanceof HttpException){   callback.onFail((HttpException) e);}else {
                                        callback.onFail((new HttpException(e)) );
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
    public static void getArtistSong(String tingUid,@NonNull final HttpCallback<ArtistSongList> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_ARTIST_SONG)
                .addParams(PARAM_TING_UID, tingUid)
                .addParams(PARAM_LIMITS,String.valueOf(50))
                .build()
                .execute(new JsonCallback<ArtistSongList>(ArtistSongList.class) {
                    @Override
                    public void onResponse(ArtistSongList response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (callback != null) {
                            if (e instanceof SocketTimeoutException) {
                                callback.onFail(new HttpException("请求超时，请稍后再试"));
                            } else if (e instanceof ConnectException) {
                                callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                            } else {
                                if(!NetworkUtil.isNetConnect(MyApplication.getInstance())){
                                    callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                                }else {
                                    if (e instanceof HttpException){   callback.onFail((HttpException) e);}else {
                                        callback.onFail((new HttpException(e)) );
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
    public static void getArtistAlbum(String tingUid,@NonNull final HttpCallback<ArtistAlbumList> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_ARTIST_ALBUM)
                .addParams(PARAM_TING_UID, tingUid)
                .addParams("order","1")
                .addParams(PARAM_LIMITS,String.valueOf(12))
                .build()
                .execute(new JsonCallback<ArtistAlbumList>(ArtistAlbumList.class) {
                    @Override
                    public void onResponse(ArtistAlbumList response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (callback != null) {
                            if (e instanceof SocketTimeoutException) {
                                callback.onFail(new HttpException("请求超时，请稍后再试"));
                            } else if (e instanceof ConnectException) {
                                callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                            } else {
                                if(!NetworkUtil.isNetConnect(MyApplication.getInstance())){
                                    callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                                }else {
                                    if (e instanceof HttpException){   callback.onFail((HttpException) e);}else {
                                        callback.onFail((new HttpException(e)) );
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
    public static void searchMusic(String keyword, @NonNull final HttpCallback<SearchResult> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_SEARCH_MUSIC)
                .addParams(PARAM_QUERY, keyword)
                .addParams("page_no","1")
                .addParams("page_size","30")
                .build()
                .execute(new JsonCallback<SearchResult>(SearchResult.class) {
                    @Override
                    public void onResponse(SearchResult response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (callback != null) {
                            if (e instanceof SocketTimeoutException) {
                                callback.onFail(new HttpException("请求超时，请稍后再试"));
                            } else if (e instanceof ConnectException) {
                                callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                            } else {
                                if(!NetworkUtil.isNetConnect(MyApplication.getInstance())){
                                    callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                                }else {
                                    if (e instanceof HttpException){   callback.onFail((HttpException) e);}else {
                                        callback.onFail((new HttpException(e)) );
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
    public static void getMusicDownloadInfo(String songId, @NonNull final HttpCallback<DownloadInfo> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_MUSIC_LINK)
                .addParams(PARAM_SONG_ID, songId)
                .build()
                .execute(new JsonCallback<DownloadInfo>(DownloadInfo.class) {
                    @Override
                    public void onResponse(DownloadInfo response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (callback != null) {
                            if (e instanceof SocketTimeoutException) {
                                callback.onFail(new HttpException("请求超时，请稍后再试"));
                            } else if (e instanceof ConnectException) {
                                callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                            } else {
                                if(!NetworkUtil.isNetConnect(MyApplication.getInstance())){
                                    callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                                }else {
                                    if (e instanceof HttpException){   callback.onFail((HttpException) e);}else {
                                        callback.onFail((new HttpException(e)) );
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
//
    public static void getBitmap(String url, @NonNull final HttpCallback<Bitmap> callback) {
        OkHttpUtils.get().url(url).build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onResponse(Bitmap bitmap, int id) {
                        callback.onSuccess(bitmap);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (callback != null) {
                            if (e instanceof SocketTimeoutException) {
                                callback.onFail(new HttpException("请求超时，请稍后再试"));
                            } else if (e instanceof ConnectException) {
                                callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                            } else {
                                if(!NetworkUtil.isNetConnect(MyApplication.getInstance())){
                                    callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                                }else {
                                    if (e instanceof HttpException){   callback.onFail((HttpException) e);}else {
                                        callback.onFail((new HttpException(e)) );
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getLrc(String songId, @NonNull final HttpCallback<Lrc> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_LRC)
                .addParams(PARAM_SONG_ID, songId)
                .build()
                .execute(new JsonCallback<Lrc>(Lrc.class) {
                    @Override
                    public void onResponse(Lrc response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (callback != null) {
                            if (e instanceof SocketTimeoutException) {
                                callback.onFail(new HttpException("请求超时，请稍后再试"));
                            } else if (e instanceof ConnectException) {
                                callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                            } else {
                                if(!NetworkUtil.isNetConnect(MyApplication.getInstance())){
                                    callback.onFail(new HttpException("网络无连接，请检查您的网络设置"));
                                }else {
                                    if (e instanceof HttpException){   callback.onFail((HttpException) e);}else {
                                        callback.onFail((new HttpException(e)) );
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }



}
