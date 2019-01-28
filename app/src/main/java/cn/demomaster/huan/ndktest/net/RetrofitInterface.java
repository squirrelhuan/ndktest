package cn.demomaster.huan.ndktest.net;


import cn.demomaster.huan.ndktest.model.CommonApi;
import cn.demomaster.huan.quickdeveloplibrary.http.URLConstant;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Squirrel桓 on 2019/1/1.
 */
public interface RetrofitInterface {

    //获取session
    @GET(URLConstant.URL_BASE)
    Observable<Object> logind();

    //获取登陆数据
    @FormUrlEncoded
    @POST("http://www.demomaster.cn/demomaster/public/index.php/index/Api/chat/login")
    Observable<CommonApi> login(@Field("username") String username, @Field("password") String password);

    //获取登陆数据
    @FormUrlEncoded
    @POST("http://www.demomaster.cn/demomaster/public/index.php/index/Api/chat/getFriendList")
    Observable<CommonApi> getFriendList(@Field("userId") String userId);


    //获取群聊
    @FormUrlEncoded
    @POST("http://www.demomaster.cn/demomaster/public/index.php/index/Api/chat/getChatGroupList")
    Observable<CommonApi> getChatGroupList(@Field("userId") String userId);

    //获取群聊
    @FormUrlEncoded
    @POST("http://www.demomaster.cn/demomaster/public/index.php/index/Api/chat/getChatRecordList")
    Observable<CommonApi> getChatRecordList(@Field("userId") String userId, @Field("sendDateTime") String sendDateTime);

    //获取群聊
    @FormUrlEncoded
    @POST("http://www.demomaster.cn/demomaster/public/index.php/index/Api/chat/deleteChatRecordList")
    Observable<CommonApi> deleteChatRecordList(@Field("userId") String userId, @Field("sendDateTime") String sendDateTime);

}
