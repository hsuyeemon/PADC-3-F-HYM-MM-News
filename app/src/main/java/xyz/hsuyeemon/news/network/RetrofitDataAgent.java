package xyz.hsuyeemon.news.network;

/**
 * Created by Dell on 1/11/2018.
 */

        import android.content.Context;

        import com.google.gson.Gson;
        import org.greenrobot.eventbus.EventBus;

        import java.util.concurrent.TimeUnit;

        import okhttp3.OkHttpClient;
        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;
        import retrofit2.Retrofit;
        import retrofit2.converter.gson.GsonConverterFactory;
        import xyz.hsuyeemon.news.events.SuccessLoginEvent;
        import xyz.hsuyeemon.news.events.LoadedNewsEvent;
        import xyz.hsuyeemon.news.events.SuccessRegisterEvent;
        import xyz.hsuyeemon.news.network.responses.GetLoginUserResponse;
        import xyz.hsuyeemon.news.network.responses.GetNewsResponse;


public class RetrofitDataAgent implements NewsDataAgent {

    private static RetrofitDataAgent sObjInstance;

    private NewsApi mNewsApi;

    public static RetrofitDataAgent getsObjInstance() {
        if (sObjInstance==null){
            sObjInstance=new RetrofitDataAgent();
        }
        return sObjInstance;
    }

    private RetrofitDataAgent(){
        OkHttpClient httpClient = new OkHttpClient.Builder() //1.
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://padcmyanmar.com/padc-3/mm-news/apis/v1/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(httpClient)
                .build();
        mNewsApi=retrofit.create(NewsApi.class);
    }

    @Override
    public void loadNews() {


        Call<GetNewsResponse> getNewsResponseCall=mNewsApi.loadNews(1,"b002c7e1a528b7cb460933fc2875e916");
        getNewsResponseCall.enqueue(new Callback<GetNewsResponse>() {
            @Override
            public void onResponse(Call<GetNewsResponse> call, Response<GetNewsResponse> response) {
                GetNewsResponse getNewsResponse=response.body();
                if (getNewsResponse!= null) {
                    LoadedNewsEvent event = new LoadedNewsEvent(getNewsResponse.getMmNews());
                    EventBus.getDefault().post(event);
                }
            }


            @Override
            public void onFailure(Call<GetNewsResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void loadLoginUser(final Context context, String phoneNo, String password) {

        Call<GetLoginUserResponse> getLoginUserResponseCall = mNewsApi.loadLoginUser(phoneNo,password);
        getLoginUserResponseCall.enqueue(new Callback<GetLoginUserResponse>() { //response capture
            @Override
            public void onResponse(Call<GetLoginUserResponse> call, Response<GetLoginUserResponse> response) {
                GetLoginUserResponse getLoginUserResponse = response.body();
                if(getLoginUserResponse != null){
                    SuccessLoginEvent event = new SuccessLoginEvent(getLoginUserResponse.getLoginUser(),context);
                    EventBus.getDefault().post(event);
                }

            }

            @Override
            public void onFailure(Call<GetLoginUserResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void registerUser(String name, String phoneNo, String password) {

        Call<GetLoginUserResponse> getRegisterUserResponseCall = mNewsApi.registerUser(name,phoneNo,password);
        getRegisterUserResponseCall.enqueue(new Callback<GetLoginUserResponse>() {
            @Override
            public void onResponse(Call<GetLoginUserResponse> call, Response<GetLoginUserResponse> response) {
                GetLoginUserResponse getRegisterUserResponse = response.body();
                if(getRegisterUserResponse != null){
                    SuccessRegisterEvent event = new SuccessRegisterEvent(getRegisterUserResponse.getLoginUser());
                    EventBus.getDefault().post(event);
                }
            }

            @Override
            public void onFailure(Call<GetLoginUserResponse> call, Throwable t) {

            }
        });
    }
}

